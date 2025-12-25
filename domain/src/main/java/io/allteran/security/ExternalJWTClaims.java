package io.allteran.security;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record ExternalJWTClaims(
        UUID playerId,
        UUID platformId,
        Instant issuedAt,
        Instant expiresAt,
        Set<Role> roles
) {
}
