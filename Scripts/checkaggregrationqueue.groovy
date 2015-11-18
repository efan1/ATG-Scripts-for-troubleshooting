/**
 * This Script show the messages stuck in AggregationPending state for a particular clinic
 * @author YuanYao
 * 
 * Paramters
 * nickkName: the nickname of the clinic we want to query
 * mode: mode for showing results. Can be 'oneline' or 'raw'. 'oneline' is default.
 *       -'oneline': list every message in one line with brief info.
 *       -'raw': shows the detailed ORU message. 
 * offset: results are ordred by time, offset specifies how many messages we want to skip from the first one
 * size: maximum messages we want to list at a time. 
 *       This is to prevent huge queries in case there're too many pending messages.
 * 
 * E.g. There 500 pending messages in total, we want to list the 201th - 300th message of them.
 *      Then set offset = 200, size = 100
 */
import com.cds.healthdock.facilities.Facility
import com.cds.healthdock.messaging.Message
import com.cds.messaging.Message.State

//specify these parameters based on comments above.
def nickName = 'FRJM'
def mode = 'oneline'
def offset = 0
def size = 100


def clinic = Facility.findByNickName(nickName)
  assert clinic : "Clinic with nickname $nickName not found !"
  assert (mode == 'oneline' || mode == 'raw') : "Mode $mode is not supported !"

    def criteria = Message.where {
      facility == clinic
        state == State.AggregationPending
    }
def total = criteria.count()
  def msgs = criteria.list(offset: offset, max: size, sort: 'id', order: 'asc')
  println("Showing ${offset + 1} - ${offset + (msgs.size() < size ? msgs.size() : size)} of $total AggregationPending results")

  msgs.eachWithIndex { msg, index ->
    if (mode == 'oneline') {
      def p = msg.patient
        println("${index+1} - Patient[${p.firstName}, ${p.lastName}] FromHospital[${msg.fromFacility.name}] LastUpdated[${msg.lastUpdated}]")
    } else if (mode == 'raw') {
      println("--------------${index+1}--------------")
        println(new String(msg.contentBytes))
        println("--------------${index+1}--------------\n")
    }
  }

println("\nListed ${offset + 1} - ${offset + (msgs.size() < size ? msgs.size() : size)} of $total AggregationPending results")
''
