package fr.techad.edc.httpd.utils;

import org.junit.Assert;
import org.junit.Test;

public class TokenUtilsTest {
	private final String tokenPath = "./token.info";
	TokenUtils tutil = TokenUtils.getInstance();
	@Test
	public void keyCreationLength() {
		Assert.assertEquals(24, tutil.genSecretKey().length());
	}

	@Test
	public void keyCreationBlank() {
		Assert.assertFalse(tutil.genSecretKey().isBlank());
	}

	@Test
	public void keyCreationEmpty() {
		Assert.assertFalse(tutil.genSecretKey().isEmpty());
	}

	@Test
	public void tokenValidationEmptyTest() {
		Assert.assertThrows(NullPointerException.class, () -> tutil.validateToken(null));
	}

	@Test
	public void tokenValidationBlankTest() {
		Assert.assertFalse(tutil.validateToken(""));
	}

	@Test
	public void tokenValidationSucess() {
		FileUtils futil = FileUtils.getInstance();
		Assert.assertTrue(tutil.validateToken(futil.readFile(tokenPath)));
	}

	@Test
	public void tokenValidationFailure() {
		Assert.assertFalse(tutil.validateToken("a fake token"));
	}

}
