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

		<c:choose>
    		<c:when test="${empty bookList}">
        		Sorry!! No book is available as yet... 
        		<br />
    		</c:when>    
    		<c:otherwise>
       			<table>
					<tr>
						<th>Name</th>
						<th>Author</th>
						<th>Price</th>
						<th>Preview URL</th>
					</tr>
					<c:forEach items="${bookList}" var="book">
					<tr> 
						<td>
						<c:out value="${book.name}"/>
						</td>
						<td>
						<c:out value="${book.author}"/>
						</td>
						<td>
						<c:out value="${book.price}"/>
						</td>
						<td>
							<a href=
							<c:out value="${book.blobPictureUrl}"/>>Preview here</a>
						</td>
					</tr>
					</c:forEach>
		
				</table>
    		</c:otherwise>
		</c:choose>
		

</body>
</html>