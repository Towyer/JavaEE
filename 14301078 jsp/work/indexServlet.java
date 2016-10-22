import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class
indexServlet
implements Servlet {
	public void service(ServletRequest request, ServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		out.println("HTTP/1.1 200 OK\r\n");
		out.println("<html>   <head>       <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />       <title>hello</title>   </head>   <body>       <form action=\"myForm\" method=\"post\">           <input type=\"text\" name=\"text1\" value=\"China\">           <input type=\"submit\" value=\"submit  myForm\">       </form>   </body>   </html>  		");
}

	
	public void destroy() {

	}

	
	public ServletConfig getServletConfig() {

		return null;
	}

	
	public String getServletInfo() {

		return null;
	}


	public void init(ServletConfig arg0) throws ServletException {

	}
}