package com.guyallard.amqdemo.runnables;
/**
 * 
 */
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.Properties;
//
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
//
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//
import com.guyallard.amqdemo.GlobalData;
/**
 * 
 * @author Guy Allard
 * @since 2007.12.20
 *
 */
public class RunnableDriver {
	/**
	 * Logger instance
	 */
    private static final Log LOG = LogFactory.getLog(RunnableDriver.class);
    /**
     * 
     */
    private Properties props = new Properties();
    /**
     * 
     */
    private Connection connection = null;
     /**
     * 
     */
    private Destination destination = null;
    /**
     * 
     */
    private String destinationJndiName = "MyQueue";
    /**
     * No argument constructor. 
     */
    public RunnableDriver()
    {
		try {
			InputStream is = this.getClass().getResourceAsStream("/amqdemo.properties");
			GlobalData.props.load( is );
		}
		catch(IOException ioex) {
			LOG.error("IOE: amqdemo.properties", ioex);
			// percolate
		}
    }
    /**
	 * @param args
	 */
	public static void main(String[] args) {
		RunnableDriver rdrvr = new RunnableDriver();
		rdrvr.go();
		System.exit(0);
	}
	/**
	 * 
	 */
	private void go()
	{
		LOG.info("starts");
		init();
		LOG.info("Initialization Complete!");
		//
		int numThreads = 4;		// for now
		List<Thread> producerList = new ArrayList<Thread>(numThreads);
		LOG.info("starting producers");
		startProducers(producerList, numThreads);
		//
		numThreads = 2;
		List<Thread> consumerList = new ArrayList<Thread>(numThreads);
		LOG.info("starting consumers");
		startConsumers(consumerList, numThreads);
		//
		LOG.info("starting producer waits");
		waitForAll(producerList);
		LOG.info("starting consumer waits");
		waitForAll(consumerList);
		//
		destroy();
		LOG.info("ends");
	}
	/**
	 * 
	 */
	private void init()
	{
		props.setProperty(Context.INITIAL_CONTEXT_FACTORY,"org.apache.activemq.jndi.ActiveMQInitialContextFactory");
		props.setProperty(Context.PROVIDER_URL,"tcp://localhost:61616");
		Context jndiContext = null;
		ConnectionFactory connectionFactory = null;
		try {
			jndiContext = new InitialContext(props);
            connectionFactory = (ConnectionFactory)jndiContext.lookup("connectionFactory");
            destination = (Destination)jndiContext.lookup(destinationJndiName);
		} 
		catch(NamingException nex) {
			LOG.error("JNDI/JMS Initialization Failed! ", nex);
			System.exit(1);
		}
		//
		try {
			connection = connectionFactory.createConnection();
			//
			// If you fail to start the connection, MessageProducers will
			// produce like mad.  MessageConsumers will not see a thing!!
			//
			connection.start();	// Do NOT forget this!!!!!!
		} 
		catch (JMSException jex) {
			LOG.error("JMS Connection Creation Failed! ", jex);
			System.exit(1);
		}
	}
	/**
	 * 
	 * @param producerList
	 */
	private void startProducers(List<Thread> producerList, int numThreads)
	{
		for (int i = 0; i < numThreads; i ++)
		{
			LOG.info("Starting producer: " + (i+1));
			Runnable prunner = new RunnableProducer(connection, destination);
			Thread pthread = new Thread(prunner);
			pthread.start();
			producerList.add(pthread);
		}
	}
	/**
	 * 
	 * @param consumerList
	 */
	private void startConsumers(List<Thread> consumerList, int numThreads)
	{
		for (int i = 0; i < numThreads; i ++)
		{
			LOG.info("Starting consumer: " + (i+1));
			Runnable crunner = new RunnableConsumer(connection, destination);
			Thread cthread = new Thread(crunner);
			cthread.start();
			consumerList.add(cthread);
		}
	}
	/**
	 * 
	 * @param threadList
	 */
	private void waitForAll(List<Thread> threadList)
	{
		for (Thread thread : threadList)
		{
			try 
			{
				thread.join();				
			} 
			catch(InterruptedException iex)
			{
				// ignore
			}

		}
	}
	/**
	 * 
	 */
	private void destroy()
	{
		try
		{
			connection.stop();
		}
		catch (JMSException jex)
		{
			LOG.error("JMSException on stop: ", jex);
		}
	}
}
