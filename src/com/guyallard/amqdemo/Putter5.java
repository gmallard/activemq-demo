package com.guyallard.amqdemo;

import java.util.Date;
import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.MessageProducer;
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
public class Putter5 {

    private static final Log LOG = LogFactory.getLog(Putter5.class);
    
		private static final int NUM_MSGS = 10;
		
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
				MessageProducer producer = sess.createProducer(destination);
				LOG.info("producer created");
				producer.setDeliveryMode(DeliveryMode.PERSISTENT);
				LOG.info("delivery mode set");
				for (int mc = 0; mc < NUM_MSGS; mc++)
				{
					String smessage = "Message " + (mc + 1) + " " + new Date();
					TextMessage message = sess.createTextMessage(smessage);
					LOG.info("message created: <" + smessage + ">");
					producer.send(message);
				}
				LOG.info("message sent");
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
