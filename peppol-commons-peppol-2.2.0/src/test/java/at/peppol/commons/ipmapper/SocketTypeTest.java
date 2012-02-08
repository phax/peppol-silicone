/**
 * Version: MPL 1.1/EUPL 1.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at:
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Copyright The PEPPOL project (http://www.peppol.eu)
 *
 * Alternatively, the contents of this file may be used under the
 * terms of the EUPL, Version 1.1 or - as soon they will be approved
 * by the European Commission - subsequent versions of the EUPL
 * (the "Licence"); You may not use this work except in compliance
 * with the Licence.
 * You may obtain a copy of the Licence at:
 * http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 *
 * If you wish to allow use of your version of this file only
 * under the terms of the EUPL License and not to allow others to use
 * your version of this file under the MPL, indicate your decision by
 * deleting the provisions above and replace them with the notice and
 * other provisions required by the EUPL License. If you do not delete
 * the provisions above, a recipient may use your version of this file
 * under either the MPL or the EUPL License.
 */
package at.peppol.commons.ipmapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Test;

import at.peppol.commons.ipmapper.ISocketType;
import at.peppol.commons.ipmapper.SocketType;


/**
 * Test class for class {@link SocketType}.
 *
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public final class SocketTypeTest {
  @Test
  public void testCtor () {
    SocketType aST = new SocketType ("myhost");
    assertEquals ("myhost", aST.getHost ());
    assertNull (aST.getPort ());
    assertEquals ("myhost", aST.getSocketString ());

    aST = new SocketType ("myhost", 15);
    assertEquals ("myhost", aST.getHost ());
    assertEquals (15, aST.getPort ().intValue ());
    assertEquals ("myhost:15", aST.getSocketString ());

    aST = new SocketType ("myhost", Integer.valueOf (27));
    assertEquals ("myhost", aST.getHost ());
    assertEquals (27, aST.getPort ().intValue ());
    assertEquals ("myhost:27", aST.getSocketString ());
  }

  @Test
  public void testCreateSocketType () {
    // Create with port
    ISocketType aST = SocketType.createSocketType ("myhost:67");
    assertNotNull (aST);
    assertEquals ("myhost", aST.getHost ());
    assertEquals (67, aST.getPort ().intValue ());
    assertEquals ("myhost:67", aST.getSocketString ());

    // Check if bidirectional conversion works
    assertEquals (aST, SocketType.createSocketType (aST.getSocketString ()));

    // Create without port
    aST = SocketType.createSocketType ("myhost67");
    assertNotNull (aST);
    assertEquals ("myhost67", aST.getHost ());
    assertNull (aST.getPort ());
    assertEquals ("myhost67", aST.getSocketString ());

    aST = SocketType.createSocketType ("www.chello.at:80");
    assertEquals ("www.chello.at", aST.getHost ());
    assertEquals (80, aST.getPort ().intValue ());

    // Create with weird syntax
    try {
      SocketType.createSocketType ("myhost:69:test");
      fail ();
    }
    catch (final IllegalArgumentException ex) {
      // expected
    }

    try {
      SocketType.createSocketType ("");
      fail ();
    }
    catch (final IllegalArgumentException ex) {
      // expected
    }
  }

  /**
   * Test method for {@link at.peppol.commons.ipmapper.SocketType#hashCode()}.
   */
  @Test
  public void testHashCode () {
    final SocketType st1 = SocketType.createSocketType ("1.1.1.1:28080");
    SocketType st2 = SocketType.createSocketType ("1.1.1.1:28080");
    assertEquals (st1.hashCode (), st2.hashCode ());
    st2 = SocketType.createSocketType ("1.1.1.1");
    assertEquals ("1.1.1.1".hashCode (), st2.getSocketString ().hashCode ());
    assertFalse (st1.hashCode () == st2.hashCode ());
    st2 = SocketType.createSocketType ("1.1.1.:28080");
    assertFalse (st1.hashCode () == st2.hashCode ());
    st2 = SocketType.createSocketType ("1.1.1.1:28080");
    assertEquals (st1.hashCode (), st2.hashCode ());
  }

  /**
   * Test method for
   * {@link at.peppol.commons.ipmapper.SocketType#createSocketType(java.lang.String)}
   * .
   *
   * @throws UnknownHostException
   */
  @Test
  public void testCreateSocketTypeWithDnsResolution () throws UnknownHostException {
    final String hostName = InetAddress.getByName ("www.chello.at").getHostName ();
    final SocketType st = SocketType.createSocketType (hostName);
    assertEquals ("www.chello.at", st.getHost ());
    assertNull (st.getPort ());
  }

  /**
   * Test method for
   * {@link at.peppol.commons.ipmapper.SocketType#equals(java.lang.Object)}.
   */
  @Test
  public void testEqualsObject () {
    final SocketType st1 = SocketType.createSocketType ("1.1.1.1:28080");
    SocketType st2 = SocketType.createSocketType ("1.1.1.1:28080");
    assertEquals (st1.hashCode (), st2.hashCode ());
    st2 = SocketType.createSocketType ("1.1.1.1");
    assertFalse (st1.equals (st2));
    assertEquals ("1.1.1.1", st2.getSocketString ());
    st2 = SocketType.createSocketType ("1.1.1.:28080");
    assertFalse (st1.equals (st2));
    st2 = SocketType.createSocketType ("1.1.1.1:28080");
    assertEquals (st1, st2);
  }

  /**
   * Test method for
   * {@link at.peppol.commons.ipmapper.SocketType#SocketType(java.lang.String, int)}
   * .
   */
  @Test
  public void testSocketTypeStringInt () {
    final SocketType st = new SocketType ("www.chello.at", 80);
    final SocketType st1 = SocketType.createSocketType ("www.chello.at:80");
    assertEquals (st, st1);
  }

  /**
   * Test method for {@link at.peppol.commons.ipmapper.SocketType#getHost()}.
   */
  @Test
  public void testGetHost () {
    final SocketType st = new SocketType ("www.chello.at", 80);
    final SocketType st1 = new SocketType ("www.chello.at", 80);
    final SocketType st2 = new SocketType ("www.chello.at", 1);
    assertEquals (st, st1);
    assertFalse (st.equals (st2));
    assertEquals (st.getHost (), st1.getHost ());
    assertEquals (st1.getHost (), st2.getHost ());
  }

  /**
   * Test method for {@link at.peppol.commons.ipmapper.SocketType#getPort()}.
   */
  @Test
  public void testGetPort () {
    final SocketType st = new SocketType ("www.chello.at", 80);
    final SocketType st1 = new SocketType ("www.chello.at", 80);
    final SocketType st2 = new SocketType ("www.chello.at", 1);
    assertEquals (st.getPort (), st1.getPort ());
    assertFalse (st.getPort ().equals (st2.getPort ()));
    // System.out.println(String.format("st.getPort='%d', st1.getPort='%d'",
    // st.getPort(), st1.getPort()));
    assertEquals (st.getPort ().intValue (), st1.getPort ().intValue ());
  }

  /**
   * Test method for
   * {@link at.peppol.commons.ipmapper.SocketType#getSocketString()}.
   */
  @Test
  public void testGetSocketString () {
    final SocketType st = new SocketType ("1.1.1.1", 10);
    final SocketType st1 = new SocketType ("1.1.1.1", 10);
    final SocketType st2 = new SocketType ("1.1.2.1", 10);
    final SocketType st3 = new SocketType ("1.1.1.1", 9);
    assertEquals (st.getSocketString (), st1.getSocketString ());
    assertFalse (st1.equals (st2));
    assertFalse (st.equals (st3));
  }
}
