import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.UUID;

@Path("/api/platform")
public class PlatformResource {
    private final ExternalJwtService externalJwtService;

    @Inject
    public PlatformResource(ExternalJwtService externalJwtService) {
        this.externalJwtService = externalJwtService;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @RunOnVirtualThread
    public Object generateJwt(GenerateJWTRequest request) {
        System.out.println("Request received: " + request);
        return externalJwtService.generateToken(UUID.fromString(request.playerId()),
                UUID.fromString(request.platformId()));
    }
}
