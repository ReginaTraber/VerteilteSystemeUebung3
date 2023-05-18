//
// (c) NDS/SE Distributed Computing 2002-2007
//   Uebung: JMSEinstieg
//
package ch.ost.mas.cds.jmstutorial.ps;

import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;

import ch.ost.mas.cds.jmstutorial.util.JMSUtil;

public class MsgProducer {
	private String mTopic;

	public MsgProducer() {
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
			// Topic topic = topicSession.createTemporaryTopic();
			System.err.println(topic.getTopicName());
			// Wait for a while before publishing
			Thread.sleep(10000);
			TopicPublisher topicPublisher = topicSession.createPublisher(topic);

			topicConnection.start();

			for (int idx = 1; idx <= count; idx++) {
				String str = "Message[" + (idx) + "]";
				// Create the message
				TextMessage msg = topicSession.createTextMessage(str);
				// Publish it
				topicPublisher.publish(msg);
				System.out.println("Published a message \"" + str + "\" on topic \"" + mTopic + "\"");
				Thread.sleep(1000);
			}
			topicSession.close();
			topicConnection.stop();
		} catch (Exception pEx) {
			System.err.println("Got a JMS exception: " + pEx);
		}
	}

	public static void main(String[] pArgs) {
		JMSUtil.init(pArgs);
		MsgProducer app = new MsgProducer();
		app.run();
		System.exit(0);
	}
}
