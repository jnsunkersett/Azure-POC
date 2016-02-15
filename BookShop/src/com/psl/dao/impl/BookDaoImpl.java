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

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
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
import com.psl.model.Book;

public class BookDaoImpl implements BookDao {

	final static Logger logger = Logger.getLogger(BookDaoImpl.class);
	
	@Autowired
	private java.util.Properties properties;
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost/BookShop";
	// Define the connection-string with your values
	final String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=wmigrationstorage;AccountKey=BSVnexsngHJWuI8/2FMIM9JUldADwBUgBuFDjNZaocFsMTKYAy+OOWQX7yyDBvFeWDaMtBgOtGRAPHgUgQM3VQ==";

	// Database credentials
	static final String USER = "root";
	static final String PASS = "password";

	private Connection getJDBCConnection() {
		Connection conn = null;
		// Register JDBC driver
		try {
			Class.forName("com.mysql.jdbc.Driver");
			// Open a connection
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;

	}

	private List<Book> getAvailableBooks(){
		List<Book> bookList = new ArrayList<>();
		Book book1 = new Book("Revolution 2020", "Chetan Bhagat", 200);
		Book book2 = new Book("Old Man and the Sea", "Ernest Hemmingway", 400);
		Book book3 = new Book("The Giver", "Lois Lowry", 300);
		Book book4 = new Book("Agnipankh", "Dr.APJ Abdul Kalam", 700);
		Book book5 = new Book("Musafir", "Achyut Godbole", 600);
		bookList.add(book1);
		bookList.add(book2);
		bookList.add(book3);
		bookList.add(book4);
		bookList.add(book5);
		return bookList;
	}
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
	
	@Override
	public List<Book> listOfAllBooks() {
		/*Connection connection = getJDBCConnection();
		 Statement stmt = null;
		 String sql = null;
		 List<Book> bookList = new ArrayList<>();
		//Execute a query
	      try {
			stmt = connection.createStatement();
			sql = "SELECT * FROM Books";
		    ResultSet rs = stmt.executeQuery(sql);
		    while(rs.next()){
		    	String name = rs.getString("name");
		    	String author = rs.getString("author");
		    	double price = rs.getDouble("price");
		    	int id = rs.getInt("id");
		    	Book book = new Book(name, author, price);
		    	bookList.add(book);
		    }
		} catch (SQLException e) {
			e.printStackTrace();
		}*/
		List<Book> bookList = getBooksFromSBQueue(); 
		return bookList;
	}

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

}
