package io.allteran.game;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

@ApplicationScoped
public class GamingPlatformInMemoryRepository implements GamingPlatformRepository {

    private final CopyOnWriteArraySet<GamingPlatform> database = new CopyOnWriteArraySet<>(List.of(
            new GamingPlatform(UUID.fromString("6fe96fed-8998-46f8-a2f8-3234f90df452"), "JoyCasino",
                    Set.of(
                            new Game(100L, "IndianaJohnSlits"),
                            new Game(101L, "Roller"),
                            new Game(102L, "CoolBall")), true),
            new GamingPlatform(UUID.fromString("0af4aced-2386-4f92-91b7-71cf6d070eec"), "FreeBetCasino",
                    Set.of(
                            new Game(100L, "IndianaJohnSlits"),
                            new Game(102L, "CoolBall")), true),
            new GamingPlatform(UUID.fromString("20652211-babd-4994-b3b7-101da062d463"), "BlockedCasino",
                    Set.of(
                            new Game(100L, "IndianaJohnSlits"),
                            new Game(101L, "Roller"),
                            new Game(102L, "CoolBall")), false)
    ));


    @Override
    public Optional<GamingPlatform> findById(UUID id) {
        return database.stream().filter(p -> p.id().equals(id)).findFirst();
    }
}
