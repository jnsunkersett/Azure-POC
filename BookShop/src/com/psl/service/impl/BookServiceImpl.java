package com.psl.service.impl;

import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;
import org.apache.qpid.amqp_1_0.jms.impl.BytesMessageImpl;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;

import com.psl.dao.BookDao;
import com.psl.model.Book;
import com.psl.service.BookService;

public class BookServiceImpl implements BookService {

	final static Logger logger = Logger.getLogger(BookServiceImpl.class);

	@Autowired
	BookDao bookDao;
	
	@Override
	public List<Book> listOfAllBooks() {
		return bookDao.listOfAllBooks();
	}

	@Override
	public void onMessage(Message message) 
	{
		try {
			byte[] msgBytes = new byte[4096];
			((BytesMessageImpl)message).readBytes(msgBytes);
			String jsonString = new String(msgBytes).trim();
			logger.debug("Received a book message from Service Bus : " + jsonString);

			bookDao.save(jsonString);

			message.acknowledge();
		} catch (JMSException e) {
			logger.error("JMS Exception in onMessage.", e);
		} catch (ParseException e) {
			logger.error("Exception when parsing Book as JSON String.");
			e.printStackTrace();
		}
	}
	
}
