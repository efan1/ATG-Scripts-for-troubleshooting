/*This script will remove all failed conection attempts
with connection status of RequestSent.
Can be modified accordingly depending on connection status.
Values for Connection State are NotConnected, RequestSent, RequestReceived, Connected
*/

import com.cds.healthdock.facilities.*

Facility f = Facility.findByNickName('your facility nickname')
//Find out all the connections of this facility with RequestSent status
def rf = RemoteFacility.findAllByFacilityAndConnectionStatus(f, 'RequestSent')

rf.each { it -> it.delete(flush:true)}
