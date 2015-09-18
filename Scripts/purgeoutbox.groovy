/*This script will delete all content of the outbox
Once this script is executed, all messages in the outbox
will be completely removed.  Use cautiously!
*/

import com.cds.healthdock.facilities.Facility
import com.cds.healthdock.messaging.Message
import com.cds.messaging.Message.State


def fac = Facility.findByNickName('Enter Facility Nickname')

def batchSize = 100
def offset = 0
def count = 1
def mquery = Message.where { facility == fac && state == State.Outboxed }
def mids = []
while (count > 0) {

  def messages = mquery.list([max: batchSize, offset: offset])
    count = messages.size()
    offset += count

    //mids += messages*.id
    mids = []
}
if(mids.size()>0){
  mids.collate(batchSize).each { ids ->
    Facility.withNewTransaction {
      log.error "deleting ${ids.size()} messages from the outbox..."
        Message.executeUpdate('update Message set state=:desiredState where id in :ids', [desiredState: State.SoftDeleted, ids: ids])
    }
  } 
}
