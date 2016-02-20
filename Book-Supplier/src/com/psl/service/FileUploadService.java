package com.psl.service;

import com.psl.model.Book;

public interface FileUploadService {

	//public String uploadFileInBlob(MultipartFile file, String fileName);
	public Book storeBook(Book book);
}
