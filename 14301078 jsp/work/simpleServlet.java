import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class
simpleServlet
implements Servlet {
	public void service(ServletRequest request, ServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		out.println("HTTP/1.1 200 OK\r\n");
		out.println("<html>  <head>  <title>First Page</title>  </head>  <body>  <H3>Today is:  ");out.println( new java.util.Date() );
out.println("  </H3>  </body>  </html>		");
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