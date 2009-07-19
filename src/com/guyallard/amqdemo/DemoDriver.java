/**
 * A short demonstration of using Active MQ (http://www.apache.org) as 
 * a JMS messaging system.
 */
package com.guyallard.amqdemo;
/**
 * 
 */
import java.io.InputStream;
import java.io.IOException;
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
	 * No args CTOR
	 */
	public DemoDriver()
	{
		//
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
		String[] args = new String[2];
		args[0] = GlobalData.props.getProperty("jndi.queue");
		args[1] = GlobalData.props.getProperty("jndi.num.messages");
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
