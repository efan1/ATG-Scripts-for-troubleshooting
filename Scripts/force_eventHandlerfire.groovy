/*This script is used to force the eventHandler method to fire
which will pick up the message from the last defined state*/

import com.cds.healthdock.messaging.*

//check message state
def m = Message.get('Enter message db ID')

//Uncomment below to fire to next state
//ctx.eventHandlerService.fireSync('last message state', [id: 'message id'])
