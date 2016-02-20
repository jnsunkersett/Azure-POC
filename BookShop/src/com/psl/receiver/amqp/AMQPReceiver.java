package com.psl.receiver.amqp;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Hashtable;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.psl.service.BookService;

public class AMQPReceiver implements Receiver {
	
	final static Logger log = Logger.getLogger(AMQPReceiver.class);
	private Connection connection;
	private Session recvSession;
	private MessageConsumer receiver;
	static int count = 0;

	 public AMQPReceiver(BookService bookService) throws Exception 
	 {	
		 try {
		 File f = new File("AlphaBeta.txt");
		 log.debug("Entering constructor of AMQPReceiver. Count " + count);
		 AMQPReceiver.count++;
//		 log.debug("Path of AlphaBeta.txt =" + f.getAbsolutePath());
		 
//		 String sbPropertyFile = System.getProperty("catalina.home") + "\\webapps\\ROOT\\WEB-INF\\resources\\sb.properties";
//		 log.debug("The sbPropertyFile=" + sbPropertyFile);
//		 log.debug("java.classpath=" + System.getProperty("java.classpath"));
//		 log.debug("java.path=" + System.getProperty("java.path"));
		 
//		 String sbPropertyFile = "./webapps/ROOT/WEB-INF/resources/sb.properties"; 
		 	//the sb.properties file is packaged inside the .WAR
		 	//This path WORKS for Azure WebApp service (configured with a Tomcat + Java).
		 	//will NOT work when deployed under Eclipse WTP and in stand alone Tomcat. 
		 
//		 log.debug("AbsolutePath=" + new File(sbPropertyFile).getAbsolutePath());
		 
		 URL resource = (URL) this.getClass().getClassLoader().getResource("sb.properties");
		 
		 String sbPropertyFile = new File(this.getClass().getClassLoader().getResource("sb.properties").toURI()).getAbsolutePath();
		 
		 log.debug("sb.properties AbsolutePath=" + Paths.get(resource.toURI()).toFile().getAbsolutePath());
		 
	     // Configure JNDI environment
	     Hashtable<String, String> env = new Hashtable<String, String>();
	     env.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.qpid.amqp_1_0.jms.jndi.PropertiesFileInitialContextFactory");
	     env.put(Context.PROVIDER_URL, sbPropertyFile);
	     Context context = new InitialContext(env);

	     // Lookup ConnectionFactory and Queue
	     ConnectionFactory cf = (ConnectionFactory) context.lookup("SBCF");
	     Destination queue = (Destination) context.lookup("MYQUEUE");

	     // Create Connection
	     connection = cf.createConnection();

	     // Create sender-side Session and MessageProducer
	     recvSession = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
	     receiver = recvSession.createConsumer(queue);
	     receiver.setMessageListener(bookService) ;
         connection.start();
//	     if (runReceiver) {
//	         // Create receiver-side Session, MessageConsumer,and MessageListener
//	         receiveSession = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
//	         receiver = receiveSession.createConsumer(queue);
//	         receiver.setMessageListener(this);
//	         connection.start();
//	     }
         log.debug("Exiting constructor of AMQPReceiver");
		 } catch (Exception e) {
			 log.error("Exception when init AMQP Receiver", e);
		 }
	 }
}
