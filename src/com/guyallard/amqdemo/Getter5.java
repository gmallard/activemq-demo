/**
 * A short demonstration of using Active MQ (http://www/apache.org) as 
 * a JMS messaging system.
 */
package com.guyallard.amqdemo;
/**
 * 
 */
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * 
 * @author Guy Allard
 * @since December 2007
 *
 */
public class Getter5 {
	/**
	 * Logger instance.
	 */
	private static final Log LOG = LogFactory.getLog(Getter5.class);
	/**
	 * Class parameter.
	 */
	private static long waitTime = 10000L;	// milliseconds
	/**
	 * Consume messages until it appears there are no more to be
	 * had.
	 */
	public void go()
	{
		//
		// Initialize parameters.
		//
		String user = ActiveMQConnection.DEFAULT_USER;
		String pass = ActiveMQConnection.DEFAULT_PASSWORD;
		String broker = ActiveMQConnection.DEFAULT_BROKER_URL;
		String queName = "GMA.Q01";
		LOG.info("User: " + user);
		LOG.info("Pass: " + pass);
		LOG.info("Broker: " + broker);
		LOG.info("Queue Name: " + queName);		
		//
		Connection conn = null;
		Session sess = null;
		//
		// Try to consume messages.
		//
		try {
			//
			// Create the connection and start it.
			//
			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(user, pass, broker);			
			conn = connectionFactory.createConnection();
			conn.start();
			LOG.info("connection started");
			//
			// Create the session
			//
			sess = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
			LOG.info("session created");
			//
			// Create the destination. NOTE!! The documentation for JMS
			// says this way to create destinations should *not* normally
			// be used.  Since this example does not use JNDI to obtain
			// destinations, this is how we do it here.
			//
			Destination destination = sess.createQueue(queName);
			LOG.info("destination created");
			//
			// Create the consumer.
			//
			MessageConsumer consumer = sess.createConsumer(destination);
			LOG.info("consumer created");
			//
			// Consume messages until there are no more.
			//
			Message msg = null;
			boolean cont = true;
			while (cont) {
				if (waitTime > 0L)
				{
					msg = consumer.receive(waitTime);
				} else {
					msg = consumer.receive();
				}
				if (msg != null) {
					LOG.info("message received");
					TextMessage tmsg = (TextMessage)msg;
					LOG.info("Message: <" + tmsg.getText() + ">");
				} else {
					LOG.info("Receive Failed, null message after wait: "
							+ waitTime + "(ms)");
					cont = false;
				}
			}
		}
		//
		// Catch and handle any exceptions.
		//
		catch(Exception ex) {
			LOG.error("Exception caught: ", ex);
		}
		//
		// Clean up the connection.
		//
		finally {
			try { 
				conn.close();
				LOG.info("connection closed");
			} catch (Throwable ignore) {
			}
		}

	}
} // end of class

