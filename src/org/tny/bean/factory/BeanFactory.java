package org.tny.bean.factory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class BeanFactory {
    
	private static File xmlFile = null;
	private static Document xmlDoc = null;
	private static boolean inited = false;
	private static HashMap<Integer,HashMap<String,Object>> beans = new HashMap<Integer,HashMap<String,Object>>();

	
	public static File getXmlFile() {
		return xmlFile;
	}

	public static void setXmlFile(File xmlFile) {
		BeanFactory.xmlFile = xmlFile;
	}
    
	
	private static Object getObject(String className)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		
		Class<?> myClass = Class.forName(className);

		return myClass.newInstance();
	}

	private static Constructor<?> getConstructor(String className, Class<?>... parameterTypes) throws ClassNotFoundException,
			InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException {
		Constructor<?> myConstructor = null;

		Class<?> myClass = Class.forName(className);

		myConstructor = myClass.getConstructor(parameterTypes);
		return myConstructor;
	}

	private static  Object getObject(Constructor<?> myConstructor, Object... initargs)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return myConstructor.newInstance(initargs);
	}
	
	private static Class<?> getPrimitiveClass(String className) throws ClassNotFoundException {
		Class<?> myClass = null;
		switch (className) {
		case "byte":
			myClass = byte.class;
			break;
		case "short":
			myClass = short.class;
			break;
		case "long":
			myClass = long.class;
			break;
		case "float":
			myClass = float.class;
			break;
		case "int":
			myClass = int.class;
			break;
		case "double":
			myClass = double.class;
			break;
		case "char":
			myClass = char.class;
			break;
		case "boolean":
			myClass = boolean.class;
			break;
		default:
			return null;
		}
		return myClass;
	}
	

	private static  Class<?> getPrimitiveArrayClass(String className,int[] dims) throws ClassNotFoundException {
		Class<?> myClass = null;
		switch (className) {
		case "byte":
			myClass =  Array.newInstance(byte.class, dims).getClass();
			break;
		case "short":
			myClass = Array.newInstance(short.class, dims).getClass();
			break;
		case "long":
			myClass =Array.newInstance(long.class, dims).getClass();
			break;
		case "float":
			myClass = Array.newInstance(float.class, dims).getClass();
			break;
		case "int":
			myClass = Array.newInstance(int.class, dims).getClass();
			break;
		case "double":
			myClass = Array.newInstance(double.class, dims).getClass();
			break;
		case "char":
			myClass = Array.newInstance(char.class, dims).getClass();
			break;
		case "boolean":
			myClass =Array.newInstance(boolean.class, dims).getClass();
			break;
		default:
			return null;
		}
		return myClass;
	}
	
	private static  Object getObject(Element bean) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		
		if(bean.hasAttr("constructor")){
		Elements allConArgs = bean.getElementsByTag("constructor-arg");
		for( Element conArg :allConArgs){
			//TODO:构造函数模式，需要完善
		}
		}else{
			return getObject(bean.attr("class"));
		}
		
		return bean;
		
	}
	private static Method getMethod(String className, String methodName, Class<?>... parameterTypes)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException {
		Class<?> myClass = Class.forName(className);
		Method method = myClass.getMethod(methodName, parameterTypes);
		return method;
	}
	private static Method getMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes)
			throws ClassNotFoundException, NoSuchMethodException, SecurityException {
		
		Method method = clazz.getMethod(methodName, parameterTypes);
		return method;
	}
	private static void setFiled(Object obj,String filedName,Object... values) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
 		Class<?>[] valueClazz = new Class[values.length];
 		for(int i =0;i<values.length;i++){
			valueClazz[i] = values[i].getClass();
		}
		
		Method mezod = getMethod(obj.getClass(),"set"+filedName,valueClazz);
 		mezod.invoke(obj, values);
	}
	private static void setFiled(Object obj,Element props) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
 		
		String filedName = props.attr("name");
		String value = props.attr("value");
		
		setFiled(obj,filedName,value);
	}
	private static void init() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException{
		if(inited){
			return;
		}
		xmlDoc = Jsoup.parse(xmlFile, "utf-8");
		Elements allBeans = xmlDoc.getElementsByTag("beans").get(0).getElementsByTag("bean");
		for(Element bean:allBeans){
			initBean(bean);
		}
		
		inited = true;
	}
	private static void  initBean(Element bean) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException{
		String beanId = bean.attr("id").trim();
		HashMap<String,Object> contentHm = new HashMap<String,Object>();
		contentHm.put("object", getObject(bean));
		contentHm.put("scope", bean.attr("scope"));
		beans.put(beanId.hashCode(), contentHm);
		Object obj = beans.get(beanId.hashCode()).get("object");
		Elements allProps = bean.getElementsByTag("property");
		for(Element prop :allProps){
			setFiled(obj,prop);
		}
	}
	private static void init(File xmlFile) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException, IOException{
		BeanFactory.setXmlFile(xmlFile);
		init();
	}
	public static Object getBean(String id) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException, IOException{
		init();
		switch (beans.get(id.trim().hashCode()).get("scope").toString()){
		case "protype" :
			initBean(xmlDoc.getElementById(id));
			break;
		default:
			break;
		}
		return beans.get(id.trim().hashCode()).get("object");
	}
}
