/*This script is used to retry missing orders that have NOT been exposed on the UI
If the order has already been exposed on the UI, the script will not work.
Exposed orders need to be resent manually with a different order ID 
For Healthlogix Versions 4.9.1 to 4.11
*/
import com.cds.messaging.hl7.*
import ca.uhn.hl7v2.model.Message as HapiMessage
import com.cds.healthdock.messaging.Message
import com.cds.messaging.Message.State

MessagingUtil messagingUtil = new MessagingUtil()
def msg = Message.get()   //place message dB ID here

  println "Place message content into string"

String rawMessage = new String(msg.contentBytes)
  Map context = msg.contextMap ?: [:]
  context << [
    id: msg.id,
    facility: msg.facility,
    sender: msg.fromFacility,
    state: msg.state,
    messageUuid: msg.uuid,
    sourceFacilityName: msg.sourceFacilityName ?: '',
    patient: msg.patient,
    subType: msg.subType,
    messageContent: rawMessage
  ]

  println "map is type HapiMessage and call messageUtil"

HapiMessage hapiMsg = messagingUtil.parseMessage(rawMessage, context)
  switch (msg.subType) {
    case 'ADT':
      context.pid = hapiMsg.PID
        break
    case 'ORM':
        context.pid = hapiMsg?.getPATIENT()?.PID
          break
          //TODO: implement retry for other message types
    default:
          log.debug "The message type is not supported!"
  }

println "message is selected for retry"

try{
  log.info "Reprocessing message: ${msg.id}"
    ctx.eventHandlerService.fire('message.received', context)
}catch(Throwable e){
  log.error "Message retry on message: ${msg.id} failed due to ${e.class.name}."
    log.error Arrays.toString(e.stackTrace)
}

println "message has been retried"
