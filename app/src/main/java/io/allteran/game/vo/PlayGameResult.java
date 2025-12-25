package io.allteran.game.vo;

import io.allteran.game.GameEvent;

import java.util.UUID;

public record PlayGameResult(
        GameEvent gameEvent
) {
}
