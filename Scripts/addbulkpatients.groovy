/*This grails script is to bulk load patients
good for test scenarios*/

import com.cds.healthdock.persons.*
import com.cds.healthdock.facilities.Facility

def f = Facility.findByNickName('your facility nickaname')
assert f

int numberOfPatients = 500

//This will create patients and append 01, 02, 03 to variablfirst and variablelast
for (int i=1; i<= numberOfPatients; i++) {
  def pat = new Patient(firstName: "variablefirst$i", lastName: "variablelast$i", facility: f)
    pat.addToIds(
        identifier: "enter id",
        namespaceId: "enter namespaceID",
        universalId: 'endter uuid',
        universalIdType: 'ISO'
        )
    pat.save(flush: true)
}
