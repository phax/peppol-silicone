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
package at.peppol.busdox.types;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.junit.Test;

import at.peppol.busdox.types.DateAdapter;

/**
 * Test class for class {@link DateAdapter}.
 * 
 * @author PEPPOL.AT, BRZ, Philip Helger
 */
public final class DateAdapterTest {
  @Test
  public void testConvert () {
    final Calendar c = new GregorianCalendar (2011, Calendar.JULY, 6);
    c.setTimeZone (TimeZone.getTimeZone ("UTC"));
    final Date d = c.getTime ();
    final String s = DateAdapter.printDate (d);
    assertEquals ("2011-07-06Z", s);
    final Date d2 = DateAdapter.parseDate (s);
    assertEquals (d.getTime (), d2.getTime ());

    final Calendar c2 = new GregorianCalendar ();
    c2.setTime (d2);
    assertEquals (2011, c2.get (Calendar.YEAR));
    assertEquals (Calendar.JULY, c2.get (Calendar.MONTH));
    assertEquals (6, c2.get (Calendar.DAY_OF_MONTH));
  }
}
