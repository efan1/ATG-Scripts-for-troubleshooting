/*This script will display the total number of ORUs or ORMs
delivered to that specific clinic.  Uncomment the subType based
on what records you want to count.  State of messages are configurable*/

import com.cds.healthdock.facilities.Facility
import com.cds.healthdock.messaging.Message
import com.cds.messaging.Message.State
import java.text.*
import groovy.sql.Sql

def f = Facility.findByNickName('Enter clinic nickname')
assert f

def c = Message.createCriteria()

  DateFormat formatter  = new SimpleDateFormat("yyyyMMdd");

  Date from = formatter.parse('yyyyMMdd')
  Date to = formatter.parse('yyyyMMdd')

  def results = c.count {

    between("dateCreated", from, to)
    //state can be configured - default is softDeleted
      eq('state', State.SoftDeleted)
      //eq('subType', 'ORM')
      eq('subType', 'ORU')
  }
