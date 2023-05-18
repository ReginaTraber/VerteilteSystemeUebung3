//
// (c) NDS/SE Distributed Computing 2002-2007
//   Uebung: JMSEinstieg
//
package ch.ost.mas.cds.jmstutorial.ptp;

import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;

import ch.ost.mas.cds.jmstutorial.util.JMSUtil;

public class MsgQueueReceiver {
	private String mQueue;

	public MsgQueueReceiver() {
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

			queueConnection.start();

			for (int idx = 1; idx <= count; idx++) {
				Message msg = receiver.receive();
				if (msg instanceof TextMessage) {
					System.out.println("Received text message: " + ((TextMessage) msg).getText());
				}
			}
			queueSession.close();
			queueConnection.stop();

		} catch (Exception pEx) {
			System.err.println("Got a JMS exception: " + pEx);
		}
	}

	public static void main(String[] pArgs) {
		JMSUtil.init(pArgs);
		MsgQueueReceiver app = new MsgQueueReceiver();
		app.run();
		System.exit(0);
	}
}