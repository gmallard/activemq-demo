package com.guyallard.amqdemo;

import java.util.Date;
import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class Putter5 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

			System.out.println("Putter starts ....");
			Putter5 p = new Putter5();
			//
			p.go();
			//
			System.out.println("Putter ends ....");
		}
		
		private void go()
		{
			String user = ActiveMQConnection.DEFAULT_USER;
			String pass = ActiveMQConnection.DEFAULT_PASSWORD;
			String broker = ActiveMQConnection.DEFAULT_BROKER_URL;
			// String queName = "widgets";
			String queName = "GMA.Q01";
			System.out.println("User: " + user);
			System.out.println("Pass: " + pass);
			System.out.println("Broker: " + broker);
			System.out.println("Queue Name: " + queName);
			//
			Connection conn = null;
			Session sess = null;
			try {
				// Create the connection.
				ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(user, pass, broker);			
				conn = connectionFactory.createConnection();
				conn.start();
				System.out.println("connection started");
				// Create the session
				sess = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
				System.out.println("session created");
				Destination destination = sess.createQueue(queName);
				System.out.println("destination created");
				MessageProducer producer = sess.createProducer(destination);
				System.out.println("producer created");
				producer.setDeliveryMode(DeliveryMode.PERSISTENT);
				System.out.println("delivery mode set");
				String smessage = "Message 01: " + new Date();
				TextMessage message = sess.createTextMessage(smessage);
				System.out.println("message created: " + smessage);
				producer.send(message);
				System.out.println("message sent");
			} catch(Exception ex) {
				ex.printStackTrace();
			} finally {
				try { 
					conn.close();
					System.out.println("connection closed");
				} catch (Throwable ignore) {
				}
			}
	}
}
