package io.allteran.player;

import java.math.BigDecimal;
import java.util.UUID;

public record BalanceEvent(
        BalanceEventType type,
        BigDecimal amount,
        Long gameId,
        UUID playerId,
        UUID platformId
) {

}
