/*This script will display orders associated
to a single patient. Fill in params accordingly
*/

import com.cds.healthdock.facilities.*
import com.cds.healthdock.messaging.Message
import com.cds.messaging.Message.State
import com.cds.healthdock.orders.Order
import com.cds.healthdock.persons.*

def f = Facility.findByNickName('')
assert f

def pid = PatientId.findByIdentifierAndFacility('', f)
assert pid

def p = pid.patient

def o = Order.findAllByFacilityAndPatient(f, p)
o.size()        //display number of orders
//uncomment to see raw orders
//println o
