#!/bin/sh
#
# Version: MPL 1.1/EUPL 1.1
#
# The contents of this file are subject to the Mozilla Public License Version
# 1.1 (the "License"); you may not use this file except in compliance with
# the License. You may obtain a copy of the License at:
# http://www.mozilla.org/MPL/
#
# Software distributed under the License is distributed on an "AS IS" basis,
# WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
# for the specific language governing rights and limitations under the
# License.
#
# The Original Code is Copyright The PEPPOL project (http://www.peppol.eu)
#
# Alternatively, the contents of this file may be used under the
# terms of the EUPL, Version 1.1 or - as soon they will be approved
# by the European Commission - subsequent versions of the EUPL
# (the "Licence"); You may not use this work except in compliance
# with the Licence.
# You may obtain a copy of the Licence at:
# http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the Licence is distributed on an "AS IS" basis,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the Licence for the specific language governing permissions and
# limitations under the Licence.
#
# If you wish to allow use of your version of this file only
# under the terms of the EUPL License and not to allow others to use
# your version of this file under the MPL, indicate your decision by
# deleting the provisions above and replace them with the notice and
# other provisions required by the EUPL License. If you do not delete
# the provisions above, a recipient may use your version of this file
# under either the MPL or the EUPL License.
#

#
# Author: Steinar O. Cook
#
# Sample program illustrating how a single file may be sent using the stand alone client.
#
# See bottom section for an explanation of document and process type identificators

# Set up default receiver, which is SendRegning in this case
export RECEIVER="9908:810017902"

# Location of keystore
export KEYSTORE="/Users/steinar/appl/apache-tomcat-7.0.22/conf/keystore/keystore.jks" 

# Figure out our installation directory relative to invoication path
export INSTALL_DIR=`dirname $0`

# Determines the path of our sample file
export SAMPLE_FILE="$INSTALL_DIR/src/main/resources/BII04_T10_EHF-v1.5_invoice.xml"
if [ ! -r "$SAMPLE_FILE" ]
then
	echo "ERROR: unable to locate $SAMPLE_FILE"
	exit 4
fi

# Verifies that keystore can be read
if [ ! -r "$KEYSTORE" ]; then
	echo "Unable to read $KEYSTORE"
	exit 4
fi

# If another receiver was supplied on the command line, use it.
if [ $# -ge 1 ]
then
	export RECEIVER="$1"
fi

# If another file was specified on the command line, use it.
if [ $# -ge 2 ]
then
    export SAMPLE_FILE=$2
    if [ ! -r "$SAMPLE_FILE" ]; then
        echo "$SAMPLE_FILE can not be read"
        exit 4
    fi
fi


#
# Sends the file using a Document type identificator indicating a Peppol bis4a document (generic european)
# and a process type identificator indicating CEN/BII "Invoice only"
# This is the simplest of them all.
#
java -jar $INSTALL_DIR/target/oxalis-standalone.jar \
-kf $KEYSTORE \
-kp=peppol \
-f $SAMPLE_FILE \
-d "urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0:#urn:www.peppol.eu:bis:peppol4a:ver1.0::2.0" \
-p "urn:www.cenbii.eu:profile:bii04:ver1.0" \
-c CH1 \
-r $RECEIVER \
-s 9908:810017902

# Other usefull PPIDs:
# SendRegning
# -r 9908:976098897 \
#
# ESV = 0088:7314455667781
# EdiGard 9908:994241257
#

#
# The following is a list of document type identificators and their accompanying cen bii process identificators, which are currently (May 25, 2012) available
#

#
# PEPPOL Catalogues (PEPPOL BIS profile 1a)
#
# urn:oasis:names:specification:ubl:schema:xsd:ApplicationResponse-2::ApplicationResponse##urn:www.cenbii.eu:transaction:biicoretrdm057:ver1.0:#urn:www.peppol.eu:bis:peppol1a:ver1.0::2.0
# urn:www.cenbii.eu:profile:bii01:ver1.0
# urn:oasis:names:specification:ubl:schema:xsd:ApplicationResponse-2::ApplicationResponse##urn:www.cenbii.eu:transaction:biicoretrdm058:ver1.0:#urn:www.peppol.eu:bis:peppol1a:ver1.0::2.0
# urn:www.cenbii.eu:profile:bii01:ver1.0
# urn:oasis:names:specification:ubl:schema:xsd:Catalogue-2::Catalogue##urn:www.cenbii.eu:transaction:biicoretrdm019:ver1.0:#urn:www.peppol.eu:bis:peppol1a:ver1.0::2.0
# urn:www.cenbii.eu:profile:bii01:ver1.0

#
# PEPPOL Orders (PEPPOL BIS profile 3a)
#
# urn:oasis:names:specification:ubl:schema:xsd:Order-2::Order##urn:www.cenbii.eu:transaction:biicoretrdm001:ver1.0:#urn:www.peppol.eu:bis:peppol3a:ver1.0::2.0
# urn:www.cenbii.eu:profile:bii03:ver1.0

#
# Standalone Credit Note according to EHF
#
# urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2::CreditNote##urn:www.cenbii.eu:transaction:biicoretrdm014:ver1.0:#urn:www.cenbii.eu:profile:biixx:ver1.0#urn:www.difi.no:ehf:kreditnota:ver1::2.0
# urn:www.cenbii.eu:profile:biixx:ver1.0

#
# PEPPOL Invoices (PEPPOL BIS profile 4a)
#
# urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0:#urn:www.peppol.eu:bis:peppol4a:ver1.0::2.0
# urn:www.cenbii.eu:profile:bii04:ver1.0

#
# Standalone Invoice according to EHF
#
# urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0:#urn:www.peppol.eu:bis:peppol4a:ver1.0#urn:www.difi.no:ehf:faktura:ver1::2.0
# urn:www.peppol.eu:bis:peppol4a:ver1.0

#
# Invoice, Credit Note and Reminder according to EHF
#
# urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0:#urn:www.cenbii.eu:profile:biixy:ver1.0#urn:www.difi.no:ehf:faktura:ver1::2.0
# urn:www.cenbii.eu:profile:biixy:ver1.0
# urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2::CreditNote##urn:www.cenbii.eu:transaction:biicoretrdm014:ver1.0:#urn:www.cenbii.eu:profile:biixy:ver1.0#urn:www.difi.no:ehf:kreditnota:ver1::2.0
# urn:www.cenbii.eu:profile:biixy:ver1.0
# urn:oasis:names:specification:ubl:schema:xsd:Reminder-2::Reminder##urn:www.cenbii.eu:transaction:biicoretrdm017:ver1.0:#urn:www.cenbii.eu:profile:biixy:ver1.0#urn:www.difi.no:ehf:purring:ver1::2.0
# urn:www.cenbii.eu:profile:biixy:ver1.0

#
# PEPPOL Billing (PEPPOL BIS profile 5a)
#
# urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0:#urn:www.peppol.eu:bis:peppol5a:ver1.0::2.0
# urn:www.cenbii.eu:profile:bii05:ver1.0
# urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2::CreditNote##urn:www.cenbii.eu:transaction:biicoretrdm014:ver1.0:#urn:www.peppol.eu:bis:peppol5a:ver1.0::2.0
# urn:www.cenbii.eu:profile:bii05:ver1.0
# urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0:#urn:www.peppol.eu:bis:peppol5a:ver1.0::2.0
# urn:www.cenbii.eu:profile:bii05:ver1.0

#
# PEPPOL Procurement processes (PEPPOL BIS profile 6a)
#
# urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biicoretrdm010:ver1.0:#urn:www.peppol.eu:bis:peppol6a:ver1.0::2.0
# urn:www.cenbii.eu:profile:bii06:ver1.0
# urn:oasis:names:specification:ubl:schema:xsd:Invoice-2::Invoice##urn:www.cenbii.eu:transaction:biicoretrdm015:ver1.0:#urn:www.peppol.eu:bis:peppol6a:ver1.0::2.0
# urn:www.cenbii.eu:profile:bii06:ver1.0
# urn:oasis:names:specification:ubl:schema:xsd:Order-2::Order##urn:www.cenbii.eu:transaction:biicoretrdm001:ver1.0:#urn:www.peppol.eu:bis:peppol6a:ver1.0::2.0
# urn:www.cenbii.eu:profile:bii06:ver1.0
# urn:oasis:names:specification:ubl:schema:xsd:OrderResponseSimple-2::OrderResponseSimple##urn:www.cenbii.eu:transaction:biicoretrdm002:ver1.0:#urn:www.peppol.eu:bis:peppol6a:ver1.0::2.0
# urn:www.cenbii.eu:profile:bii06:ver1.0
# urn:oasis:names:specification:ubl:schema:xsd:OrderResponseSimple-2::OrderResponseSimple##urn:www.cenbii.eu:transaction:biicoretrdm003:ver1.0:#urn:www.peppol.eu:bis:peppol6a:ver1.0::2.0
# urn:www.cenbii.eu:profile:bii06:ver1.0
# urn:oasis:names:specification:ubl:schema:xsd:CreditNote-2::CreditNote##urn:www.cenbii.eu:transaction:biicoretrdm014:ver1.0:#urn:www.peppol.eu:bis:peppol6a:ver1.0::2.0
# urn:www.cenbii.eu:profile:bii06:ver1.0

