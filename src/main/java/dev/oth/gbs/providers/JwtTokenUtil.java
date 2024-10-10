package dev.oth.gbs.providers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.oth.gbs.domain.TokenModel;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import lombok.SneakyThrows;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.JwtParser;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

@Component
public class JwtTokenUtil {

    // 비밀키는 256비트 이상이어야 하므로 길이를 늘림
    @Value("${jwt.secretKey}")
    private String secretKey;

    @Value("${jwt.accessExpiration}")
    private long accessTokenValidTime;

    @Value("${jwt.refreshExpiration}")
    private long refreshTokenValidTime;

    @Value("${jwt.aesKey}")
    private String aesKey;

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(this.secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public TokenModel issueToken(Long id, String email) {
        return new TokenModel(
                createAccessToken(id, email),
                createRefreshToken(id, email)
        );
    }
    public TokenModel reIssueToken(String token) {

        JsonObject values = extractValue(token);

        return new TokenModel(
                createAccessToken(values.get("id").getAsLong(), values.get("email").getAsString()),
                createRefreshToken(values.get("id").getAsLong(), values.get("email").getAsString())
        );
    }

    public String createAccessToken(Long id, String email) {
        return this.createToken(id, email, accessTokenValidTime);
    }

    public String createRefreshToken(Long id, String email) {
        return this.createToken(id, email, refreshTokenValidTime);
    }

    public String createToken(Long id, String email, long tokenValid) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", id);
        jsonObject.addProperty("email", email);

        Claims claims = Jwts.claims().subject(encrypt(jsonObject.toString())).build();
        Date date = new Date();

        return Jwts.builder()
                .claims(claims)
                .issuedAt(date)
                .expiration(new Date(date.getTime() + tokenValid))
                .signWith(getSigningKey())
                .compact();
    }


    @SneakyThrows
    private String encrypt(String plainToken) {
        SecretKeySpec secretKeySpec = new SecretKeySpec(aesKey.getBytes(StandardCharsets.UTF_8), "AES");
        IvParameterSpec IV = new IvParameterSpec(aesKey.substring(0, 16).getBytes());

        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        c.init(Cipher.ENCRYPT_MODE, secretKeySpec, IV);

        byte[] encryptionByte = c.doFinal(plainToken.getBytes(StandardCharsets.UTF_8));

        return Hex.encodeHexString(encryptionByte);
    }

    @SneakyThrows
    private String decrypt(String encodeText) {
        SecretKeySpec secretKeySpec = new SecretKeySpec(aesKey.getBytes(StandardCharsets.UTF_8), "AES");
        IvParameterSpec IV = new IvParameterSpec(aesKey.substring(0, 16).getBytes());

        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        c.init(Cipher.DECRYPT_MODE, secretKeySpec, IV);

        byte[] decodeByte = Hex.decodeHex(encodeText);

        return new String(c.doFinal(decodeByte), StandardCharsets.UTF_8);

    }

    private Claims extractAllClaims(String token) {
        return getParser()
                .parseSignedClaims(token)
                .getPayload();
    }

    private JwtParser getParser() {
        return Jwts.parser()
                .verifyWith(this.getSigningKey())
                .build();
    }

    public JsonObject extractValue(String token)  {
        String subject = extractAllClaims(token).getSubject();
        String decrypted = decrypt(subject);
        return new Gson().fromJson(decrypted, JsonObject.class);
    }

    // validateToken 메서드 추가
    public boolean validateToken(String token, String username) {
        try {
            JsonObject tokenData = extractValue(token);
            String tokenUsername = tokenData.get("id").getAsString();
            Claims claims = extractAllClaims(token);

            // 토큰의 사용자 정보가 일치하고, 토큰이 만료되지 않았는지 확인
            return (tokenUsername.equals(username) && !isTokenExpired(claims));
        } catch (Exception e) {
            return false;  // 파싱 중 예외가 발생하면 토큰을 유효하지 않다고 판단
        }
    }

    // 토큰이 만료되었는지 확인
    private boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }


}
