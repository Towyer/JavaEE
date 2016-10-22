package jsp;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;
import java.io.File;
import java.io.IOException;
 
import javax.servlet.Servlet;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import jsp.Request;
import jsp.Response;
 
public class ServletProcessor {
 
 public void process(Request request, Response response,String servletName) {
 
 //类加载器，用于从指定JAR文件或目录加载类
 URLClassLoader loader = null;
 try {
 URLStreamHandler streamHandler = null;
 //创建类加载器
 System.out.println(System.getProperty("user.dir")+ File.separator+ "work" +  File.separator);
 loader = new URLClassLoader(new URL[]{new URL(null, "file:" + 
 System.getProperty("user.dir")+ File.separator+ "work" +  File.separator, 
 streamHandler)});
 } catch (IOException e) {
 System.out.println(e.toString());
 }
  
 Class<?> myClass = null;
 try {
 //加载对应的servlet类
 myClass = loader.loadClass(servletName);
 } catch (ClassNotFoundException e) {
 System.out.println(e.toString());
 }
 
 Servlet servlet = null;
 
 try {
 //生产servlet实例
 servlet = (Servlet) myClass.newInstance();
 //执行ervlet的service方法
 servlet.service((ServletRequest) request,(ServletResponse) response);
 } catch (Exception e) {
 System.out.println(e.toString());
 } catch (Throwable e) {
 System.out.println(e.toString());
 }
 
 }
 
}
