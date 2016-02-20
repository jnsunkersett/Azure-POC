package com.psl.model;

public class Book {

	private int bookId;
	private String name;
	private String author;
	private double price;
	private String blobPictureUrl;
	
	
	public Book(String name, String author, double price) {
		super();
		this.name = name;
		this.author = author;
		this.price = price;
	}
	public Book() {
		// TODO Auto-generated constructor stub
	}
	public int getBookId() {
		return bookId;
	}
	public void setBookId(int bookId) {
		this.bookId = bookId;
	}
	public String getBlobPictureUrl() {
		return blobPictureUrl;
	}
	public void setBlobPictureUrl(String blobPictureUrl) {
		this.blobPictureUrl = blobPictureUrl;
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
