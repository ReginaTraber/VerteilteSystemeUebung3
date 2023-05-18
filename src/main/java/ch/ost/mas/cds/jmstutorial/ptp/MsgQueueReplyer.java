//
// (c) NDS/SE Distributed Computing 2002-2007
//   Uebung: JMSEinstieg
//
package ch.ost.mas.cds.jmstutorial.ptp;

import javax.jms.DeliveryMode;
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

public class MsgQueueReplyer {
	private String mQueue;

	public MsgQueueReplyer() {
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
			QueueReceiver receiver = queueSession.createReceiver(queue);
			QueueSender replySnd = queueSession.createSender(null);
			replySnd.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

			queueConnection.start();

			for (int idx = 1; idx <= count; idx++) {
				Message msg = receiver.receive();
				Queue replyQue = (Queue) msg.getJMSReplyTo();

				if (msg instanceof TextMessage) {
					System.err.print("Received text message: " + ((TextMessage) msg).getText());
					if (replyQue != null)
						System.err.println(" will reply to " + replyQue.getQueueName());
					else
						System.err.println();

					if (replyQue != null) {
						TextMessage answ = queueSession.createTextMessage("AnswMsg[" + idx + "]");
						replySnd.send(replyQue, answ);
						System.out.println(" Has replied [" + idx + "] to " + replyQue.getQueueName());
					}
				}
			}
			replySnd.close();
			queueSession.close();
			queueConnection.close();
		} catch (Exception pEx) {
			System.err.println("Got a JMS exception: " + pEx);
		}
	}

	public static void main(String[] pArgs) {
		JMSUtil.init(pArgs);
		MsgQueueReplyer app = new MsgQueueReplyer();
		app.run();
		System.exit(0);
	}
}