package com.psl.dao.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
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
import com.microsoft.azure.storage.queue.CloudQueue;
import com.microsoft.azure.storage.queue.CloudQueueClient;
import com.microsoft.azure.storage.queue.CloudQueueMessage;
import com.psl.dao.FileUploadDao;
import com.psl.model.Book;


public class FileUploadDaoImpl implements FileUploadDao{

	final static Logger logger = Logger.getLogger(FileUploadDaoImpl.class);
	
	@Autowired
    private java.util.Properties properties;
	// Define the connection-string with your values
	
	// Operation on blob storage
	
	private String uploadFileInBlob(MultipartFile file, String fileName) {
		CloudStorageAccount storageAccount = null;
		InputStream inputStream = null;
		String profileImageBLOBUrl = null;
		byte[] byteArr = null;
		final String storageConnectionString = properties.getProperty("storageConnectionString");
		try {
			byteArr = file.getBytes();
			inputStream = new ByteArrayInputStream(byteArr);
			// Retrieve storage account from connection-string.
			storageAccount = CloudStorageAccount.parse(storageConnectionString);
			// Create the blob client.
			CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
			// Container name must be lower case.
			CloudBlobContainer container = blobClient
					.getContainerReference("files");
			// Create the container if it does not exist.
			container.createIfNotExists();
			// Create a permissions object.
			//Set Access Control Level(ACL) to private
			BlobContainerPermissions containerPermissions = new BlobContainerPermissions();
			// Include public access in the permissions object.
			//containerPermissions.setPublicAccess(BlobContainerPublicAccessType.CONTAINER);

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
			// In the Sample the Shared Access Permissions are set to READ permission.
			EnumSet<SharedAccessBlobPermissions> perEnumSet = EnumSet.of(SharedAccessBlobPermissions.READ);
			policy.setPermissions(perEnumSet);
			policy.setSharedAccessExpiryTime(sharedAccessExpiryTime);
			policy.setSharedAccessStartTime(sharedAccessStartTime);
			// Define Blob container permissions.
			HashMap<String, SharedAccessBlobPolicy> map = new HashMap<String, SharedAccessBlobPolicy >();
			map.put("policy", policy);
			containerPermissions.setSharedAccessPolicies(map);
			// Set the permissions on the container.
			container.uploadPermissions(containerPermissions);
			CloudBlockBlob blob = container.getBlockBlobReference(fileName);
			// System.out.println("Image fileName:: " + fileName);
			blob.getProperties().setContentType(file.getContentType());

			blob.upload(inputStream, byteArr.length);
			SharedAccessBlobPolicy policy1 = container.downloadPermissions().getSharedAccessPolicies().get("policy");
			String signature = container.generateSharedAccessSignature(policy1, null);
			profileImageBLOBUrl = blob.getUri() + "?" + signature;
			//System.out.println("Uri:: " + profileImageBLOBUrl);
			/*
			 * // Loop through each blob item in the container. for
			 * (ListBlobItem blobItem : container.listBlobs()) { // If the item
			 * is a blob, not a virtual directory. if (blobItem instanceof
			 * CloudBlob) { // Download the item and save it to a file with the
			 * same name. CloudBlob blob = (CloudBlob) blobItem;
			 * blob.download(new FileOutputStream("C:\\mydownloads\\" +
			 * blob.getName())); } }
			 */
			// Delete the blob.
			// blob.deleteIfExists();

		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (StorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return profileImageBLOBUrl;
	}
	@Override
	public Book storeBook(Book book) {
		try {
			final String storageConnectionString = properties.getProperty("storageConnectionString");
			// Retrieve storage account from connection-string.
			CloudStorageAccount storageAccount = CloudStorageAccount
					.parse(storageConnectionString);
			// Create the queue client.
			CloudQueueClient queueClient = storageAccount
					.createCloudQueueClient();
			// Retrieve a reference to a queue.
			CloudQueue queue = queueClient.getQueueReference("bookqueue");
			// Create the queue if it doesn't already exist.
			queue.createIfNotExists();
			book.setBlobPictureUrl(uploadFileInBlob(book.getBookPicture(), book.getBookPicture().getOriginalFilename()));
			//String bookString = objectMapper.writeValueAsString(book);
			String bookJsonInString = "{\"name\":" + book.getName() + ",\"author\":" + book.getAuthor() + ",\"price\":" + book.getPrice() + ",\"blobPictureUrl\":" + book.getBlobPictureUrl() +"}";
			logger.info("Book as JSON:: " + bookJsonInString);
			//  Add message to the queue.
		    CloudQueueMessage msg = new CloudQueueMessage(bookJsonInString);
		    queue.addMessage(msg);
		    // Peek at the next message.
		    /*CloudQueueMessage peekedMessage = queue.peekMessage();
		    // Output the message value.
		    if (peekedMessage != null)
		    {
		      System.out.println(peekedMessage.getMessageContentAsString());
		   }*/
		  // Delete the queue if it exists.
		    //queue.deleteIfExists();
		} catch (Exception e) {
			logger.error("This is error : " + e.getStackTrace());
		}
		return book; //.getBlobPictureUrl();
	}

}
