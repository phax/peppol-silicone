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
package eu.peppol.start.log;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;

import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.xml.XMLLayout;

import com.phloc.commons.collections.ContainerHelper;
import com.phloc.commons.microdom.IMicroElement;
import com.phloc.commons.microdom.impl.MicroElement;
import com.phloc.commons.microdom.serialize.MicroWriter;

/**
 * Log4J logging layout customizer
 * 
 * @author Jose Gorvenia Narvaez(jose@alfa1lab.com)<br>
 *         PEPPOL.AT, BRZ, Philip Helger
 */
public class CustomizedXMLLayout extends XMLLayout {
  /**
   * Set up the format for XML file.
   * 
   * @param aEvent
   *        Represents log event.
   * @return String containing XML log data
   */
  @Override
  public final String format (final LoggingEvent aEvent) {
    final IMicroElement eLogEvent = new MicroElement ("logEvent");
    eLogEvent.appendElement ("message").appendCDATA (aEvent.getRenderedMessage ());
    eLogEvent.appendElement ("class").appendText (aEvent.getLoggerName ());
    eLogEvent.appendElement ("method").appendText (aEvent.getLocationInformation ().getMethodName () +
                                                   ":" +
                                                   aEvent.getLocationInformation ().getLineNumber ());
    eLogEvent.appendElement ("timestamp").appendText (Long.toString (aEvent.timeStamp));
    final Date logdate = new Date (aEvent.timeStamp);
    final String formatted = DateFormat.getDateTimeInstance (DateFormat.SHORT, DateFormat.SHORT).format (logdate);
    eLogEvent.appendElement ("datetime").appendText (formatted);
    eLogEvent.appendElement ("priority").appendText (String.valueOf (aEvent.getLevel ()));
    eLogEvent.appendElement ("thread").appendText (aEvent.getThreadName ());
    final String [] aThrowableTexts = aEvent.getThrowableStrRep ();
    if (aThrowableTexts != null) {
      final StringBuilder aSB = new StringBuilder ();
      for (final String sElement : aThrowableTexts)
        aSB.append (sElement).append ('\n');
      eLogEvent.appendElement ("throwable").appendCDATA (aSB);
    }

    final Set <?> aPropertyKeys = aEvent.getPropertyKeySet ();
    if (!ContainerHelper.isEmpty (aPropertyKeys)) {
      final IMicroElement eProps = eLogEvent.appendElement ("properties");
      final Object [] aKeys = aPropertyKeys.toArray ();
      Arrays.sort (aKeys);
      for (final Object aKey : aKeys) {
        final String sKey = aKey.toString ();
        final String sValue = aEvent.getProperty (sKey);
        if (sValue != null)
          eProps.appendElement ("data").setAttribute ("name", sKey).setAttribute ("value", sValue);
      }
    }

    return MicroWriter.getXMLString (eLogEvent);
  }
}
