package com.guyallard.amqdemo;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class Getter5 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Getter5 g = new Getter5();
		g.go();
	}

	
	private static long waitTime = 10000L;	// milliseconds
	
	private void go()
	{
		String user = ActiveMQConnection.DEFAULT_USER;
		String pass = ActiveMQConnection.DEFAULT_PASSWORD;
		String broker = ActiveMQConnection.DEFAULT_BROKER_URL;
		// String queName = "widgets";
		// String queName = "GMA.Q01";
		String queName = "example.MyQueue";
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
			MessageConsumer consumer = sess.createConsumer(destination);
			System.out.println("consumer created");
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
					System.out.println("message received");
					TextMessage tmsg = (TextMessage)msg;
					System.out.println("Message: <" + tmsg.getText() + ">");
				} else {
					System.out.println("Receive Failed, null message after wait: "
							+ waitTime + "(ms)");
					cont = false;
				}
			}
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
