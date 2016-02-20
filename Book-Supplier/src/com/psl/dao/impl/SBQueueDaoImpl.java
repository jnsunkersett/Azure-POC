package com.psl.dao.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.BlobContainerPermissions;
import com.microsoft.azure.storage.blob.BlobContainerPublicAccessType;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.microsoft.azure.storage.blob.SharedAccessBlobPermissions;
import com.microsoft.azure.storage.blob.SharedAccessBlobPolicy;
import com.microsoft.windowsazure.Configuration;
import com.microsoft.windowsazure.exception.ServiceException;
import com.microsoft.windowsazure.services.servicebus.ServiceBusConfiguration;
import com.microsoft.windowsazure.services.servicebus.ServiceBusContract;
import com.microsoft.windowsazure.services.servicebus.ServiceBusService;
import com.microsoft.windowsazure.services.servicebus.models.BrokeredMessage;
import com.microsoft.windowsazure.services.servicebus.models.CreateQueueResult;
import com.microsoft.windowsazure.services.servicebus.models.QueueInfo;
import com.psl.dao.SBQueueDao;
import com.psl.model.Book;

public class SBQueueDaoImpl implements SBQueueDao {
	
	final static Logger logger = Logger.getLogger(SBQueueDaoImpl.class);

	@Autowired
	private java.util.Properties properties;
	// Define the connection-string with your values
	

	@SuppressWarnings("unchecked")
	@Override
	public Book storeBook(Book book) {
		
		logger.info("In storeBook() of SBQueueDaoImpl");
//		String namespace = properties.getProperty("namesapce");
//		String sasKeyName = properties.getProperty("sasKeyName");
//		String sasKeyValue = properties.getProperty("sasKey");
//		String serviceBusRootUri = properties.getProperty("serviceBusRootUri");
//		String queueName = properties.getProperty("queueName");
		book.setBlobPictureUrl(uploadFileIntoBlobStorage(book.getBookPicture(), book.getBookPicture().getOriginalFilename()));
		JSONObject obj = new JSONObject();
		obj.put("name",book.getName());
	    obj.put("author",book.getAuthor());
	    obj.put("price",book.getPrice());
	    obj.put("blobPictureUrl", book.getBlobPictureUrl());

	    StringWriter out = new StringWriter();
	    try {
			obj.writeJSONString(out);
		} catch (IOException ioException) {
			logger.error("This is ioException : " + ioException.getStackTrace());
		}
	      
	    String bookJsonInString = out.toString();
	    logger.info("Book As a JSON String:: " + bookJsonInString);

	    sendMessageOntoServiceBus(bookJsonInString);
//		Configuration config = ServiceBusConfiguration
//				.configureWithSASAuthentication(namespace, sasKeyName,
//						sasKeyValue, serviceBusRootUri);
//		ServiceBusContract service = ServiceBusService.create(config);
//		BrokeredMessage msg1 = new BrokeredMessage(bookJsonInString);
//		try {
//			service.sendQueueMessage(queueName, msg1);
//		} catch (ServiceException serviceException) {
//			logger.error("This is serviceException : " + serviceException.getStackTrace());
//		}
		return book;
	}

	private String uploadFileIntoBlobStorage(MultipartFile file, String fileName) {
		
		logger.info("In uploadFileInBlob() of SBQueueDaoImpl");
		String storageConnectionString = properties.getProperty("storageConnectionString");
		CloudStorageAccount storageAccount = null;
		InputStream inputStream = null;
		String profileImageBLOBUrl = null;
		byte[] byteArr = null;

		try {
			byteArr = file.getBytes();
			inputStream = new ByteArrayInputStream(byteArr);
			// Retrieve storage account from connection-string.
			storageAccount = CloudStorageAccount.parse(storageConnectionString);
			// Create the blob client.
			CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
			// Container name must be lower case.
			CloudBlobContainer container = blobClient.getContainerReference("files");
			// Create the container if it does not exist.
			container.createIfNotExists();
			// Create a permissions object.
			// Set Access Control Level(ACL) to private
			BlobContainerPermissions containerPermissions = new BlobContainerPermissions();
			// Include public access in the permissions object.
			// containerPermissions.setPublicAccess(BlobContainerPublicAccessType.CONTAINER);

			// Setting Private access to Container
			containerPermissions.setPublicAccess(BlobContainerPublicAccessType.OFF);
			Calendar cal = Calendar.getInstance();
			cal.setTimeZone(TimeZone.getTimeZone("UTC"));
			// Define the start and end time to grant permissions.
			Date sharedAccessStartTime = cal.getTime();
			cal.add(Calendar.HOUR, 2);
			Date sharedAccessExpiryTime = cal.getTime();
			// Define shared access policy
			SharedAccessBlobPolicy policy = new SharedAccessBlobPolicy();
			// In the Sample the Shared Access Permissions are set to READ
			// permission.
			EnumSet<SharedAccessBlobPermissions> perEnumSet = EnumSet.of(SharedAccessBlobPermissions.READ);
			policy.setPermissions(perEnumSet);
			policy.setSharedAccessExpiryTime(sharedAccessExpiryTime);
			policy.setSharedAccessStartTime(sharedAccessStartTime);
			// Define Blob container permissions.
			HashMap<String, SharedAccessBlobPolicy> map = new HashMap<String, SharedAccessBlobPolicy>();
			map.put("policy", policy);
			containerPermissions.setSharedAccessPolicies(map);
			// Set the permissions on the container.
			container.uploadPermissions(containerPermissions);
			CloudBlockBlob blob = container.getBlockBlobReference(fileName);
			blob.getProperties().setContentType(file.getContentType());

			blob.upload(inputStream, byteArr.length);
			SharedAccessBlobPolicy policy1 = container.downloadPermissions().getSharedAccessPolicies().get("policy");
			String signature = container.generateSharedAccessSignature(policy1, null);
			profileImageBLOBUrl = blob.getUri() + "?" + signature;

		} catch (InvalidKeyException invalidKeyException) {
			logger.error("This is invalidKeyException : " + invalidKeyException.getStackTrace());
		} catch (URISyntaxException uriSyntaxException) {
			logger.error("This is uriSyntaxException : " + uriSyntaxException.getStackTrace());
		} catch (StorageException storageException) {
			logger.error("This is storageException : " + storageException.getStackTrace());
		} catch (IOException ioException) {
			logger.error("This is ioException : " + ioException.getStackTrace());
		}
		return profileImageBLOBUrl;
	}
	
	private void sendMessageOntoServiceBus(String string2Send) {
		
		String namespace = properties.getProperty("namesapce");
		String sasKeyName = properties.getProperty("sasKeyName");
		String sasKeyValue = properties.getProperty("sasKey");
		String serviceBusRootUri = properties.getProperty("serviceBusRootUri");
		String queueName = properties.getProperty("queueName");
		Configuration config = ServiceBusConfiguration.configureWithSASAuthentication(namespace, sasKeyName,
						sasKeyValue, serviceBusRootUri);
		ServiceBusContract service = ServiceBusService.create(config);
		BrokeredMessage brokeredMsg = new BrokeredMessage(string2Send);
		try {
			service.sendQueueMessage(queueName, brokeredMsg);
			logger.info("Message sent onto Service Bus [" + string2Send + "]");
		} catch (ServiceException serviceException) {
			logger.error("ServiceException when attempting to send: ", serviceException);
		}
		
	}

	/*private String createServiceBusQueue(String name) {
		String namespace = properties.getProperty("namesapce");
		String sasKeyName = properties.getProperty("sasKeyName");
		String sasKeyValue = properties.getProperty("sasKey");
		String serviceBusRootUri = properties.getProperty("serviceBusRootUri");
		String msg = null;
		Configuration config = ServiceBusConfiguration
				.configureWithSASAuthentication(namespace, sasKeyName,
						sasKeyValue, serviceBusRootUri);
		ServiceBusContract service = ServiceBusService.create(config);
		QueueInfo queueInfo = new QueueInfo(name);
		// queueInfo.isRequiresDuplicateDetection();
		try {
			CreateQueueResult result = service.createQueue(queueInfo);
			msg = result.getValue().getPath();
		} catch (ServiceException e) {
			System.out.print("ServiceException encountered: ");
			System.out.println(e.getMessage());
			System.exit(-1);
		}
		return msg;
	}*/
}
