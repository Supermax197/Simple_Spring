package org.tny.test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.tny.bean.factory.BeanFactory;

public class Tester {

	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException, IOException {
		
		BeanFactory.setXmlFile(new File("Test.xml"));
        TestBean myTst = (TestBean)BeanFactory.getBean("test1");
        System.out.println(myTst);
	}

}
