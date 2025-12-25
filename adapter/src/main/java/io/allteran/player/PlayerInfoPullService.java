package io.allteran.player;

import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;

@ApplicationScoped
public class PlayerInfoPullService implements PlayerInfoService {
    private final CopyOnWriteArraySet<Player> database = new CopyOnWriteArraySet<>(Set.of(
            new Player(UUID.fromString("c8185ca2-9975-4ef6-bc5e-83c3ec3b4f3c"), UUID.fromString("6fe96fed-8998-46f8-a2f8-3234f90df452"),
                    new Balance("USD", BigDecimal.valueOf(1000L))),
            new Player(UUID.fromString("e1cc0e1a-c785-4631-b1ab-21162ee05ad9"), UUID.fromString("0af4aced-2386-4f92-91b7-71cf6d070eec"),
                    new Balance("USD", BigDecimal.valueOf(100L))),
            new Player(UUID.fromString("92445565-ab78-4add-819c-33539ae476d4"), UUID.fromString("20652211-babd-4994-b3b7-101da062d463"),
                    new Balance("EUR", BigDecimal.valueOf(2L)))));

    @Override
    public Player pull(UUID playerId) {
        return database.stream()
                .filter(player -> player.id().equals(playerId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Player with id: " + playerId + " not found"));
    }
}
