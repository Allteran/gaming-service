package io.allteran.player;

import java.util.UUID;

public interface PlayerInfoService {

    Player pull(UUID playerId);
}
