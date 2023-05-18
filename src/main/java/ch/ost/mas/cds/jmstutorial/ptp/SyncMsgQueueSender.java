//
// (c) NDS/SE Distributed Computing 2002-2007
//   Uebung : JMSEinstieg
//
package ch.ost.mas.cds.jmstutorial.ptp;

import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;

import ch.ost.mas.cds.jmstutorial.util.JMSUtil;

public class SyncMsgQueueSender {
	private String mQueue;

	public SyncMsgQueueSender() {
		mQueue = JMSUtil.getInstance().getDestination("Q.CDS.");
	}

	private void run() {
		QueueConnectionFactory queueConnectionFactory = JMSUtil.getInstance().getQueueConnectionFactory();
		int count = JMSUtil.getInstance().findArgVal(JMSUtil.COUNT);

		if (count <= 0) {
			count = 100;
		}

		if (queueConnectionFactory == null) {
			return;
		}

		try {
			QueueConnection queueConnection = queueConnectionFactory.createQueueConnection();
			QueueSession queueSession = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);

			Queue queue = queueSession.createQueue(mQueue);
			Queue tmpque = queueSession.createTemporaryQueue();
			QueueReceiver tmpQRcv = queueSession.createReceiver(tmpque);
			String tmpqueName = tmpque.getQueueName();
			System.err.println("Sender has the following Tmpqueue: " + tmpqueName);
			QueueSender sender = queueSession.createSender(queue);
			queueConnection.start();

			for (int idx = 1; idx <= count; idx++) {
				Message rcvdMsg = null;
				long timeout = 10000;

				String str = "Message[" + (idx) + "]";
				// Create the message
				TextMessage msg = queueSession.createTextMessage(str);
				// Publish it
				msg.setJMSReplyTo(tmpque);
				sender.send(msg);
				System.out.println("Sent a message \"" + str + "\" to queue \"" + mQueue + "\" and await answer on " + tmpqueName);

				rcvdMsg = tmpQRcv.receive(timeout); // Only waits a certain time
													// (max 10s.)
				if (rcvdMsg != null) {
					System.err.println("Received a message of type " + ((TextMessage) rcvdMsg).getText());
				}

				Thread.sleep(100);
			}
			queueSession.close();
			queueConnection.stop();

		} catch (Exception pEx) {
			System.err.println("Got a JMS exception: " + pEx);
		}
	}

	public static void main(String[] pArgs) {
		JMSUtil.init(pArgs);
		SyncMsgQueueSender app = new SyncMsgQueueSender();
		app.run();
		System.exit(0);
	}
}