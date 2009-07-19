package com.guyallard.amqdemo.runnables;
/**
 * 
 */
import javax.jms.Connection;
import javax.jms.Destination;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * 
 * @author Guy Allard
 * @since 2007.12.20
 *
 */
public abstract class RunnableJMSClient implements Runnable {
	/**
	 * Logger instance
	 */
    private static final Log LOG = LogFactory.getLog(RunnableJMSClient.class);
    /**
     * 
     */
    protected Connection connection;
    /**
     * 
     */
    protected Destination destination;
    /**
     * 
     */
    public RunnableJMSClient()
    {
    	super();
    }
    /**
     * 
     * @param connection
     * @param destination
     */
    public RunnableJMSClient(Connection connection, Destination destination)
    {
    	this();
    	this.connection = connection;
    	this.destination = destination;
    	LOG.info("construction completes");
    }
    /**
     * 
     */
	public void run() {
		// LOG.info("runs .....");
	}
}
