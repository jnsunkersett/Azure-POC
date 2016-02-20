package com.psl.dao;

import java.util.List;

import org.json.simple.parser.ParseException;

import com.psl.model.Book;

public interface BookDao {

	public List<Book> listOfAllBooks();
//	public void downloadImageFromBlobStorage();
	public void save(Book book);
	public void save(String bookAsJSONString) throws ParseException;
}
