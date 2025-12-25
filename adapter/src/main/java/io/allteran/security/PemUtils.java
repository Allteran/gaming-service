package io.allteran.security;

import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@UtilityClass
public class PemUtils {

    public static PublicKey loadPublicKey(String fileName) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName)) {
            if (is == null) {
                throw new RuntimeException("Can't find public key file: " + fileName);
            }

            String pem = new String(is.readAllBytes(), StandardCharsets.UTF_8);

            String publicKeyPEM = pem
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replaceAll(System.lineSeparator(), "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .trim();

            byte[] encoded = Base64.getDecoder().decode(publicKeyPEM);

            X509EncodedKeySpec spec = new X509EncodedKeySpec(encoded);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePublic(spec);
        }
    }
}
