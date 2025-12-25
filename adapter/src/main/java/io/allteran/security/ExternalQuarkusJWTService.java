package io.allteran.security;

import io.smallrye.jwt.auth.principal.JWTAuthContextInfo;
import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.jwt.auth.principal.ParseException;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Singleton
public class ExternalQuarkusJWTService implements ExternalJWTService{
    final JWTParser jwtParser;

    @ConfigProperty(name = "jwt.external.public-key")
    String publicKeyLocation;

    @Inject
    public ExternalQuarkusJWTService(JWTParser jwtParser) {
        this.jwtParser = jwtParser;
    }

    @Override
    public ExternalJWTClaims decode(String token) throws TokenParseException {
        try {
            PublicKey publicKey = PemUtils.loadPublicKey(publicKeyLocation);

            JWTAuthContextInfo ctx = new JWTAuthContextInfo();
            ctx.setPublicVerificationKey(publicKey);
            ctx.setIssuedBy(null);
            JsonWebToken decrypted = jwtParser.parse(token, ctx);

            UUID playerId = UUID.fromString(decrypted.getClaim(Claims.sub));
            UUID platformId = UUID.fromString(decrypted.getClaim(Claims.iss));
            Long issuedAt = decrypted.getClaim(Claims.iat);
            Long expirationTime = decrypted.getClaim(Claims.exp);
            Set<Role> roles = decrypted.getClaim(Claims.groups);

            return new ExternalJWTClaims(
                    playerId,
                    platformId,
                    Instant.ofEpochSecond(issuedAt),
                    Instant.ofEpochSecond(expirationTime),
                    roles
            );
        } catch (ParseException | NoSuchAlgorithmException | IOException | InvalidKeySpecException e) {
            log.error("Failed to decrypt JWT", e);
            throw new TokenParseException("Failed to decrypt JWT");
        }
    }

    @Override
    public boolean validate(String token) {
        try {
            ExternalJWTClaims decode = decode(token);
            return decode.expiresAt().isAfter(Instant.now());
        } catch (TokenParseException e) {
            return false;
        }
    }


}
