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
	
	final static Logger log = Logger.getLogger(AMQPSender.class);
	private Connection connection;
	private Session sendSession;
	private MessageProducer sender;

	 public AMQPSender() throws Exception {
	     // Configure JNDI environment
		 String sbPropertyFile = "./webapps/ROOT/WEB-INF/resources/sb.properties";
		 	//the sb.properties file is packaged inside the .WAR
		 	//This path WORKS for Azure WebApp service (configured with a Tomcat + Java).
		 	//will NOT work when deployed under Eclipse WTP and in stand alone Tomcat. 
		 
		 log.debug("AbsolutePath=" + new File(sbPropertyFile).getAbsolutePath());
		 
		 URL resource2 = this.getClass().getClassLoader().getResource("resources/sb.properties");
		 String sPath = Paths.get(resource2.toURI()).toFile().getAbsolutePath();
		 java.io.InputStream is = this.getClass().getResourceAsStream("sb.properties");
		 URL resource = this.getClass().getResource("resources/sb.properties");
		 java.io.FileInputStream fis;
		 
		 log.debug("sb.properties AbsolutePath=" + Paths.get(resource.toURI()).toFile().getAbsolutePath());
		 
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

//	     if (runReceiver) {
//	         // Create receiver-side Session, MessageConsumer,and MessageListener
//	         receiveSession = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
//	         receiver = receiveSession.createConsumer(queue);
//	         receiver.setMessageListener(this);
//	         connection.start();
//	     }
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
