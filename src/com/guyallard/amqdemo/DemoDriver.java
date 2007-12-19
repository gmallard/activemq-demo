package com.guyallard.amqdemo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * 
 * @author gallard
 *
 */
public class DemoDriver {

    private static final Log LOG = LogFactory.getLog(DemoDriver.class);
    
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DemoDriver driver = new DemoDriver();
		driver.go();
	}
	
	private void go()
	{
		LOG.info("DemoDriver starts");
		//
		Putter5 p5 = new Putter5();
		p5.go();
		//
		Getter5 g5 = new Getter5();
		g5.go();
		//
		String[] args = {
			"MyQueue", "5"	
		};
		JndiProducer jp = new JndiProducer();
		jp.go(args);
		//
		JndiConsumer jc = new JndiConsumer();
		jc.go(args[0]);
		LOG.info("DemoDriver ends");
	}

}
