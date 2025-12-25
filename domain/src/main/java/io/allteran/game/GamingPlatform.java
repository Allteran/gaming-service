package io.allteran.game;

import java.util.Set;
import java.util.UUID;

public record GamingPlatform(
        UUID id,
        String name,
        Set<Game> allowedGames,
        Boolean active
) {
}
