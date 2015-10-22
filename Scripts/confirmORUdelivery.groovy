//This script is to compare ORUs that are stuck on the outbox
//Here are the steps to reprocess stuck ORUs
//1)  Execute the following grep command on the machine you are investigating where newnameforlogfile is a name of choosing and change the log line to match clinic nickname:
//zgrep "ERROR messaging.OutboxService @(Clininc Nickname) - Unable to process MSH" /home/healthdock/logs/healthdock.log.2015-0[(begin month number)- (end month number)]* > isrejected.newnameforlogfile
//2)  Next, run the following linux command: mv isrejected.newnameforlogfile /home/healthdock/logs and chown healthdock:healthdock isrejected.newnameforlogfile so that the owner for this log is healthdock
//3)  Navigate to the console on the machine and execute the following script:

import com.cds.healthdock.facilities.*
import com.cds.healthdock.messaging.*
import ca.uhn.hl7v2.model.v26.message.ORU_R01
import com.cds.messaging.Message.State
import com.cds.messaging.hl7.Hl7ParserFactory
import com.cds.messaging.hl7.MessagingUtil

//Put the clinic nickname here
def f = Facility.findByNickName('Enter Clinic NickName')
//Put the absolute path of your grepped log in the server.
def logFile = new File("file path of grepped log") as String[]
assert logFile

def msgs = Message.findAllByFacilityAndStateAndSubType(f, State.Outboxed, 'ORU',[max: 700, sort:'dateCreated', order:'desc'])

def msgUtil = new MessagingUtil(hl7ParserFactory: new Hl7ParserFactory())

  msgs.each {
    def content = new String(it.contentBytes)
      ORU_R01 oruMessage = msgUtil.parseMessage(content, [:])
      def controlId = oruMessage.MSH.messageControlID.value
      def found = false

      for (int i=0; i<logFile.length; i++) {
        String timeStampLine = logFile[i++]
        String ackLine = logFile[i]
        if (ackLine?.contains(controlId)){
          found = true
            println "ORU [$controlId] got ACK at ${timeStampLine.substring(66, 89)}"
            log.error "ORU [$controlId] got ACK at ${timeStampLine.substring(66, 89)}"
            return
        }
      }

    if (!found) {
      println "ORU [$controlId] received at ${it.dateCreated} not found"
        log.error "ORU [$controlId] received at ${it.dateCreated} not found"
    }
  }

''
