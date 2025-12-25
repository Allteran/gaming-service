package io.allteran.security;

import java.util.UUID;

public interface InternalJWTService {

    String generate(UUID playerId, UUID platformId, Long gameId);

}
