package test;

import spring.factory.*;

public class test {

    public static void main(String[] args) {
        String[] locations = {"bean.xml"};
        ApplicationContext ctx = 
		    new LoadAllBeanApplicationContext(locations);
        boss boss = (boss) ctx.getBean("boss");
        System.out.println(boss.tostring());;
    }
}