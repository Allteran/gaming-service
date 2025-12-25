package io.allteran;

import io.allteran.game.GameSessionRepository;
import io.allteran.game.GamingPlatformRepository;
import io.allteran.game.LaunchGameUseCase;
import io.allteran.game.PlayGameUseCase;
import io.allteran.player.BalanceUpdatePublishUseCase;
import io.allteran.player.BalanceUpdatePublisher;
import io.allteran.player.PlayerInfoService;
import io.allteran.security.ExternalJWTService;
import io.allteran.security.InternalJWTService;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

@Singleton
public class AppConfig {


    @Produces
    public LaunchGameUseCase launchGameUseCase(
            ExternalJWTService externalJWTService,
            GamingPlatformRepository gamingPlatformRepository,
            PlayerInfoService playerInfoService,
            GameSessionRepository gameSessionRepository,
            InternalJWTService internalJWTService) {
        return new LaunchGameUseCase(externalJWTService, gamingPlatformRepository, playerInfoService, gameSessionRepository,
                internalJWTService);
    }

    @Produces
    public BalanceUpdatePublishUseCase balanceUpdatePublishUseCase(BalanceUpdatePublisher balanceUpdatePublisher) {
        return new BalanceUpdatePublishUseCase(balanceUpdatePublisher);
    }

    @Produces
    public PlayGameUseCase playGameUseCase(GameSessionRepository gameSessionRepository, BalanceUpdatePublishUseCase balanceUpdatePublishUseCase) {
        return new PlayGameUseCase(gameSessionRepository, balanceUpdatePublishUseCase);
    }
}
