package io.allteran.game.vo;

import java.util.UUID;

public record LaunchGameResult(
        String internalToken,
        UUID sessionId
) {
}
