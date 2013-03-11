# Copyright 2012 Christe, Anthony
# 
# This file is part of KiP.
#
# KiP is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
# 
# KiP is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
# 
# You should have received a copy of the GNU General Public License
# along with KiP.  If not, see <http://www.gnu.org/licenses/>.

#!/bin/bash

PASS=123456
KS=-Djavax.net.ssl.keyStore=/home/anthony/clientKeyStore.jks
KSP=-Djavax.net.ssl.keyStorePassword=$PASS
TS=-Djavax.net.ssl.trustStore=/home/anthony/clientTrustStore.jks
TSP=-Djavax.net.ssl.trustStorePassword=$PASS
DBG=-Djavax.net.debug=ssl

OPTIONS="$KS $KSP $TS $TSP"

if [ $1 ] && [ $2 ]
then
	java  $OPTIONS -cp ./bin kip.ssl.SSLSocketClientWithClientAuth $1 $2 "hello, world."
else
	echo "Usage: sh SSLClient.sh [host] [port]"
fi
