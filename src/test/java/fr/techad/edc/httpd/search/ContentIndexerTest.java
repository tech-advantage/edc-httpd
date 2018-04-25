package fr.techad.edc.httpd.search;

import fr.techad.edc.httpd.WebServerConfig;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;

/**
 * TECH ADVANTAGE
 * All right reserved
 * Created by cochon on 22/03/2018.
 */
public class ContentIndexerTest {

    @Test
    public void shouldIndexDocumentation() throws IOException {
        WebServerConfig webServerConfigMock = Mockito.mock(WebServerConfig.class);
        File file = new File("src/test/resources/edc-doc");
        Mockito.when(webServerConfigMock.getBase()).thenReturn(file.getAbsolutePath());
        ContentIndexer contentIndexer = new ContentIndexer(webServerConfigMock);
        long number = contentIndexer.index();
        Assert.assertEquals(32, number);
    }
}