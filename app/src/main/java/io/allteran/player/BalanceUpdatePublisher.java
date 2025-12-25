package io.allteran.player;

import io.allteran.game.GameEvent;

public interface BalanceUpdatePublisher {

    void publish(BalanceEvent balanceEvent);
}
