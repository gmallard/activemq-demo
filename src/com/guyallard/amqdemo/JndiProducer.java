/**
 * A short demonstration of using Active MQ (http://www/apache.org) as 
 * a JMS messaging system.
 */
package com.guyallard.amqdemo;

import java.util.Hashtable;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
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
public class JndiProducer {
	/**
	 * Logger instance
	 */
    private static final Log LOG = LogFactory.getLog(JndiProducer.class);
    /**
     * Produce JMA messages.
     * 
     * @param args
     */
	public void go(String[] args)
	{
		//
		// Define message object instances.
		//
        ConnectionFactory connectionFactory = null;
        Connection connection = null;
        Session session = null;
        Destination destination = null;
        MessageProducer producer = null;
        String destinationName = null;
        final int numMsgs;
        //
        // Check for right parameters.
        //
        if ((args.length < 1) || (args.length > 2)) {
            LOG.info("Usage: java SimpleProducer <destination-name> [<number-of-messages>]");
            System.exit(1);
        }
        destinationName = args[0];
        LOG.info("Destination name is " + destinationName);
        //
        // Get number of messages from parameters.
        //
        if (args.length == 2) {
            numMsgs = (new Integer(args[1])).intValue();
        } else {
            numMsgs = 1;
        }
        //
        // Create the JNDI lookup table.  Note: for AMQ, this primes the 
        // properties from file 'jndi.properties' which must be on the CLASSPATH.
        // The *same* 'jndi.properties' definitions should be on the CLASSPATH
        // of the AMQ server.
        //
		Properties props = new Properties();
		props.setProperty(Context.INITIAL_CONTEXT_FACTORY,"org.apache.activemq.jndi.ActiveMQInitialContextFactory");
		props.setProperty(Context.PROVIDER_URL,"tcp://localhost:61616");
		Context jndiContext = null;
		try {
			jndiContext = new InitialContext(props);
		} catch(NamingException nex) {
			LOG.error("Context Create Failed! ", nex);
			System.exit(1);
		}
        LOG.info("Context Create Complete!");
        /*
         * Look up connection factory and destination.
         */
        try {
        	Hashtable<?,?> ht = jndiContext.getEnvironment();
            LOG.info("Hash Table Env1: " + ht); 	// Information dump.
            //
            // Look up the connection factory and destination.
            //
            // Watch the case of the lookup key!!!
            //
            connectionFactory = (ConnectionFactory)jndiContext.lookup("connectionFactory");
            destination = (Destination)jndiContext.lookup(destinationName);
        }
        catch (NamingException nex) {
            LOG.info("JNDI API lookup failed: " + nex);
            System.exit(1);
        }
        LOG.info("Factory Lookup Complete");
        /*
         * Create connection. Create session from connection; false means
         * session is not transacted. Create sender and text message. Send
         * messages, varying text slightly.
         * Finally, close connection.
         */
        try {
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            producer = session.createProducer(destination);
            TextMessage message = session.createTextMessage();
            for (int i = 0; i < numMsgs; i++) {
                message.setText("This is message " + (i + 1));
                LOG.info("Sending message: <" + message.getText() + ">");
                producer.send(message);
            }

            /*
             * Alternately, Send a non-text control message indicating end of messages.
             * Must be supported by the consumer!! Not used in this example!!
             */
            // producer.send(session.createMessage());
        }
        //
        // Handle exceptions.
        //
        catch (JMSException e) {
            LOG.info("Exception occurred: " + e);
        }
        //
        // Connection clean up.
        //
        finally {
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
}
