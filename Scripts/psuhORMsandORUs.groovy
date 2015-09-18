/*This script will be used to force the eventHandler to push
messages of subType ORM and ORU.  ONLY USE IF NECESSARY!
This will cause high memory usage on the system*/

import com.cds.healthdock.facilities.Facility
import com.cds.healthdock.messaging.Message
import com.cds.messaging.Message.State

def f = Facility.findByNickName('Enter Facility nickName')
assert f

def orm = Message.countByFacilityAndStateAndSubType(f, State.Outboxed, 'ORM')
def log_orm = "ORM messages: $orm"
println log_orm
log.error log_orm

def oru = Message.countByFacilityAndStateAndSubType(f, State.Outboxed, 'ORU')
def log_oru = "ORU messages: $oru"
println log_oru
log.error log_oru

Message.findAllByFacilityAndStateAndSubType(f, State.Outboxed, 'ORM').each {
  try {
    it.ttl = 3
      it.save(flush: true)
      ctx.outboxService.serviceMethod([id: it.id])
  } catch (Exception e) {
    log.error "Failed to push message #${it.id}"
  }
}
Message.findAllByFacilityAndStateAndSubType(f, State.Outboxed, 'ORU').each {
  try {
    it.ttl = 3
      it.save(flush: true)
      ctx.outboxService.serviceMethod([id: it.id])
  } catch (Exception e) {
    log.error "Failed to push message #${it.id}"
  }
}

log.error "Manually pushing outboxed messages finished"
'Done'

