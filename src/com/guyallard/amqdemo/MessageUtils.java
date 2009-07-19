package com.guyallard.amqdemo;
/**
 * A short demonstration of using Active MQ (http://www.apache.org) as 
 * a JMS messaging system.
 */
import javax.jms.Connection;
import javax.jms.JMSException;
//
//
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * 
 */
import org.apache.activemq.ActiveMQConnectionFactory;
/**
 * 
 * @author Guy Allard
 * @since July 2009
 *
 */
public final class MessageUtils {
	/**
	 * Logger instance. 
	 */
	private static final Log LOG = LogFactory.getLog(MessageUtils.class);
	/**
	 * 
	 */
	public static Connection getConnection(String user, String pass, String broker)
	{
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(user, pass, broker);
		Connection conn;
		try {
			conn = connectionFactory.createConnection(); 
		}
		catch(JMSException jex)
		{
			LOG.error("JMSConnCre: ", jex);
			throw new IllegalStateException("Connection create failed");
		}
		return conn;
	}
}
