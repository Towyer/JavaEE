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
		
		//�ȶ�component��map
		loadComponent();
		
		//�ٶ�xml
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
			System.out.println("�� ��xml��ʱ�������QAQ");
		}
	}
   
	//��xml
	private void loadBean(String theBeanName) {
		
		// Ҫ�ǻ�û����Map�����load
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
						System.out.println("�� ��xml��class��ʱ�������QAQ");
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
									System.out.println("�� ��XMLƥ��property������QAQ");
								}
							} 
							if (!ele.getAttribute("ref").isEmpty()) {
								String refBean = ele.getAttribute("ref");

								// �жϸ�bean�Ƿ��ѱ�����
								if (getBean(refBean) == null) {
									// �������Map�У�ȥʣ�µ�Xml���ҵ���������ж�ȡ
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
   
	//ʵ��@component
	private void loadComponent() {
		String packageName = "";
		File root = new File(System.getProperty("user.dir") + "\\src");
		try {
			loadJava(root, packageName);
		} catch (Exception e) {
			System.out.println("�� ����java�ļ�ʱ������QAQ");
		}
	}

	private void loadJava(File folder, String packageName) throws Exception {
		File[] files = folder.listFiles();
		for (int fileIndex = 0; fileIndex < files.length; fileIndex++) {
			File file = files[fileIndex];
			if (file.isDirectory()) {
				loadJava(file, packageName + file.getName() + ".");
			} else {
				//��ÿ��java�������ƥ��component
				String filename = file.getName();
				
				try {
					String name = filename.substring(0, filename.length() - 5);
					Class<?> obj = Class.forName(packageName + name);
					
					// �ҵ�component
					
					if ((Component) obj.getAnnotation(Component.class) != null) {

						Component com = (Component) obj.getAnnotation(Component.class);
						
						BeanDefinition beandef = new BeanDefinition();
						beandef.setBeanClassName(packageName + name);
						beandef.setBeanClass(obj);

						this.registerBeanDefinition(com.value(), beandef);
					}
				} catch (Exception e) {
					System.out.println("�� ƥ��component������QAQ");
				}
			}
		}
	}

	//ʵ��@autowire
	public Object findAutowire(BeanDefinition beanDefinition) {

		Class<?> obj = beanDefinition.getBeanClass();
		Constructor<?>[] cons = obj.getConstructors();

		// ����ÿ�� constructor ����ע�����
		for (Constructor<?> c : cons) {

			

			if ((Autowired) c.getAnnotation(Autowired.class) != null) {

				Object[] params = new Object[c.getParameterTypes().length];

				for (int i = 0; i < c.getParameterTypes().length; i++) {
					// �ȵõ�����
					String objectName = c.getParameterTypes()[i].getName()
							.split("\\.")[(c.getParameterTypes()[i].getName().split("\\.").length) - 1];
					// ȡ����Ӧobject
					if (getBean(objectName) != null) {// ���Ŀǰmap��û�У�ȥprove����
						params[i] = getBean(objectName);
					}
				}
				// ���ù��캯��
				try {
					return c.newInstance(params);
				} catch (Exception e) {
					System.out.println("�� �Զ�ע�빹�캯��������QAQ");
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
			System.out.println("�� ����object������QAQ");
		} 
		return null;
	}

	
}
