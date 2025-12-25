import io.smallrye.jwt.build.Jwt;
import jakarta.inject.Singleton;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Singleton
public class ExternalJwtService {

    @ConfigProperty(name = "jwt.external.private-key")
    String jwtExternalPrivateKey;

    String generateToken(UUID playerId, UUID platformId) {

        return Jwt.issuer(platformId.toString())
                .subject(playerId.toString())
                .groups(new HashSet<>(List.of("USER")))
                .expiresAt(Instant.now().plusSeconds(3600))
                .sign(jwtExternalPrivateKey);
    }
}
