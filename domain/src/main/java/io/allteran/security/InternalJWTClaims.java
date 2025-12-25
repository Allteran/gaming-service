package io.allteran.security;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record InternalJWTClaims(
        UUID playerId,
        UUID platformId,
        Long gameId,
        Instant issuedAt,
        Instant expiresAt,
        Set<Role> roles
) {
}
