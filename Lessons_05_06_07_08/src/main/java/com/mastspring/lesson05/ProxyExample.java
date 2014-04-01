package com.mastspring.lesson05;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ProxyExample {
	public static void main(String[] args) {
		UnitConveter conv = new BinaryStringToIntegerConverter();
		System.out.println("Before Proxied : " + conv);
		
		/*
		 * Create a new Dynamic Proxy object so that any invocations on proxy object
		 * goes via "invoke" method of InvocationHandler's Implementation class
		 */
		UnitConveter proxiedObject = (UnitConveter) Proxy.newProxyInstance(conv.getClass().getClassLoader(), 
															conv.getClass().getInterfaces(),
															new MyCustomInvocationHandler(conv));
		
		System.out.println("Output From Plain Object call: " + conv.convert("1001"));
		System.out.println("==============================");
		System.out.println("Output From Proxied Object call: " + proxiedObject.convert("1001"));
	}
}

/*
 * Interface to convert Strings to Numbers
 */
interface UnitConveter {
	public int convert(String str);
}

/*
 * Standard Implementation.
 */
class BinaryStringToIntegerConverter implements UnitConveter {
	public int convert(String str) {
		return Integer.parseInt(str,2);
	}	
}

/*
 * Want to print input and ouput message before/after invoking converter
 * Want to implement some prechecks/postchecks (as of now only printing to console)
 * Want to implement above requirement without touching convert method. 
 */
class MyCustomInvocationHandler implements InvocationHandler {
	Object target;
	
	MyCustomInvocationHandler(Object target) {
		this.target = target;
	}
	
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		preCheckMethod();
		System.out.println("Method Parameters: " + args[0]);
		
		System.out.println("Before Proxied/Original Object : " + target);
		
		Object res = method.invoke(target, args);
		System.out.println("Output :" + res);
		postCheckMethod();
		return res;
	}	
	
	public void preCheckMethod() {
		System.out.println("Pre Checks can be done here....");
	}
	
	public void postCheckMethod() {
		System.out.println("Post Checks can be done here....");
	}
}