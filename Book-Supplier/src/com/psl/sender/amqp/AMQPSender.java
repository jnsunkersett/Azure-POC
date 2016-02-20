package com.psl.sender.amqp;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Hashtable;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.log4j.Logger;

import com.microsoft.azure.storage.file.FileInputStream;
import com.psl.sender.Sender;

public class AMQPSender implements Sender {
	
	private static boolean bInitialized = false;
	
	final static Logger log = Logger.getLogger(AMQPSender.class);
	private Connection connection;
	private Session sendSession;
	private MessageProducer sender;

	public AMQPSender() throws Exception {
		if (bInitialized)
			log.info("The JNDI Context for Azure Service Bus AMQP is already initialized.");
		else
			initializeAMQP();
	}

	
	 public void initializeAMQP()  
	 {
		 try 
		 {
		     // Configure JNDI environment

			 log.info("Entering initializeAMQP");
			 
			 URL resource = (URL) this.getClass().getClassLoader().getResource("sb.properties");
			 
			 String sbPropertyFile = new File(this.getClass().getClassLoader().getResource("sb.properties").toURI()).getAbsolutePath();
			 
			 log.debug("sb.properties AbsolutePath=" + sbPropertyFile);
			 
		     Hashtable<String, String> env = new Hashtable<String, String>();
		     env.put(Context.INITIAL_CONTEXT_FACTORY, 
		             "org.apache.qpid.amqp_1_0.jms.jndi.PropertiesFileInitialContextFactory");
		     env.put(Context.PROVIDER_URL, sbPropertyFile);
		     Context context = new InitialContext(env);
	
		     // Lookup ConnectionFactory and Queue
		     ConnectionFactory cf = (ConnectionFactory) context.lookup("SBCF");
		     Destination queue = (Destination) context.lookup("MYQUEUE");
	
		     // Create Connection
		     connection = cf.createConnection();
	
		     // Create sender-side Session and MessageProducer
		     sendSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		     sender = sendSession.createProducer(queue);
		     
		     AMQPSender.bInitialized = true;

		 } catch (Exception e) {
			 log.error("Exception occured during AMQP initialization", e);
		 }

		 log.info("Exiting initializeAMQP");
	 }
	 
	 public void sendMessage(String stringToSend) throws JMSException {
	     TextMessage message = sendSession.createTextMessage();
	     message.setText(stringToSend);
//	     long randomMessageID = randomGenerator.nextLong() >>>1;
//	     message.setJMSMessageID("ID:" + randomMessageID);
	     sender.send(message);
	     log.debug("Sent message : [" + stringToSend + "]");
	 }
	 

}
