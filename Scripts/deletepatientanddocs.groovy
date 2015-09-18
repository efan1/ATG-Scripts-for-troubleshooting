/*This script will delete patients and all of there
associated documents.  Enter information accordingly*/

import com.cds.healthdock.facilities.Facility
import com.cds.healthdock.messaging.Message
import com.cds.healthdock.persons.Patient
import com.cds.healthdock.persons.PatientId
import com.cds.healthdock.repository.Document

String facilityNickname = '' // enter the facility's nickname here
Facility facility = Facility.findByNickName(facilityNickname)

def patient = Patient.findAllByFacility(facility)
assert patient : 'patient could not be found'

def document = Document.findAllByFacility(facility)
  document.each{
    it.delete(flush:true, failOnError: true)
  }
patient.each{
  String patientStr = it.toString()
    log.info "Deleting $patientStr as well as all associated documents and messages"

    Message.createCriteria().list {
      eq('patient', it)
    }*.delete()
  it.delete(flush: true, failOnError: true)
}
