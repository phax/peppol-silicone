package at.peppol.smp.server;

import java.net.URI;
import java.net.URISyntaxException;

import javax.annotation.Nonnull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.phloc.commons.exceptions.InitializationException;

/**
 * REST Web Service for redirection
 * 
 * @author Jerry Dimitriou
 */
@Path ("/")
public final class RedirectInterface {
  private static final URI INDEX_HTML;

  static {
    try {
      INDEX_HTML = new URI ("/web/index.html");
    }
    catch (final URISyntaxException e) {
      throw new InitializationException ("Failed to build index URI");
    }
  }

  public RedirectInterface () {}

  @GET
  @Produces (MediaType.TEXT_HTML)
  @Nonnull
  public Response displayHomeURI () {
    return Response.seeOther (INDEX_HTML).build ();
  }
}
