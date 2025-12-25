package io.allteran.game;

import io.allteran.game.vo.LaunchGameInput;
import io.allteran.game.vo.LaunchGameResult;
import io.allteran.game.vo.PlayGameResult;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import java.util.UUID;

@Path("/api/v1/game")
public class GameResource {
    private final LaunchGameUseCase launchGameUseCase;
    private final PlayGameUseCase playGameUseCase;

    @Inject
    public GameResource(LaunchGameUseCase launchGameUseCase, PlayGameUseCase playGameUseCase) {
        this.launchGameUseCase = launchGameUseCase;
        this.playGameUseCase = playGameUseCase;
    }

    @POST
    @Path("/{gameId}/launch")
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public LaunchGameResult launchGame(@PathParam("gameId") Long gameId, @QueryParam("token") String externalToken) {
        return launchGameUseCase.execute(new LaunchGameInput(externalToken, gameId));
    }

    @POST
    @Path("/play")
    @RolesAllowed({"USER"})
    public PlayGameResult playGame(@QueryParam("sessionId") UUID sessionId) {
        return playGameUseCase.execute(sessionId);
    }
}
