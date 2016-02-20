<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<html>
<head>
<style>
.error {
	color: #ff0000;
}
.errorblock{
	color: #000;
	background-color: #ffEEEE;
	border: 3px solid #ff0000;
	padding:8px;
	margin:16px;
}
</style>
</head>

<body>
<h2>Please supply the details of your book....</h2>

<form:form method='POST' 
	action='storeBook.htm' 
	commandName='book' enctype='multipart/form-data'>

<%-- Use relative url in the action, not fully qualified like /SpringFileUpload/Upload/ --%>

<%-- <form:errors path="*" cssClass="errorblock" element="div"/> --%>

 
<table>
	<tr>
        <td><form:label path="name">Book Name</form:label></td>
        <td><form:input path="name" /></td>
    </tr>
    <tr>
        <td><form:label path="author">Author</form:label></td>
        <td><form:input path="author" /></td>
    </tr>
    <tr>
        <td><form:label path="price">Price</form:label></td>
        <td><form:input path="price" /></td>
    </tr>
    <tr>
        <td><form:label path="bookPicture">Please select a preview off your book :</form:label></td>
        <td><input type="file" name="bookPicture"></td>
    </tr>
    <tr>
        <td colspan="2">
            <input type="submit" value="upload"/>
        </td>
    </tr> 
</table>    
<%-- <span><form:errors path="file" cssClass="error" /></span>  --%>

</form:form>

</body>
</html>