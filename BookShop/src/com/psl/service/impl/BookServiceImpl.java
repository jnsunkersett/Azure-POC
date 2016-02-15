package com.psl.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.psl.dao.BookDao;
import com.psl.model.Book;
import com.psl.service.BookService;

public class BookServiceImpl implements BookService{

	@Autowired
	BookDao bookDao;
	@Override
	public List<Book> listOfAllBooks() {
		return bookDao.listOfAllBooks();
	}

}
