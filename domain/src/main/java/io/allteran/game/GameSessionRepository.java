package io.allteran.game;

import java.util.Optional;
import java.util.UUID;

public interface GameSessionRepository {
    void save(GameSession gameSession);

    Optional<GameSession> findBySessionId(UUID sessionId);
}
