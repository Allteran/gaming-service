package io.allteran;

import io.allteran.game.GameNotAllowed;
import io.allteran.game.GameSessionNotFoundException;
import io.allteran.game.GamingPlatformBlockedException;
import io.allteran.game.GamingPlatformNotFoundException;
import io.allteran.security.TokenInvalidException;
import io.allteran.security.TokenParseException;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

@Slf4j
@ApplicationScoped
public class RestErrorMapper {

    @ServerExceptionMapper
    public RestResponse<String> mapApiException(Throwable ex) {
        log.info("Error occurred", ex);
        log.error("Error occurred: {}", ex.getMessage());
        return switch (ex) {
            case GameNotAllowed e -> RestResponse.status(RestResponse.Status.FORBIDDEN, e.getMessage());
            case GameSessionNotFoundException e -> RestResponse.status(RestResponse.Status.NOT_FOUND, e.getMessage());
            case GamingPlatformBlockedException e ->
                    RestResponse.status(RestResponse.Status.UNAUTHORIZED, e.getMessage());
            case GamingPlatformNotFoundException e ->
                    RestResponse.status(RestResponse.Status.UNAUTHORIZED, e.getMessage());
            case TokenInvalidException e -> RestResponse.status(RestResponse.Status.UNAUTHORIZED, e.getMessage());
            case TokenParseException e -> RestResponse.status(RestResponse.Status.UNAUTHORIZED, e.getMessage());
            default -> RestResponse.status(RestResponse.Status.INTERNAL_SERVER_ERROR, ex.getMessage());
        };
    }

}