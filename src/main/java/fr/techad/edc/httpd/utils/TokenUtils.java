package fr.techad.edc.httpd.utils;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderValues;

public class TokenUtils {
  static final Logger LOGGER = LoggerFactory.getLogger(TokenUtils.class);
  final String SECRET = "edc-server";
  final String AUTH = "edc";

  private static TokenUtils instance;
  private String privateKey;
  private final String tokenPath = "./token.info";
  private final String keyPath = "./private.key";

  private TokenUtils() {
  }

  public static synchronized TokenUtils getInstance() {
    if (instance == null)
      instance = new TokenUtils();
    return instance;
  }

  public boolean getTokenInHeader(HttpServerExchange exchange) throws IOException {
    Optional<HeaderValues> headerValues = Optional.ofNullable(exchange.getRequestHeaders().get("Edc-Token"));
    String token;
    if (headerValues.isPresent())
      token = headerValues.get().getFirst();
    else
      token = "";
    return StringUtils.isNoneBlank(token) && validateToken(token);
  }

  String genSecretKey() {
    return RandomStringUtils.randomAlphanumeric(24);
  }

  public boolean validateToken(String token) throws IOException {
    if (readPrivateKey().isEmpty() || this.privateKey == null)
      return false;
    else {
      try {
        Algorithm algorithm = Algorithm.HMAC256(SECRET);
        JWTVerifier verifier = JWT.require(algorithm).withClaim("private", this.privateKey).withIssuer(AUTH).build();
        DecodedJWT decJwt = verifier.verify(token);
        return true;
      } catch (JWTVerificationException exception) {
        return false;
      }
    }
  }

  private String readPrivateKey() throws IOException {
    return FileUtils.readFileToString(new File(keyPath), "UTF-8");
  }

  public void createTokenFile() throws IOException {
    String token = null;
    String tempKey = readPrivateKey();
    if (tempKey.isEmpty()) {
      this.privateKey = genSecretKey();
      FileUtils.writeStringToFile(new File(keyPath), privateKey, "UTF-8");
    } else {
      this.privateKey = tempKey;
    }
    try {
      Algorithm algorithm = Algorithm.HMAC256(SECRET);
      token = JWT.create().withClaim("private", this.privateKey).withIssuer(AUTH).sign(algorithm);
    } catch (JWTCreationException exception) {
      LOGGER.error("Error during creating token", exception);
    }
    FileUtils.writeStringToFile(new File(tokenPath), token, "UTF-8");
  }
}
