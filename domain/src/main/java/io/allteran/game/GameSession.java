package io.allteran.game;

import java.util.UUID;

public record GameSession(
        UUID sessionId,
        Long gameId,
        UUID playerId,
        UUID platformId
) {
}
