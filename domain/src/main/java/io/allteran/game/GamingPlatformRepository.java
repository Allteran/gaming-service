package io.allteran.game;

import java.util.Optional;
import java.util.UUID;

public interface GamingPlatformRepository {

    Optional<GamingPlatform> findById(UUID id);

}
