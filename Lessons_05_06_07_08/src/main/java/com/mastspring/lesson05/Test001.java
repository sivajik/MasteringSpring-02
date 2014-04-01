package com.mastspring.lesson05;

import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

public class Test001 {
	public static void main(String[] args) {
		// Classic XML based configuration
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:META-INF/spring/app-context.xml");
		PositiveNumberEvaluator evaluator = ctx.getBean("postinumeval", PositiveNumberEvaluator.class);
		evaluator.evaluate();
	}
}

@Aspect
@Component
class LogAspect { //Making normal class as Aspect
	
	@Pointcut (value="execution (* com.mastspring.lesson05.PositiveNumberEvaluator.evaluate())")
	public void pointcutMethod() { }

	// Referring Pointcuts defined as above. This code runs as before advice
	@Before ("com.mastspring.lesson05.LogAspect.pointcutMethod()")
	public void sayWelcomeAdvice() {
		System.out.println("Wow! This runs before evaluate method of com.mastsprng.lesson05.PositiveNumberEvaluator");
	}
	
	// Using inline pointcut expressions. This code runs as after advice
	@After ("execution (* com.mastspring.lesson05.PositiveNumberEvaluator.evaluate())")
	public void sayGoodByeAdvice() {
		System.out.println("Thank you using this service.");
	}
}

@Component
class PositiveNumberEvaluator {
	Integer[] inputs;
	
	PositiveNumberEvaluator() {}
	PositiveNumberEvaluator(Integer[] inputs) {
		this.inputs = inputs;
	}
	
	public void evaluate() {
		for (Integer i : inputs) {
			if (i > 0) {
				System.out.println("Input: " + i);
			}
		}
	}
}

@Configuration
@EnableAspectJAutoProxy // Try running the code with out this line and see what happens.
class MyConfig {
	@Bean (name="postinumeval")
	public PositiveNumberEvaluator getPosNumEvaluator() {
		Integer[] inputs = new Integer[]{3,7,-9,2,1};
		return new PositiveNumberEvaluator(inputs);
	}
}