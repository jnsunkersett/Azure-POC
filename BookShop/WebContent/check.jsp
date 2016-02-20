
<%@page import="java.io.File"%>

<% 

	File f = new File("AlphaBeta.txt");
		 f.createNewFile();

		 System.out.println("Path=" + f.getAbsolutePath());
	 out.println("Path=" + f.getAbsolutePath());
%>