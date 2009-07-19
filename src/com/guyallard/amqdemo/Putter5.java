/**
 * A short demonstration of using Active MQ (http://www.apache.org) as 
 * a JMS messaging system.
 */
package com.guyallard.amqdemo;
/**
 * 
 */
import java.util.Date;
// import java.util.Properties;
//
import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
//
import org.apache.activemq.ActiveMQConnectionFactory;
//
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * 
 * @author Guy Allard
 * @since December 2007
 *
 */
public class Putter5 {
	/**
	 * Logger instance. 
	 */
	private static final Log LOG = LogFactory.getLog(Putter5.class);
	/**
	 * A parameter for the class.
	 */
	private int num_messages = 10;
	/**
	 * Produce JMS messages.
	 */
	public void go()
	{
		String work = GlobalData.props.getProperty("putter.num.messages");
		if (work != null)
		{
			num_messages = new Integer(work).intValue();			
		}
		LOG.info("Number of messages to put: " + num_messages);
		//
		// Define initial access data.
		//
		String user = GlobalData.props.getProperty("user.name");
		String pass = GlobalData.props.getProperty("user.password");
		String broker = GlobalData.props.getProperty("broker.url");
		String queName = GlobalData.props.getProperty("putter.queue");
		LOG.info("User: " + user);
		LOG.info("Pass: " + pass);
		LOG.info("Broker: " + broker);
		LOG.info("Queue Name: " + queName);
		//
		Connection conn = null;
		Session sess = null;
		//
		// Attempt to send JMS messages
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
			// Create and configure the message producer.
			//
			MessageProducer producer = sess.createProducer(destination);
			LOG.info("producer created");
			producer.setDeliveryMode(DeliveryMode.PERSISTENT);
			LOG.info("delivery mode set");
			//
			// Create and produce messages.
			//
			for (int mc = 0; mc < num_messages; mc++)
			{
				String smessage = "Message " + (mc + 1) + " " + new Date();
				TextMessage message = sess.createTextMessage(smessage);
				LOG.info("message created: <" + smessage + ">");
				producer.send(message);
			}
			LOG.info("messages sent");
		}
		//
		// Handle any exceptions thrown.  Not a best practices way, but
		// used for this demo.
		//
		catch(Exception ex) {
			LOG.error("Exception caught: ", ex);
		}
		//
		// And at the end, close the connection, which does the right thing
		// with the session.
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
