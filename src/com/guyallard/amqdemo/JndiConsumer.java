/**
 * A short demonstration of using Active MQ (http://www.apache.org) as 
 * a JMS messaging system.
 */
package com.guyallard.amqdemo;
/**
 * 
 */
import java.util.Hashtable;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * 
 * @author Guy Allard
 * @since December 2007
 *
 */
public class JndiConsumer {
	/**
	 * Logger instance.
	 */
    private static final Log LOG = LogFactory.getLog(JndiConsumer.class);
    /**
     * Consume JMS messages, using only JNDI for factory and destination
     * lookup and definition.
     *  
     * @param jndiName
     */
	public void go(String jndiName)
	{
		//
		// Define message object instances.
		//
        ConnectionFactory connectionFactory = null;
        Connection connection = null;
        Session session = null;
        Destination destination = null;
        MessageConsumer consumer = null;
        String destinationName = null;
        //
        destinationName = jndiName;
        LOG.info("Receive name is " + destinationName);
 // There are several ways to code for JNDI lookups.
 // Start Method 1
         /*
 		Properties props = new Properties();
 		props.setProperty(Context.INITIAL_CONTEXT_FACTORY,
 				GlobalData.props.getProperty("broker.factory"));
 		props.setProperty(Context.PROVIDER_URL,
 				GlobalData.props.getProperty("broker.url"));
 		Context jndiContext = null;
 		try {
 			jndiContext = new InitialContext(props);
 		} catch(NamingException nex) {
 			LOG.error("Context Create Failed! ", nex);
 			System.exit(1);
 		}
 		*/
 // End Method 1        
 // Start Method 2
     		Context jndiContext = null;
     		try {
     			jndiContext = new InitialContext();
     		} catch(NamingException nex) {
     			LOG.error("Context Create Failed! ", nex);
     			System.exit(1);
     		}
// End Method 2		
        LOG.info("Context Create Complete!");
        /*
         * Look up connection factory and destination.
         */
        try {
            Hashtable<?,?> ht = jndiContext.getEnvironment();
            LOG.info("Hash Table Env1: " + ht);		// Dump for information review.
            //
            // Look up the connection factory and the destination objects.
            //
            // Watch the case of the lookup key!!!
            //
            connectionFactory = (ConnectionFactory)jndiContext.lookup("connectionFactory");
            destination = (Destination)jndiContext.lookup(destinationName);
            //
            // Write destination info for review/debugging.
            //
            if (destination instanceof Queue)
            {
            	Queue q = (Queue)destination;
            	LOG.info("Destination QName is: " + q.getQueueName());
            } else {
            	LOG.warn("dest is not a queue!");
            }
        }
        //
        // Handle any exceptions thrown
        //
        catch (Exception nex) {
            LOG.info("JNDI API lookup failed: " + nex);
            System.exit(1);
        }
        LOG.info("Factory Lookup Complete");
        /*
         * Create connection. Create session from connection; false means
         * session is not transacted.
         */
        try {
        	//
        	// Create the connection and start it.
        	//
            connection = connectionFactory.createConnection();
            connection.start();		// Important!! to receive anything!!
            //
            // Create the session.
            //
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            //
            // Create the message consumer.
            //
            consumer = session.createConsumer(destination);
            //
            // Receive messages until it appears there are no more being 
            // produced.
            //
            Message message = null;
            while (true) {
            	message = consumer.receive(10000L);
            	if (message == null) break;
            	TextMessage tmsg = (TextMessage)message;
            	LOG.info("Received Message: <" + tmsg.getText() + ">");
            }
            //
            // Stop the connection.
            //
            connection.stop();
        }
        //
        // Catch any JMS exceptions, and handle them (yeah).
        //
        catch (JMSException e) {
            LOG.info("Exception occurred: " + e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                	LOG.info("final exception ignored");
                }
            }
        }
        LOG.info("run complete");
	}
} // end of class
