<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<body>
<h2>Your book has been accepted. </h2>
<h3>The same has been communicated to the Book - Shop, <u>in real time</u> and can be checked here.</h3>

<h4>Details that were submitted were:</h4>
<br>
<table>
	<tr>
        <td>Book Name</td>
        <td><strong> ${bookName} </strong></td>
    </tr>
    <tr>
        <td>Author</td>
        <td><strong> ${author} </strong></td>
    </tr>
    <tr>
        <td>Price</td>
        <td><strong> ${price} </strong></td>
    </tr>
	<tr>
		<td colspan='2'><p>The preview has been stored on Azure Blob Storage and can be <a target="_blank" href="${blobURL}">previewed here</a></p></td>
	</tr>
	<tr>
		<td colspan='2'><p>Upload the next book? Please <a href="fileupload.htm">click here.</a></p></td>
	</tr>
</table>
<!-- 
<h3>FileName : "<strong> ${fileName} </strong>" - Uploaded in Blob Storage Successfully. &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
U can access : "<strong> ${result} </strong>"
 -->
</body>
</html>