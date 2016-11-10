package spring.factory;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import spring.bean.BeanDefinition;

public abstract class abstractApplicationContext implements ApplicationContext {

	private Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, BeanDefinition>();

	public Object getBean(String beanName) {

		if (this.beanDefinitionMap.get(beanName) != null){

			return this.beanDefinitionMap.get(beanName).getBean();
		}
		else
		{
			return null;
		}
			
	}

	public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
		beanDefinition = GetCreatedBean(beanDefinition);
		this.beanDefinitionMap.put(beanName, beanDefinition);
	}

	protected abstract BeanDefinition GetCreatedBean(BeanDefinition beanDefinition);

}
