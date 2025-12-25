package io.allteran.player;

import java.util.UUID;

public record Player(
        UUID id,
        UUID platformId,
        Balance balance
) {
}
