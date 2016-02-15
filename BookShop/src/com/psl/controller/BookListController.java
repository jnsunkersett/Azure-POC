package com.psl.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.psl.model.Book;
import com.psl.service.BookService;

@Controller
public class BookListController {

	@Autowired
	BookService bookService;
	@RequestMapping(value = "/books", method = RequestMethod.GET)
	public ModelAndView welcomeToBookShop(){
		ModelAndView modelAndView = new ModelAndView("welcome");
		return modelAndView;
	}
	
	@RequestMapping(value = "/listAllBooks", method = RequestMethod.GET)
	public ModelAndView listAllBooks(){
		List<Book> bookList = bookService.listOfAllBooks();
		ModelAndView modelAndView = new ModelAndView("bookList");
		modelAndView.addObject("bookList", bookList);
		return modelAndView;
	}
}
