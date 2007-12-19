package com.guyallard.amqdemo;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
/**
 * 
 * @author gallard
 *
 */
public class Getter5 {

	private static final Log LOG = LogFactory.getLog(Getter5.class);
	
	private static long waitTime = 10000L;	// milliseconds
	
	public void go()
	{
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
		try {
			// Create the connection.
			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(user, pass, broker);			
			conn = connectionFactory.createConnection();
			conn.start();
			LOG.info("connection started");
			// Create the session
			sess = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
			LOG.info("session created");
			Destination destination = sess.createQueue(queName);
			LOG.info("destination created");
			MessageConsumer consumer = sess.createConsumer(destination);
			LOG.info("consumer created");
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
		} catch(Exception ex) {
			LOG.error("Exception caught: ", ex);
		} finally {
			try { 
				conn.close();
				LOG.info("connection closed");
			} catch (Throwable ignore) {
			}
		}

	}
}
