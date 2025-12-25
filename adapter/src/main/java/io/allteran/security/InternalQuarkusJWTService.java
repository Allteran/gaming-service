package io.allteran.security;

import io.smallrye.jwt.build.Jwt;
import jakarta.inject.Singleton;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Singleton
public class InternalQuarkusJWTService implements InternalJWTService{

    @ConfigProperty(name = "mp.jwt.verify.issuer")
    String issuer;

    private static final String CLAIM_PLATFORM_ID = "platformId";
    private static final String CLAIM_GAME_ID = "gameId";

    @Override
    public String generate(UUID playerId, UUID platformId, Long gameId) {
        return Jwt.issuer(issuer)
                .subject(playerId.toString()) //id
                .claim(CLAIM_PLATFORM_ID, platformId.toString())
                .claim(CLAIM_GAME_ID, gameId)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .groups(new HashSet<>(List.of("USER")))
                .sign();
    }

}
