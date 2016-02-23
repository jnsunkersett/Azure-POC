package com.psl.dao.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.windowsazure.Configuration;
import com.microsoft.windowsazure.exception.ServiceException;
import com.microsoft.windowsazure.services.servicebus.ServiceBusConfiguration;
import com.microsoft.windowsazure.services.servicebus.ServiceBusContract;
import com.microsoft.windowsazure.services.servicebus.ServiceBusService;
import com.microsoft.windowsazure.services.servicebus.models.BrokeredMessage;
import com.microsoft.windowsazure.services.servicebus.models.ReceiveMessageOptions;
import com.microsoft.windowsazure.services.servicebus.models.ReceiveMode;
import com.microsoft.windowsazure.services.servicebus.models.ReceiveQueueMessageResult;
import com.psl.dao.BookDao;
import com.psl.hibernate.util.HibernateUtil;
import com.psl.model.Book;

public class BookDaoImpl implements BookDao {

	final static Logger logger = Logger.getLogger(BookDaoImpl.class);
	
	@Autowired
	private java.util.Properties properties;
	// JDBC driver name and database URL
	// Define the connection-string with your values

	private List<Book> getBooksFromSBQueue(){
		
		logger.info("In getBooksFromSBQueue() of BookDaoImpl");
		String namespace = properties.getProperty("namesapce");
		String sasKeyName = properties.getProperty("sasKeyName");
		String sasKeyValue = properties.getProperty("sasKey");
		String serviceBusRootUri = properties.getProperty("serviceBusRootUri");
		String queueName = properties.getProperty("queueName");
		Configuration config = ServiceBusConfiguration
				.configureWithSASAuthentication(namespace, sasKeyName,
						sasKeyValue, serviceBusRootUri);
		ServiceBusContract service = ServiceBusService.create(config);
		String s = null;
		String name = null;
		String author = null;
		double price = 0;
		String blobPictureUrl = null;
		
		List<Book> bookList = new ArrayList<>();
		JSONParser parser = new JSONParser();
		Object obj = null;
		try {
		ReceiveMessageOptions opts = ReceiveMessageOptions.DEFAULT;
		opts.setReceiveMode(ReceiveMode.PEEK_LOCK);
		while (true) {
			ReceiveQueueMessageResult resultQM = service.receiveQueueMessage(queueName, opts);
			BrokeredMessage message = resultQM.getValue();
			if (message != null && message.getMessageId() != null) {
				BufferedReader br = null;
				String line = null;
				StringBuilder sb = new StringBuilder();
				br = new BufferedReader(new InputStreamReader(message.getBody()));
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
				logger.info("Book from queue:: " + sb.toString());
				obj = parser.parse(sb.toString());
				JSONObject jsonObject = (JSONObject) obj;
				name = (String) jsonObject.get("name");
				author = (String) jsonObject.get("author");
				price = (double) jsonObject.get("price");
				blobPictureUrl = (String) jsonObject.get("blobPictureUrl");
				Book book = new Book();
				book.setName(name);
				book.setAuthor(author);
				book.setPrice(price);
				book.setBlobPictureUrl(blobPictureUrl);
				bookList.add(book);
				// Remove message from queue.
				logger.info("Deleting this message.");
				service.deleteMessage(message);
			} else {
				break;
			}
		}
		
	} catch (ServiceException serviceException) {
		logger.error("This is serviceException : " + serviceException.getStackTrace());
	} catch (Exception exception) {
		logger.error("This is exception : " + exception.getStackTrace());
	}
		return bookList;
	
}
	
	public void deleteAllBooks() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		String stringQuery = "DELETE * FROM BOOK";
		Query query = session.createQuery(stringQuery);
		query.executeUpdate();
		session.getTransaction().commit();
	}
	
	@Override
	public List<Book> listOfAllBooks() {
		
		Session session = HibernateUtil.getSessionFactory().openSession();
		
		@SuppressWarnings("unchecked")
		List<Book> bookList = session.createCriteria(Book.class).list();
		session.close();
		return bookList;
	}

/* ---- not required
	@Override
	public void downloadImageFromBlobStorage() {
		try {
			CloudStorageAccount cloudStorageAccount = CloudStorageAccount.parse(storageConnectionString);
			CloudBlobClient cloudBlobClient = cloudStorageAccount.createCloudBlobClient();
			CloudBlobContainer blobContainer = cloudBlobClient.getContainerReference("files");
			//blob.download(new FileOutputStream("C:\\mydownloads\\" + blob.getName())
		} catch (InvalidKeyException | URISyntaxException exception) {
			logger.error("This is exception : " + exception.getStackTrace());
		} catch (StorageException storageException) {
			logger.error("This is storageException : " + storageException.getStackTrace());
		}
		
	}
------ */
	
	@Override
	public void save(Book book) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		session.save(book);
		session.getTransaction().commit();
		session.close();
	}

	@Override
	public void save(String bookAsJSONString) throws ParseException 
	{
		JSONParser parser = new JSONParser();
		JSONObject jsonObject = (JSONObject)(parser.parse(bookAsJSONString));

		Book book = new Book();
		book.setName((String) jsonObject.get("name"));
		book.setAuthor((String) jsonObject.get("author"));
		book.setPrice((double) jsonObject.get("price"));
		book.setBlobPictureUrl((String) jsonObject.get("blobPictureUrl"));
		
		save(book);
	}

}
