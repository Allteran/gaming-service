package io.allteran.security;


public interface ExternalJWTService {

    ExternalJWTClaims decode(String token) throws TokenParseException;

    boolean validate(String token) throws TokenParseException;
}
