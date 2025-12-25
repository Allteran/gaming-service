package io.allteran.game;

import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;

@ApplicationScoped
public class GameSessionInMemoryRepository implements GameSessionRepository{
    private final CopyOnWriteArraySet<GameSession> database = new CopyOnWriteArraySet<>();

    @Override
    public void save(GameSession gameSession) {
        database.add(gameSession);
    }

    @Override
    public Optional<GameSession> findBySessionId(UUID sessionId) {
        return database.stream().filter(s -> s.sessionId().equals(sessionId)).findFirst();
    }
}
