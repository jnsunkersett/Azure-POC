package com.psl.dao;

import org.springframework.web.multipart.MultipartFile;

import com.psl.model.Book;

public interface FileUploadDao {
	//public String uploadFileInBlob(MultipartFile file, String fileName);
	public Book storeBook(Book book);
}
