package spring.factory;

import spring.bean.BeanDefinition;

public interface ApplicationContext {
	Object getBean(String beanName);
	void registerBeanDefinition(String beanName, BeanDefinition beanDefinition);
}
