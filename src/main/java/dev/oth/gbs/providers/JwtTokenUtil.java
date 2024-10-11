package dev.oth.gbs.providers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.oth.gbs.domain.TokenDetailModel;
import dev.oth.gbs.domain.TokenModel;
import dev.oth.gbs.enums.UserRole;
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

    public TokenModel issueToken(TokenDetailModel model) {
        return new TokenModel(
                createAccessToken(model),
                createRefreshToken(model)
        );
    }
    public TokenModel reIssueToken(String token) {

//        JsonObject values = extractValue(token);
        TokenDetailModel model = extractValue(token);

        return new TokenModel(
                createAccessToken(model),
                createRefreshToken(model)
        );
    }

    public String createAccessToken(TokenDetailModel model) {
        return this.createToken(model, accessTokenValidTime);
    }

    public String createRefreshToken(TokenDetailModel model) {
        return this.createToken(model, refreshTokenValidTime);
    }

    public String createToken(TokenDetailModel model, long tokenValid) {

        JsonObject jsonObject = new Gson().toJsonTree(model).getAsJsonObject();
//        JsonObject jsonObject = new JsonObject();
//        jsonObject.addProperty("id", model.i);
//        jsonObject.addProperty("role", role.name());
//        jsonObject.addProperty("email", email);

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

    public TokenDetailModel extractValue(String token)  {
        String subject = extractAllClaims(token).getSubject();
        String decrypted = decrypt(subject);
        return new Gson().fromJson(decrypted, TokenDetailModel.class);
    }

    public boolean validateToken(String token, String userEmail) {
        try {
            TokenDetailModel tokenData = extractValue(token);
            String tokenId = tokenData.getEmail();

            // 토큰에서 추출된 사용자 이메일과 주어진 username 비교
            if (!tokenId.equals(userEmail)) {
                System.out.println("Token validation failed: Username does not match : [" + tokenId + "] : [" + userEmail + "]");
                return false;
            }

            // Claims에서 만료 시간 확인
            Claims claims = extractAllClaims(token);
            if (isTokenExpired(claims)) {
                System.out.println("Token validation failed: Token is expired");
                return false;
            }

            // 토큰이 유효하면 true 반환
            return true;
        } catch (Exception e) {
            System.out.println("Error during token validation: " + e.getMessage());
            return false;
        }
    }


    // 토큰이 만료되었는지 확인
    private boolean isTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }


}
