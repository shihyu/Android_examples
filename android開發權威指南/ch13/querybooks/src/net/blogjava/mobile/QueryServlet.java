package net.blogjava.mobile;

import java.io.IOException;
import java.io.PrintWriter;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class QueryServlet
 */
public class QueryServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;
 
	@Override
	protected void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException
	{
		response.setContentType("text/html;charset=utf-8"); 
		String queryStr = "";
		if ("post".equals(request.getMethod().toLowerCase()))
			queryStr = "POST請求；查詢字串：" + new String(request.getParameter("bookname").getBytes(
					"iso-8859-1"), "utf-8");
		else if ("get".equals(request.getMethod().toLowerCase()))
			queryStr = "GET請求；查詢字串：" + request.getParameter("bookname");

		String s =queryStr
				+ "[Java Web開發速學寶典;Java開發指南思想（第4版）;Java EE開發寶典；C#開發寶典]";
		PrintWriter out = response.getWriter();
		out.println(s);
	}
}
