package io.allteran.player;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
public class BalanceUpdateDummyPublisher implements BalanceUpdatePublisher{
    @Override
    public void publish(BalanceEvent balanceEvent) {
        log.info("Publishing balance event: {}", balanceEvent);
    }
}
