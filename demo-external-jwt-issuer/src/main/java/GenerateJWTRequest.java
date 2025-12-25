import java.util.UUID;

public record GenerateJWTRequest(
        String playerId,
        String platformId
) {
}
