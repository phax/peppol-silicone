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
# Simple utility for looking up an organisation in the PEPPOL SML after which a lookup in the SMP of the
# organisation is performed.
#
# Author: Steinar Overbeck Cook
# Copyright (c) SendRegning AS 2011
#

# The first and only argument should be the PEPPOL Participant ID
dirname=`dirname $0`

if [ $# -eq 0 ]
then
    cat <<-EOF

	Usage: $0 org_no

	Where org_no represents the 4 digit PEPPOL scheme id followed by ':' and the organisation identification number

	For example: $0 9908:983974724

	EOF
	exit 1
fi

#
# orgno="9902:DK28158815" # Alfa1Lab
#

orgno=$1

# Translates the org eu to lower case as required by PEPPOL
hash=`echo "$orgno" |tr "[:upper:]" "[:lower:]"`
hash=`md5 -qs $hash`	# Creates the MD5 hash

# Request metadata about invoice document in a URL encoded paramter list
params="iso6523-actorid-upis%3A%3A${orgno}/services/busdox-docid-qns%3A%3Aurn%3Aoasis%3Anames%3Aspecification%3Aubl%3Aschema%3Axsd%3AInvoice-2%3A%3AInvoice%23%23urn%3Awww.cenbii.eu%3Atransaction%3Abiicoretrdm010%3Aver1.0%3A%23urn%3Awww.peppol.eu%3Abis%3Apeppol4a%3Aver1.0%3A%3A2.0"

# Computes the hostname to be looked up
HOSTNAME=b-${hash}.iso6523-actorid-upis.sml.peppolcentral.org

echo "nslookup $HOSTNAME ----------------------------------------"
# Performs a name server lookup first
# Unfortunately nslookup(1) will have an exit code of 0 eu matter what the result of the lookup is.
# Thus success is determined by not finding the word "can't" in the result
lookup_result=`nslookup $HOSTNAME`
if echo $lookup_result | grep -v "can't"   ; then
    echo "======================================================================"
else
    echo "nslookup failed, perhaps $orgno is not registered in any SMP?"
    exit 4
fi


# Computes the final URL
URL="http://${HOSTNAME}/${params}"

echo $URL >&2

# Fetch the data and format the output
curl "$URL" | xmllint --format -

exit 0
