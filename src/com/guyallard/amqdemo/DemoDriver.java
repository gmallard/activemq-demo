/**
 * A short demonstration of using Active MQ (http://www/apache.org) as 
 * a JMS messaging system.
 */
package com.guyallard.amqdemo;
/**
 * 
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * 
 * @author Guy Allard
 * @since December 2007
 *
 */
public class DemoDriver {
	/**
	 * Logger instance.
	 */
    private static final Log LOG = LogFactory.getLog(DemoDriver.class);
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DemoDriver driver = new DemoDriver();
		driver.go();
	}
	/**
	 * Drive the demonstration.
	 */
	private void go()
	{
		LOG.info("DemoDriver starts");
		//
		// Non-JNDI putter.
		//
		Putter5 p5 = new Putter5();
		p5.go();
		//
		// Non-JNDI getter.
		//
		Getter5 g5 = new Getter5();
		g5.go();
		//
		// JNDI Consumer/Producer parameters.
		//
		String[] args = {
			"MyQueue", "5"	
		};
		//
		// JNDI Producer.
		//
		JndiProducer jp = new JndiProducer();
		jp.go(args);
		//
		// JNDI Consumer.
		//
		JndiConsumer jc = new JndiConsumer();
		jc.go(args[0]);
		//
		// FINIS!
		//
		LOG.info("DemoDriver ends");
	}
} // end of class
