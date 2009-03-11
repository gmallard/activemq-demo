package com.guyallard.amqdemo.runnables;
/**
 * 
 */
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.MessageConsumer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * 
 * @author Guy Allard
 * @since 2007.12.20
 *
 */
public class RunnableConsumer extends RunnableJMSClient {
	/**
	 * Logger instance
	 */
    private static final Log LOG = LogFactory.getLog(RunnableConsumer.class);
    /**
     * 
     * @param connection
     * @param destination
     */
    public RunnableConsumer(Connection connection, Destination destination)
    {
    	super(connection, destination);    	
    }
    /**
     * 
     */
	public void run() {
		// Start message to log.
		LOG.info("starts");
		try 
		{
			//
			// Create the session.
			//
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			//
			// Create the message consumer.
			//
			MessageConsumer consumer = session.createConsumer(destination);
			//
			// Receive messages until it appears there are no more being 
			// produced.
			//
			Message message = null;
			while (true) {
				message = consumer.receive(10000L);		// 10 secs, for now
				if (message == null) break;
				TextMessage tmsg = (TextMessage)message;
				LOG.info("Received Message: <" + tmsg.getText() + ">");
			}
			session.close();
		}
		catch(JMSException jex)
		{
			LOG.error("Consumer exception: ", jex);
		}
		// End message to log.
		LOG.info("ends");
	}
}
