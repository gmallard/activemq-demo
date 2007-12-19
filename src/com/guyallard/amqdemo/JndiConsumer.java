package com.guyallard.amqdemo;

import java.util.Properties;
import java.util.Hashtable;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Queue;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * 
 * @author gallard
 *
 */
public class JndiConsumer {

    private static final Log LOG = LogFactory.getLog(JndiConsumer.class);

	public void go(String jndiName)
	{
        ConnectionFactory connectionFactory = null;
        Connection connection = null;
        Session session = null;
        Destination destination = null;
        MessageConsumer consumer = null;
        String destinationName = null;

        destinationName = jndiName;
        LOG.info("Receive name is " + destinationName);
	
		Properties props = new Properties();
		props.setProperty(Context.INITIAL_CONTEXT_FACTORY,"org.apache.activemq.jndi.ActiveMQInitialContextFactory");
		props.setProperty(Context.PROVIDER_URL,"tcp://localhost:61616");
		javax.naming.Context jndiContext = null;
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
            Hashtable ht = jndiContext.getEnvironment();
            LOG.info("Hash Table Env1: " + ht);
            // Watch the case of the lookup key
            connectionFactory = (ConnectionFactory)jndiContext.lookup("connectionFactory");
            destination = (Destination)jndiContext.lookup(destinationName);
            if (destination instanceof Queue)
            {
            	Queue q = (Queue)destination;
            	LOG.info("Destination QName is: " + q.getQueueName());
            } else {
            	LOG.warn("dest is not a queue!");
            }
        }
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
            connection = connectionFactory.createConnection();
            connection.start();		// Important!! to receive anything!!
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            consumer = session.createConsumer(destination);
            Message message = null;
            while (true) {
            	message = consumer.receive(10000L);
            	if (message == null) break;
            	TextMessage tmsg = (TextMessage)message;
            	LOG.info("Received Message: <" + tmsg.getText() + ">");
            }
            connection.stop();
        } catch (JMSException e) {
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
}
