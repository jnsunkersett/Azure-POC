package com.psl.dao;

import java.util.List;

import com.psl.model.Book;

public interface BookDao {

	public List<Book> listOfAllBooks();
	public void downloadImageFromBlobStorage();
}
