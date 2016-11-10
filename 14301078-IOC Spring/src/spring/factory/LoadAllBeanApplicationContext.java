package spring.factory;

import java.io.File;

import java.lang.reflect.Constructor;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import spring.bean.BeanDefinition;
import spring.bean.BeanUtil;
import spring.bean.PropertyValue;
import spring.bean.PropertyValues;
import spring.resource.LocalFileResource;
import spring.resource.Resource;
import spring.annotation.*;

public class LoadAllBeanApplicationContext extends abstractApplicationContext {

	NodeList beanList = null;

	public LoadAllBeanApplicationContext(String[] filename) {
		
		//先读component到map
		loadComponent();
		
		//再读xml
		Resource resource = new LocalFileResource(filename[0]);

		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dbBuilder = dbFactory.newDocumentBuilder();
			Document document = dbBuilder.parse(resource.getInputStream());
			beanList = document.getElementsByTagName("bean");

			for (int i = 0; i < beanList.getLength(); i++) {
				Node bean = beanList.item(i);		
				loadBean(bean.getAttributes().getNamedItem("id").getNodeValue());
			}

		} catch (Exception e) {
			System.out.println("哇 打开xml的时候出错惹QAQ");
		}
	}
   
	//读xml
	private void loadBean(String theBeanName) {
		
		// 要是还没读入Map则进行load
		if (getBean(theBeanName) == null) {
			for (int i = 0; i < beanList.getLength(); i++) {
				Node bean = beanList.item(i);

				String beanClassName = bean.getAttributes().getNamedItem("class").getNodeValue();
				String beanName = bean.getAttributes().getNamedItem("id").getNodeValue();

				if (theBeanName.equals(beanName)) {
					BeanDefinition beandef = new BeanDefinition();
					beandef.setBeanClassName(beanClassName);

					try {
						Class<?> beanClass = Class.forName(beanClassName);
						beandef.setBeanClass(beanClass);
					} catch (ClassNotFoundException e) {
						System.out.println("哇 读xml打开class的时候出错惹QAQ");
					}

					PropertyValues propertyValues = new PropertyValues();

					NodeList propertyList = bean.getChildNodes();

					for (int j = 0; j < propertyList.getLength(); j++) {
						Node property = propertyList.item(j);
						if (property instanceof Element) {
							Element ele = (Element) property;

							String name = ele.getAttribute("name");

							if (!ele.getAttribute("value").isEmpty()) {
								try {
									Class<?> type;
									type = beandef.getBeanClass().getDeclaredField(name).getType();
									Object value = ele.getAttribute("value");

									if (type == Integer.class) {
										value = Integer.parseInt((String) value);
									}
									propertyValues.AddPropertyValue(new PropertyValue(name, value));
								} catch (Exception e) {
									System.out.println("哇 打开XML匹配property出错惹QAQ");
								}
							} 
							if (!ele.getAttribute("ref").isEmpty()) {
								String refBean = ele.getAttribute("ref");

								// 判断该bean是否已被引用
								if (getBean(refBean) == null) {
									// 如果不在Map中，去剩下的Xml里找到这玩意进行读取
									loadBean(refBean);
								}
							}
						}
					}
					beandef.setPropertyValues(propertyValues);

					this.registerBeanDefinition(beanName, beandef);
				}
			}
		}
	}
   
	//实现@component
	private void loadComponent() {
		String packageName = "";
		File root = new File(System.getProperty("user.dir") + "\\src");
		try {
			loadJava(root, packageName);
		} catch (Exception e) {
			System.out.println("哇 遍历java文件时出错惹QAQ");
		}
	}

	private void loadJava(File folder, String packageName) throws Exception {
		File[] files = folder.listFiles();
		for (int fileIndex = 0; fileIndex < files.length; fileIndex++) {
			File file = files[fileIndex];
			if (file.isDirectory()) {
				loadJava(file, packageName + file.getName() + ".");
			} else {
				//对每个java代码进行匹配component
				String filename = file.getName();
				
				try {
					String name = filename.substring(0, filename.length() - 5);
					Class<?> obj = Class.forName(packageName + name);
					
					// 找到component
					
					if ((Component) obj.getAnnotation(Component.class) != null) {

						Component com = (Component) obj.getAnnotation(Component.class);
						
						BeanDefinition beandef = new BeanDefinition();
						beandef.setBeanClassName(packageName + name);
						beandef.setBeanClass(obj);

						this.registerBeanDefinition(com.value(), beandef);
					}
				} catch (Exception e) {
					System.out.println("哇 匹配component出错惹QAQ");
				}
			}
		}
	}

	//实现@autowire
	public Object findAutowire(BeanDefinition beanDefinition) {

		Class<?> obj = beanDefinition.getBeanClass();
		Constructor<?>[] cons = obj.getConstructors();

		// 对于每个 constructor 进行注解查找
		for (Constructor<?> c : cons) {

			

			if ((Autowired) c.getAnnotation(Autowired.class) != null) {

				Object[] params = new Object[c.getParameterTypes().length];

				for (int i = 0; i < c.getParameterTypes().length; i++) {
					// 先得到参数
					String objectName = c.getParameterTypes()[i].getName()
							.split("\\.")[(c.getParameterTypes()[i].getName().split("\\.").length) - 1];
					// 取到对应object
					if (getBean(objectName) != null) {// 如果目前map里没有，去prove里找
						params[i] = getBean(objectName);
					}
				}
				// 调用构造函数
				try {
					return c.newInstance(params);
				} catch (Exception e) {
					System.out.println("哇 自动注入构造函数出错惹QAQ");
				}

			}
		}
		return null;
	}

	@Override
	protected BeanDefinition GetCreatedBean(BeanDefinition beanDefinition) {
		try {

			Class<?> beanClass = beanDefinition.getBeanClass();

			Object bean = this.findAutowire(beanDefinition);

			if (bean == null) {
				bean = beanClass.newInstance();
			}
			
			if (beanDefinition.getPropertyValues() != null) {
				List<PropertyValue> fieldDefinitionList = beanDefinition.getPropertyValues().GetPropertyValues();

				for (PropertyValue propertyValue : fieldDefinitionList) {
					BeanUtil.invokeSetterMethod(bean, propertyValue.getName(), propertyValue.getValue());
				}
			}

			beanDefinition.setBean(bean);

			return beanDefinition;

		} catch (Exception e) {
			System.out.println("哇 创建object出错惹QAQ");
		} 
		return null;
	}

	
}
