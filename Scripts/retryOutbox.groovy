/*RETRY SCRIPT FOR MESSAGES IN OUTBOX
This will reset the ttl life of a message to 10
Up to 1000 messages, this number is configurable
*/

import com.cds.healthdock.facilities.Facility
import com.cds.healthdock.messaging.Message
import com.cds.messaging.Message.State

def f = Facility.findByNickName('Enter Facility Nickname')
assert f: "Facility is required"

def messages = Message.findAllByFacilityAndTtlAndState(f, 0, State.Outboxed,[max: 1000]).each { msg ->
  msg.ttl = 10
}

messages.first().save(flush: true)
