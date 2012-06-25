package eu.sendregning.oxalis;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ServiceLoader;

import org.junit.Ignore;
import org.junit.Test;

import eu.peppol.start.persistence.MessageRepository;

/**
 * Experiment to determine whether it is possible to load an external service in
 * a separate class loader.
 * 
 * @author Steinar Overbeck Cook
 *         <p/>
 *         Created by User: steinar Date: 28.12.11 Time: 13:47
 */
public class ClassLoaderTest {

  @Test
  @Ignore
  public void loadClass () throws MalformedURLException {

    final File jarfile = new File ("/Users/steinar/src/sr-peppol/oxalis-persistence/target/oxalis-persistence-1.0-SNAPSHOT.jar");

    assertTrue ("Jar file does not exist " + jarfile.toString (), jarfile.exists ());
    final URL [] urls = { jarfile.toURI ().toURL () };

    final URLClassLoader urlClassLoader = new URLClassLoader (urls, ClassLoaderTest.class.getClassLoader ());

    final InputStream is = urlClassLoader.getResourceAsStream ("META-INF/services/eu.peppol.start.persistence.MessageRepository");
    assertNotNull (is);

    final ServiceLoader <MessageRepository> messageRepositoryServiceLoader = ServiceLoader.load (MessageRepository.class,
                                                                                                 urlClassLoader);
    for (final MessageRepository repository : messageRepositoryServiceLoader) {
      if (repository != null) {}
    }
  }
}
