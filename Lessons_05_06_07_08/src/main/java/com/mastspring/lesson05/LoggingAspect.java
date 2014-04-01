package com.mastspring.lesson05;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;

//this class is an Aspect.Just like any regular java class.
@Aspect 
public class LoggingAspect {
 
	/*
	 * Applying "after" advice for a below pointcut expression
	 * 	"execution(* com.mastspring.lesson05.CustomerBo.setName(..))" 
	 * i.e this method code'll be called after CustomerBo.setName() is completed.
	 */
	@After("execution(* com.mastspring.lesson05.CustomerBo.setName(..))")
	public void afterNameWasSet(JoinPoint joinPoint) {
		System.out.println("We just now have set the name property..");
		System.out.println("We CAN write some more code here to for post processing");
	}
}