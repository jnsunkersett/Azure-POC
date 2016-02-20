package com.psl.model;

import org.springframework.web.multipart.MultipartFile;

public class Book {

	private String name;
	private String author;
	private double price;
	private MultipartFile bookPicture;
	private String blobPictureUrl;
	
	public String getBlobPictureUrl() {
		return blobPictureUrl;
	}
	public void setBlobPictureUrl(String blobPictureUrl) {
		this.blobPictureUrl = blobPictureUrl;
	}
	public MultipartFile getBookPicture() {
		return bookPicture;
	}
	public void setBookPicture(MultipartFile bookpicture) {
		this.bookPicture = bookpicture;
	}
	public Book() {
		// TODO Auto-generated constructor stub
	}
	public Book(String name, String author, double price) {
		super();
		this.name = name;
		this.author = author;
		this.price = price;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	
}
