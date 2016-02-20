package com.psl.service;

import java.util.List;

import javax.jms.MessageListener;

import com.psl.model.Book;

public interface BookService extends MessageListener {

	public List<Book> listOfAllBooks();
}
