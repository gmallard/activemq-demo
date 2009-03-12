package com.guyallard.amqdemo.runnables;
/**
 * 
 */
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;
//
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * 
 * @author Guy Allard
 * @since 2007.12.20
 *
 */
public class RunnableProducer extends RunnableJMSClient {
	/**
	 * Logger instance
	 */
    private static final Log LOG = LogFactory.getLog(RunnableProducer.class);
    /**
     * 
     * @param connection
     * @param destination
     */
    public RunnableProducer(Connection connection, Destination destination)
    {
    	super(connection, destination);
    }

    private final static int NUM_MESSAGES = 4;	// for now
    
    /**
     * 
     */
	public void run() {
		LOG.info("starts");
        /*
         * Create session from connection; false means
         * session is not transacted. Create sender and text message. Send
         * messages, varying text slightly.
         * Finally, close connection.
         */
        try 
        {
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer producer = session.createProducer(destination);
            TextMessage message = session.createTextMessage();
            String me = Thread.currentThread().getName();
            for (int i = 0; i < NUM_MESSAGES; i++) 
            {
                message.setText("This is message " + (i + 1) + " " +
                		me);
                LOG.info("Sending message: <" + message.getText() + ">");
                producer.send(message);
            }
            /*
             * Alternately, Send a non-text control message indicating end of messages.
             * Must be supported by the consumer!! Not used in this example!!
             */
            // producer.send(session.createMessage());
            session.close();
        }
        //
        // Handle exceptions.
        //
        catch (JMSException jex) 
        {
            LOG.info("Exception occurred: " + jex);
        }
		LOG.info("ends");
	}
}

