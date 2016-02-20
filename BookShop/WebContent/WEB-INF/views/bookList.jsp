<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>BookList</title>
</head>
<body>
	<h3>Welcome to the Book Shop</h3>
	<br>
		<c:choose>
    		<c:when test="${empty bookList}">
        		Sorry!! No book is available as yet... 
        		<br />
    		</c:when>    
    		<c:otherwise>
       			<table>
					<tr>
						<th width="100" align="left">Name</th>
						<th width="100" align="left">Author</th>
						<th width="100" align="left">Price</th>
						<th width="200" align="left">Preview URL</th>
					</tr>
					<c:forEach items="${bookList}" var="book">
					<tr> 
						<td width="100">
						<c:out value="${book.name}"/>
						</td>
						<td width="100">
						<c:out value="${book.author}"/>
						</td>
						<td width="100">
						<c:out value="${book.price}"/>
						</td>
						<td width="200">
							<a href=
							<c:out value="${book.blobPictureUrl}"/>>Preview here</a>
						</td>
					</tr>
					</c:forEach>
					<tr>
						<td colspan="4">
							<p>Start with a clean slate. <a href="listAllBooks.htm">Delete All records</a></p>
						</td>
					</tr>
				</table>
				
    		</c:otherwise>
		</c:choose>
		

</body>
</html>