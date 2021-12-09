package fr.techad.edc.httpd.utils;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

public class TokenUtils {
	static final Logger LOGGER = LoggerFactory.getLogger(TokenUtils.class);
	final String SECRET = "edc-server";
	final String AUTH = "edc";

	private static TokenUtils instance;
	private String privateKey;
	private FileUtils fileUtils;
	private final String tokenPath = "./token.info";
	private final String keyPath = "./private.key";

	private TokenUtils() {
		this.fileUtils = FileUtils.getInstance();
		createTokenFile();
	}

	public static synchronized TokenUtils getInstance() {
		if (instance == null)
			instance = new TokenUtils();
		return instance;
	}

	String genSecretKey() {
		return RandomStringUtils.randomAlphanumeric(24);
	}

	public boolean validateToken(String token) {
		if(readPrivateKey().isEmpty()|| this.privateKey==null)return false;
		else {
			try {
				Algorithm algorithm = Algorithm.HMAC256(SECRET);
				JWTVerifier verifier = JWT.require(algorithm).withClaim("private", this.privateKey).withIssuer(AUTH)
						.build();
				DecodedJWT decJwt = verifier.verify(token);
				return true;

			} catch (JWTVerificationException exception) {
				return false;
			}
		}
	}

	private String readPrivateKey() {
		return fileUtils.readFile(keyPath);
	}

	private void createTokenFile() {
		String token = null;
		String tempKey = readPrivateKey();
		if (tempKey.isEmpty()) {
			this.privateKey = genSecretKey();
			fileUtils.writeFile(keyPath, privateKey);
		} else {
			this.privateKey = tempKey;
		}
		try {
			Algorithm algorithm = Algorithm.HMAC256(SECRET);
			token = JWT.create().withClaim("private", this.privateKey).withIssuer(AUTH).sign(algorithm);
		} catch (JWTCreationException exception) {
			LOGGER.error("Error during creating token", exception);
		}

		fileUtils.writeFile(tokenPath, token);
	}
}
