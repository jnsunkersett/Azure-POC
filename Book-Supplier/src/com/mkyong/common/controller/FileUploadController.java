package com.mkyong.common.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.psl.model.Book;
import com.psl.service.FileUploadService;


@Controller
public class FileUploadController {
	
	@Autowired
	public FileUploadService fileUploadService;
	
	
	/*public FileUploadController(){
		setCommandClass(FileUpload.class);
		setCommandName("fileUploadForm");
	}*/
 
	@RequestMapping("/fileupload")
	protected ModelAndView fileUploadForm(){
		ModelAndView  modelAndView = new ModelAndView("FileUploadForm");
		modelAndView.addObject("book", new Book());
		return modelAndView;
	}
	
	@RequestMapping("/storeBook")
	protected ModelAndView storeBook(@ModelAttribute Book book) throws Exception 
	{
		ModelAndView modelAndView = new ModelAndView("FileUploadSuccess");
		MultipartFile multipartFile = book.getBookPicture();
		String fileName="";

		if(multipartFile!=null){
			fileName = multipartFile.getOriginalFilename();
			Book result = fileUploadService.storeBook(book);
			modelAndView.addObject("blobURL", result.getBlobPictureUrl());
			modelAndView.addObject("author", result.getAuthor());
			modelAndView.addObject("price", result.getPrice());
			modelAndView.addObject("bookName", result.getName());
		}
		
		return modelAndView;
 
	}

	/*@Override
	protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder)
		throws ServletException {
		
		// Convert multipart object to byte[]
		binder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor());
		
	}*/
	
}