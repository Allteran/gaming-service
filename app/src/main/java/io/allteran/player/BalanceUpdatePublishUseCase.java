package io.allteran.player;

import io.allteran.game.GameEvent;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BalanceUpdatePublishUseCase {
    private final BalanceUpdatePublisher balanceUpdatePublisher;

    public void execute(GameEvent gameEvent) {
        balanceUpdatePublisher.publish(new BalanceEvent(
                defineEventType(gameEvent),
                gameEvent.amount(),
                gameEvent.gameId(),
                gameEvent.playerId(),
                gameEvent.platformId()
        ));
    }

    private static BalanceEventType defineEventType(GameEvent event) {
        return switch (event.type()) {
            case WIN -> BalanceEventType.ADD;
            case LOSE -> BalanceEventType.SUBTRACT;
        };
    }
}
