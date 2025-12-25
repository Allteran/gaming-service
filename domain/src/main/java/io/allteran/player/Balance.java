package io.allteran.player;

import java.math.BigDecimal;

public record Balance(
        String currency,
        BigDecimal amount
) {
}
