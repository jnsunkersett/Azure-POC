package com.psl.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.psl.dao.FileUploadDao;
import com.psl.dao.SBQueueDao;
import com.psl.model.Book;
import com.psl.service.FileUploadService;

@Service
public class FileUplaodServiceImpl implements FileUploadService{

	@Autowired
	public FileUploadDao fileUploadDao;
	
	@Autowired
	public SBQueueDao sbQueueDao;
	
	/*@Override
	public String uploadFileInBlob(MultipartFile file, String fileName) {
		return fileUploadDao.uploadFileInBlob(file, fileName);
	}*/

	@Override
	public Book storeBook(Book book) {
		//return fileUploadDao.storeBook(book);
		return sbQueueDao.storeBook(book);
	}

}
