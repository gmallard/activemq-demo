/**
 * A short demonstration of using Active MQ (http://www.apache.org) as 
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
//
import org.apache.activemq.ActiveMQConnectionFactory;
//
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//
import java.io.InputStream;
import java.io.IOException;
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
	private long waitTime = 1000L;	// milliseconds
	/**
	 * Consume messages until it appears there are no more to be
	 * had.
	 */
	public void go()
	{
		String work = GlobalData.props.getProperty("getter.wait");
		if (work != null)
		{
			waitTime = new Long(work).longValue();			
		}
		//
		// Initialize parameters.
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
	
	public void init()
	{
		//
		try {
			InputStream is = this.getClass().getResourceAsStream("/amqdemo.properties");
			GlobalData.props.load( is );
		}
		catch(IOException ioex) {
			LOG.error("IOE: amqdemo.properties", ioex);
			// percolate
		}
	}
	
	public static void main(String[] args)
	{
		Getter5 g5 = new Getter5();
		g5.init();
		g5.go();
	}
} // end of class

