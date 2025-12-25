package io.allteran.game;

import io.allteran.game.vo.LaunchGameInput;
import io.allteran.game.vo.LaunchGameResult;
import io.allteran.player.Player;
import io.allteran.player.PlayerInfoService;
import io.allteran.security.ExternalJWTClaims;
import io.allteran.security.ExternalJWTService;
import io.allteran.security.InternalJWTService;
import io.allteran.security.TokenInvalidException;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class LaunchGameUseCase {
    private final ExternalJWTService externalJWTService;
    private final GamingPlatformRepository gamingPlatformRepository;
    private final PlayerInfoService playerInfoService;
    private final GameSessionRepository gameSessionRepository;
    private final InternalJWTService internalJWTService;

    LaunchGameResult execute(LaunchGameInput input) {
//        4. game-serivce (our backend) decodes JWT and using encoded data checks the database for Platform and does it allow to play particular game.
        boolean isExternalTokenValid = externalJWTService.validate(input.externalToken());
        if (!isExternalTokenValid) {
//        4.3. In case of invalid JWT we return 403.
            throw new TokenInvalidException("Invalid external token");
        }

        ExternalJWTClaims externalClaims = externalJWTService.decode(input.externalToken());
//        4.2. In case if we do not have info about such platform - we return 401 error
        GamingPlatform platformInfo = gamingPlatformRepository.findById(externalClaims.platformId())
                .orElseThrow(() -> new GamingPlatformNotFoundException("Platform not found by id: " + externalClaims.platformId()));

//        4.1. In case if platform was blocked on our side - we return 403 error
        if (!platformInfo.active()) {
            throw new GamingPlatformBlockedException("Platform with id " + platformInfo.id() + " is blocked");
        }

        if(platformInfo.allowedGames().stream().noneMatch(game -> game.id().equals(input.gameId()))) {
            throw new GameNotAllowed("Game with id" + input.gameId() + " is not allowed for platform with id:" + platformInfo.id());
        }

//        5. In case if everything is OK game-service make request to the Platform's backend to identify player and get player's info: balance, currency, etc.
        Player playerInfo = playerInfoService.pull(externalClaims.playerId());
//        5.1. If Platform returns 404 or any other error - we propagate it as 401 from our side.
//        6. After retrieving player's info game-service generates game session and stores it inside NoSQL database.
        GameSession gameSession = buildGameSession(input.gameId(), playerInfo.id(), platformInfo.id());
        gameSessionRepository.save(gameSession);
//        7. Game-service generates internal JWT which will be using only on our side by player and returns sessionId and JWT to the frontend.

        String internalToken = internalJWTService.generate(playerInfo.id(), platformInfo.id(), input.gameId());
        return new LaunchGameResult(internalToken, gameSession.sessionId());
    }

    private static GameSession buildGameSession(Long gameId, UUID playerId, UUID platformId) {
        return new GameSession(UUID.randomUUID(), gameId, playerId, platformId);
    }

}
