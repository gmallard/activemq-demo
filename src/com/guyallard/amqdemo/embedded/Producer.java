package com.guyallard.amqdemo.embedded;
/**
 * 
 */
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
//
import org.apache.activemq.broker.BrokerService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * 
 * @author Guy Allard
 * @since 2007.12.20
 */
public final class Producer {
	/**
	 * A logger instance.
	 */
    private static final Log LOG = LogFactory.getLog(Producer.class);
    /**
     * No argument constructor. 
     */
    private Producer() {
    }
    /**
     * @param args the destination name to send to and optionally, the number of
     *                messages to send
     */
    public static void main(String[] args) {
    	BrokerService broker = null;
    	try {
    		// broker = BrokerFactory.createBroker(new URI("xbean:activemq.xml"));
    		broker = new BrokerService();
    		broker.addConnector("tcp://localhost:61616");    		
    		broker.start();
    	} catch(Exception ex) {
    		LOG.error("Broker create failed: ", ex);
    		System.exit(4);
    	}
    	LOG.info("broker is running");
    	LOG.info("connURI: " + broker.getVmConnectorURI());
    	//
        Context jndiContext = null;
        ConnectionFactory connectionFactory = null;
        Connection connection = null;
        Session session = null;
        Destination destination = null;
        MessageProducer producer = null;
        String destinationName = null;
        final int numMsgs;
        //
        if ((args.length < 1) || (args.length > 2)) {
            LOG.info("Usage: java Producer <destination-name> [<number-of-messages>]");
            System.exit(1);
        }
        destinationName = args[0];
        LOG.info("Destination name is " + destinationName);
        if (args.length == 2) {
            numMsgs = (new Integer(args[1])).intValue();
        } else {
            numMsgs = 1;
        }
        /*
         * Create a JNDI API InitialContext object
         */
        try {
            jndiContext = new InitialContext();
        } catch (NamingException e) {
            LOG.info("Could not create JNDI API context: " + e.toString());
            System.exit(1);
        }
        /*
         * Look up connection factory and destination.
         */
        try {
            // connectionFactory = (ConnectionFactory)jndiContext.lookup("ConnectionFactory");
            connectionFactory = (ConnectionFactory)jndiContext.lookup("connectionFactory");
            destination = (Destination)jndiContext.lookup(destinationName);
            // destination = (Destination)jndiContext.lookup("jms/"+destinationName);
        } catch (NamingException e) {
            LOG.info("JNDI API lookup failed: " + e);
            System.exit(1);
        }
        /*
         * Create connection. Create session from connection; false means
         * session is not transacted. Create sender and text message. Send
         * messages, varying text slightly. Send end-of-messages message.
         * Finally, close connection.
         */
        try {
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            producer = session.createProducer(destination);
            TextMessage message = session.createTextMessage();
            for (int i = 0; i < numMsgs; i++) {
                message.setText("This is message " + (i + 1));
                LOG.info("Sending message: " + message.getText());
                producer.send(message);
            }
            /*
             * Send a non-text control message indicating end of messages.
             */
            producer.send(session.createMessage());
        } catch (JMSException e) {
            LOG.info("Exception occurred: " + e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                }
            }
        }
    	/*
    	 * Stop the broker.
    	 * Ignore any exceptions. 
    	 */
    	try {
    		LOG.info("starting stop");
    		broker.stop();
    	} catch(Exception ex) {
    		LOG.error("Broker stop failed: ", ex);
    	}
    	LOG.info("run complete");
   	}
}
