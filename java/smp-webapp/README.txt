====
    Version: MPL 1.1/EUPL 1.1

    The contents of this file are subject to the Mozilla Public License Version
    1.1 (the "License"); you may not use this file except in compliance with
    the License. You may obtain a copy of the License at:
    http://www.mozilla.org/MPL/

    Software distributed under the License is distributed on an "AS IS" basis,
    WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
    for the specific language governing rights and limitations under the
    License.

    The Original Code is Copyright The PEPPOL project (http://www.peppol.eu)

    Alternatively, the contents of this file may be used under the
    terms of the EUPL, Version 1.1 or - as soon they will be approved
    by the European Commission - subsequent versions of the EUPL
    (the "Licence"); You may not use this work except in compliance
    with the Licence.
    You may obtain a copy of the Licence at:
    http://joinup.ec.europa.eu/software/page/eupl/licence-eupl

    Unless required by applicable law or agreed to in writing, software
    distributed under the Licence is distributed on an "AS IS" basis,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the Licence for the specific language governing permissions and
    limitations under the Licence.

    If you wish to allow use of your version of this file only
    under the terms of the EUPL License and not to allow others to use
    your version of this file under the MPL, indicate your decision by
    deleting the provisions above and replace them with the notice and
    other provisions required by the EUPL License. If you do not delete
    the provisions above, a recipient may use your version of this file
    under either the MPL or the EUPL License.
====

Setting up tomcat:
Tomcat must be set up with the following java system property:
org.apache.tomcat.util.buf.UDecoder.ALLOW_ENCODED_SLASH = true

This can be done by adding:
-Dorg.apache.tomcat.util.buf.UDecoder.ALLOW_ENCODED_SLASH="true"
as a JVM argument. (Tomcat Properties -> Java -> Java Options) or
put it into the catalina.sh in linux.

Edit the catalina.sh with the following: 
---------------------------------------------------------------------------------------------------------------------------------------------------------
JAVA_OPTS="$JAVA_OPTS -Dorg.apache.tomcat.util.buf.UDecoder.ALLOW_ENCODED_SLASH=true"
---------------------------------------------------------------------------------------------------------------------------------------------------------

Dependencies:
If the ServiceMetadataPublishingCommonLibrary project has been updated then
export the ServiceMetadataPublishingCommonLibrary project as a jar file to 
WebContent/lib/peppol-smp-common.jar

Creating the database from a backup using the command line on ubuntu:

Command:
mysql --user=root --password=peppol peppol < Backup\ 20090708\ 1106.sql

hibernate.cfg.xml:
<property name="connection.url">jdbc:mysql://localhost/peppol</property>
<property name="connection.username">root</property>
<property name="connection.driver_class">com.mysql.jdbc.Driver</property>
<property name="dialect">org.hibernate.dialect.MySQLDialect</property>
<property name="connection.password">peppol</property>

Known problems:
- When importing the backup the mysql server on ubuntu dev server 3 don't like "USING BTREE". Works when
these are deleted.

If the deployed package is missing the metro framework; install it on the
tomcat server.