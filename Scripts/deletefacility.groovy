import com.cds.healthdock.audit.AuditMessage
import com.cds.healthdock.auth.User
import com.cds.healthdock.facilities.Event
import com.cds.healthdock.facilities.EventNotification
import com.cds.healthdock.facilities.Facility
import com.cds.healthdock.facilities.HealthcareSystem
import com.cds.healthdock.facilities.Membership
import com.cds.healthdock.facilities.RemoteFacility
import com.cds.healthdock.messaging.Message
import com.cds.healthdock.messaging.MessagePhysician
import com.cds.healthdock.orders.Order
import com.cds.healthdock.persons.Patient
import com.cds.healthdock.persons.Physician
import com.cds.healthdock.registry.RegistryEntry
import com.cds.healthdock.repository.Document
/**
 * Deletes A facility specified by its nickName
 *
 * To use:
 * 1. Set the nickname of the facility and run the script
 *
 * Note:
 * This script works only when the facility has small amount of messages.
 * As a reference, deleting 70000 messages takes about 3 hours.
 * Therefore if the facility has 1 million related messages, this won't work.
 *
 */
// Set the nickname of the facility here
def f = Facility.findByNickName('your facility nickname')
def deleteMsg(Message msg, List deleted) {
  if (msg.id in deleted) {
    return
  }
  Message.findAllByParentMessage(msg).each { deleteMsg(it, deleted) }
  if (msg instanceof Order) {
    Order.findAllByParentOrder(msg).each { deleteMsg(it, deleted) }
  }
  MessagePhysician.findAllByMsg(msg)*.delete()
    msg.delete()
    deleted << msg.id
}
if (f) {
  Facility.withTransaction { txn ->
    EventNotification.findAllByFacility(f)*.delete()
      Event.findAllByFacility(f)*.delete()
      RegistryEntry.findAllByFacility(f)*.delete()
      Document.findAllByFacility(f)*.delete()
      AuditMessage.findAllByFacility(f)*.delete()
      def deleted = []
      Message.findAllByFacility(f).each { Message msg ->
        deleteMsg(msg, deleted)
      }
      RemoteFacility.findAllByFacility(f)*.delete()
        Patient.findAllByFacility(f)*.delete()
        Physician.findAllByFacility(f).each { Physician phy ->
          Membership.findAllByEntity(phy)*.delete()
            phy.delete()
        }
      Membership.findAllByEntity(f)*.delete()
        HealthcareSystem.findAll().each {
          Set<Facility> facilities = it.facilities
            if (facilities.contains(f)) {
              facilities.remove(f)
                it.save()
            }
        }
      //also remove facility id in user's mru list to avoid NPE on UI
      User.findAll().each { User u ->
        if(u.mruFacilities) {
          def mruFacilityIds = u.mruFacilities.split(',')*.toLong()
            mruFacilityIds.remove(f.id)
            u.mruFacilities = mruFacilityIds.join(',')
        }
      }
      f.delete(flush: true)
  }
  log.error 'Facility deleted successfully..'
} else {
  log.error 'No facility found, please check its nickName...'
}
''
