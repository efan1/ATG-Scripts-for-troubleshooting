/*This script is used to delete a certain physician
off the healthlogix platform.*/

import com.cds.healthdock.persons.*
import com.cds.healthdock.facilities.Facility
import com.cds.healthdock.messaging.Message

String facilityNickname = 'facility nickname'
Facility facility = Facility.findByNickName(facilityNickname)

def physician = Physician.findAllByFacility(facility)
  assert physician : "physicians not found"

def phys = Physician.findAllByFacility(facility)
  phys.each{
    it.delete(flush:true, failOnError: true)
  }
