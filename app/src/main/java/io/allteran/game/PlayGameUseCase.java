package io.allteran.game;

import io.allteran.game.vo.PlayGameResult;
import io.allteran.player.BalanceUpdatePublishUseCase;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.Random;
import java.util.UUID;

@RequiredArgsConstructor
public class PlayGameUseCase {
    private final GameSessionRepository gameSessionRepository;
    private final BalanceUpdatePublishUseCase balanceUpdatePublishUseCase;

    PlayGameResult execute(UUID sessionId) {
        GameSession gameSession = gameSessionRepository.findBySessionId(sessionId).orElseThrow(() ->
                new GameSessionNotFoundException("Can't find game session with id: " + sessionId));

        Random random = new Random();
        GameEventType eventType = random.nextBoolean()
                ? GameEventType.WIN
                : GameEventType.LOSE;

        GameEvent gameEvent = new GameEvent(
                UUID.randomUUID(),
                sessionId,
                gameSession.gameId(),
                gameSession.playerId(),
                gameSession.platformId(),
                BigDecimal.valueOf(100),
                eventType
        );

        balanceUpdatePublishUseCase.execute(gameEvent);

        return new PlayGameResult(gameEvent);
    }
}
