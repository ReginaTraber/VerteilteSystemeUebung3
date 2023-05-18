//
// (c) MAS/SE Distributed Computing 2002-2007
//   Uebung: JMSEinstieg
//
package ch.ost.mas.cds.jmstutorial.ps;

import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;

import ch.ost.mas.cds.jmstutorial.util.JMSUtil;

public class MsgConsumer {
	private String mTopic;

	public MsgConsumer() {
		mTopic = JMSUtil.getInstance().getDestination("T.CDS.");
	}

	private void run() {
		TopicConnectionFactory topicConnectionFactory = JMSUtil.getInstance().getTopicConnectionFactory();

		int count = JMSUtil.getInstance().findArgVal(JMSUtil.COUNT);

		if (count <= 0) {
			count = 100;
		}

		if (topicConnectionFactory == null) {
			return;
		}

		try {
			TopicConnection topicConnection = topicConnectionFactory.createTopicConnection();

			TopicSession topicSession = topicConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);

			Topic topic = topicSession.createTopic(mTopic);

			TopicSubscriber topicSubscriber = topicSession.createSubscriber(topic);

			topicConnection.start();

			for (int idx = 1; idx <= count; idx++) {
				Message msg = topicSubscriber.receive();
				if (msg instanceof TextMessage) {
					System.out.println("Received text message: " + ((TextMessage) msg).getText());
				}
			}
			topicSession.close();
			topicConnection.stop();
		} catch (Exception pEx) {
			System.err.println("Got a JMS exception: " + pEx);
		}
	}

	public static void main(String[] pArgs) {
		JMSUtil.init(pArgs);
		MsgConsumer app = new MsgConsumer();
		app.run();
		System.exit(0);
	}
}