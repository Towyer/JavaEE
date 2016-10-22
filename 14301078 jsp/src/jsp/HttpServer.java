package jsp;

import java.net.Socket;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import jsp.Request;
import jsp.Response;
import jsp.jspReader;

import java.net.ServerSocket;
import java.net.InetAddress;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.File;
import java.io.IOException;

public class HttpServer {

	// 关闭服务命令
	private static final String SHUTDOWN_COMMAND = "/SHUTDOWN";

	public static void main(String[] args) {
		HttpServer server = new HttpServer();
		// 等待连接请求
		server.await();
	}

	public void await() {
		ServerSocket serverSocket = null;
		int port = 8080;
		try {
			// 服务器套接字对象
			serverSocket = new ServerSocket(port, 1, InetAddress.getByName("127.0.0.1"));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		// 循环等待请求
		while (true) {
			Socket socket = null;
			InputStream input = null;
			OutputStream output = null;
			try {
				// 等待连接，连接成功后，返回一个Socket对象
				socket = serverSocket.accept();
				input = socket.getInputStream();
				output = socket.getOutputStream();

				// 创建Request对象并解析
				Request request = new Request(input);
				request.parse();

				// 创建 Response 对象
				Response response = new Response(output);
				response.setRequest(request);

				// 取到url
				String url = request.getUri();

				// 解析xml
				/*
				 * Boolean canRead = findurl(url);
				 * 
				 * if(canRead) { 
				 * //解析xml得到类名 
				 * String servletName = getServeletName(url); 
				 * ServletProcessor processor = new ServletProcessor(); 
				 * processor.process(request,response,servletName); 
				 * } 
				 * else { 
				 * StaticResourceProcessor
				 * processor = new StaticResourceProcessor();
				 * processor.process(request, response); }
				 */
				String filename = System.getProperty("user.dir")+ File.separator +"jsp" + File.separator + url;
				if(new File(filename).exists())
				{
					// 转换jsp
					jspReader jsp = new jspReader(url.split("/")[1]);
					ServletProcessor processor = new ServletProcessor(); 
					processor.process(request,response,jsp.getServletName()); 
				}
				else
				{
					System.out.println("找不到该文件！");
				}
				

				// 关闭 socket
				socket.close();

			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	public Boolean findurl(String url) {
		Element element = null;
		// 可以使用绝对路径
		File f = new File("web.xml");

		DocumentBuilder db = null;
		DocumentBuilderFactory dbf = null;
		try {
			dbf = DocumentBuilderFactory.newInstance();
			db = dbf.newDocumentBuilder();
			Document dt = db.parse(f);

			// 得到一个elment根元素
			element = dt.getDocumentElement();

			// 获得根元素下的子节点
			NodeList childNodes = element.getChildNodes();

			NodeList theNodeList = null;

			// 遍历这些子节点
			for (int i = 0; i < childNodes.getLength(); i++) {
				// 获得每个对应位置i的结点
				Node node1 = childNodes.item(i);

				// 匹配每一个servelet-mapping里的uri
				if ("servlet-mapping".equals(node1.getNodeName())) {

					NodeList nodeDetail = node1.getChildNodes();

					for (int j = 0; j < nodeDetail.getLength(); j++) {
						Node detail = nodeDetail.item(j);

						if ("url-pattern".equals(detail.getNodeName())) {
							// 匹配url
							if (url.equals(detail.getTextContent()))// 如果匹配到，返回true
								return true;
						}

					}
				}
			}
		}

		catch (Exception e) {
			System.out.println(e.toString());
		}

		return false;
	}

	public String getServeletName(String url) {
		String ServeletName = null;

		Element element = null;
		// 可以使用绝对路径
		File f = new File("web.xml");

		DocumentBuilder db = null;
		DocumentBuilderFactory dbf = null;
		try {
			dbf = DocumentBuilderFactory.newInstance();
			db = dbf.newDocumentBuilder();
			Document dt = db.parse(f);

			// 得到一个elment根元素
			element = dt.getDocumentElement();

			// 获得根元素下的子节点
			NodeList childNodes = element.getChildNodes();

			NodeList theNodeList = null;

			// 遍历这些子节点
			for (int i = 0; i < childNodes.getLength(); i++) {
				// 获得每个对应位置i的结点
				Node node1 = childNodes.item(i);

				// 匹配每一个servelet-mapping里的uri
				if ("servlet-mapping".equals(node1.getNodeName())) {

					NodeList nodeDetail = node1.getChildNodes();

					for (int j = 0; j < nodeDetail.getLength(); j++) {
						Node detail = nodeDetail.item(j);

						if ("url-pattern".equals(detail.getNodeName())) {
							// 匹配url
							if (url.equals(detail.getTextContent()))// 如果匹配到，将该节点取出
								theNodeList = nodeDetail;
						}

					}
				}
			}

			String Sname = null;
			// 取出名字
			for (int j = 0; j < theNodeList.getLength(); j++) {
				Node detail = theNodeList.item(j);

				if ("servlet-name".equals(detail.getNodeName())) // 匹配name
				{
					Sname = detail.getTextContent();
				}

			}

			NodeList theNodeList1 = null;

			// 再次遍历，匹配对应servelet-class
			for (int i = 0; i < childNodes.getLength(); i++) {
				// 获得每个对应位置i的结点
				Node node1 = childNodes.item(i);

				// 匹配每一个servelet里的uri
				if ("servlet".equals(node1.getNodeName())) {

					NodeList nodeDetail = node1.getChildNodes();

					for (int j = 0; j < nodeDetail.getLength(); j++) {
						Node detail = nodeDetail.item(j);

						if ("servlet-name".equals(detail.getNodeName())) // 匹配url对应名字
							if (Sname.equals(detail.getTextContent()))// 如果匹配到，将该节点取出
								theNodeList1 = nodeDetail;
					}
				}
			}
			// 取出serveletname
			for (int j = 0; j < theNodeList1.getLength(); j++) {
				Node detail = theNodeList1.item(j);

				if ("servlet-class".equals(detail.getNodeName())) // 匹配
				{
					ServeletName = detail.getTextContent();
				}

			}
		}

		catch (Exception e) {
			e.printStackTrace();
		}

		return ServeletName;
	}
}
