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
# This script was used to construct the keystore and truststore for SendRegning's aksesspunkt
#
# Note: the script will have to be adapted to generate keystores for other participants
#


home=~/Filer/mazeppa/SendRegning/live-certificates
build=build
final_location=$1

our_certificate=sendregning.cer
our_private_key=PEPPOL_private.key.encrypted
peppol_certificates=PEPPOL-CA-certificates

keystore_file=$final_location/keystore.jks
truststore_file=$final_location/truststore.jks
pass=$2

tmp1=$build/private-key.txt
tmp2=$build/temp2.p12
tmp3=mypass
tmp4=1

cd $home


# --------------------------------------------------------------------------
# keystore
# --------------------------------------------------------------------------


if [ ! -f $tmp1 ];
then
	openssl des3 -d -salt -in $our_private_key -out $tmp1
fi

openssl pkcs12 -export -in $our_certificate -inkey $tmp1 -out $tmp2 -passout pass:$tmp3 -name $tmp4

rm $keystore_file
keytool -importkeystore -srckeystore $tmp2 -srcstoretype PKCS12 -srcstorepass $tmp3 -alias $tmp4 -destkeystore $keystore_file -deststorepass $pass -destkeypass $pass

rm $tmp2


# --------------------------------------------------------------------------
# truststore
# --------------------------------------------------------------------------

if [ ! -f $truststore_file ];
then
	keytool -import -keystore $truststore_file -storepass $pass -alias root -file "$peppol_certificates/PEPPOL Root Test CA.cer"
	keytool -import -keystore $truststore_file -storepass $pass -alias ap -file "$peppol_certificates/PEPPOL Access Point Test CA.cer"
fi

cd $final_location
ls -l
