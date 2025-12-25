package io.allteran.game;

import java.math.BigDecimal;
import java.util.UUID;

public record GameEvent(
        UUID id,
        UUID sessionId,
        Long gameId,
        UUID playerId,
        UUID platformId,
        BigDecimal amount,
        GameEventType type) {

}
