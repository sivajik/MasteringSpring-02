package com.mastspring.lesson05;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

public class Test002 {
	public static void main(String[] args) throws Exception {
		ClassPathXmlApplicationContext aContext = new ClassPathXmlApplicationContext("classpath:META-INF/spring/app-context.xml");
		PoshAndCostlyCar ferrari = aContext.getBean("ferraribean", PoshAndCostlyCar.class);

		System.out.println("Proxied Object: " + ferrari); // toString() calls some aspects code too.
		
		/*
		 * Lets see all interfaces our proxied object is implementing apart from ours.
		 */
		for (Class c : ferrari.getClass().getInterfaces()) {
			System.out.println("Interface: \"" + c + "\"");
		}
		System.out.println("-------------------------------------------------------");
		
		// Keep un-commenting one after another and try understandin from outputs.
		
		//1
//		ferrari.race();
//		System.out.println("-------------------------------------------------------");
		
		//2
//		ferrari.engine();
//		EngineModel model = new EngineModel();
//		model.setName("Nissan 123");
//		ferrari.engine(model);
//		System.out.println("-------------------------------------------------------");
		
		//3
//		ferrari.raceWithAnotherCar(ferrari);
//		System.out.println("-------------------------------------------------------");
		
		//4
//		ferrari.amIaPoshCar();
//		System.out.println("-------------------------------------------------------");
	}
}

@Retention (RetentionPolicy.RUNTIME)
@Target (ElementType.METHOD)
@interface RaceCar {
	String nameOfTheCar();
}

@Retention (RetentionPolicy.RUNTIME)
@Target (ElementType.TYPE)
@interface FullyFittedCar {
}

@Component
@Aspect
class MotorVehicleAspect {
	
	// 1: Example for "@annotation". Match only if method has @RaceCar annotations
	@Pointcut ( "@annotation(RaceCar)" )
	public void fillFuelPC() {}
	
	@Before (value="fillFuelPC()")
	public void fillFuel() {
		System.out.println("**** ASPECT CODE **** Fill Engine with Tesco FUEL ****");
	}
	
	// 2: Example for "args"
	@Pointcut(" execution (* com.mastspring.lesson05.Ferrari.engine(..)) ")
	public void fixEnginePointcut() {}
	
	// Match if method name is "engine" with arbitary number of parameters
	@Before(value="fixEnginePointcut()")
	public void fixEngine() {
		System.out.println("**** ASPECT CODE **** Fixing Engine ****");
	}
	
	// Match if method name is "engine" with "EngineModel" as argument.
	@Before(value="fixEnginePointcut() && args(com.mastspring.lesson05.EngineModel)")
	public void fixEngineWithNewEngine() {
		System.out.println("**** ASPECT CODE **** Fixing Engine with New Engine****");
	}
	
	// 3: Example for "@args" : Match if argument has @FullyFittedCar annotation
	@Before ("execution (* com.mastspring.lesson05.Ferrari.*(*)) && args(com.mastspring.lesson05.Ferrari) && @args(com.mastspring.lesson05.FullyFittedCar)")
	public void raceWithAnotherCar() {
		System.out.println("**** ASPECT CODE **** Race With Another Car ****");
	}
	
	//4: Example for "this" (where proxy implements PoshAndCostlyCar,HighSpeedCar,other spring interfaces
	@Before ("this(com.mastspring.lesson05.HighSpeedCar) && this(com.mastspring.lesson05.PoshAndCostlyCar)")
	public void thisPointcut() {
		System.out.println("**** ASPECT CODE **** Proxy is implemented by SpringProxy/Advised/Custom Interfaces ****");
	}
	
	//5: Example for "target" (where target implements PoshAndCostlyCar interface. See logical OR
	// http://stackoverflow.com/questions/11924685/spring-aop-target-vs-this 
	@Before ("target(com.mastspring.lesson05.PoshAndCostlyCar) || target(com.mastspring.lesson05.HighSpeedCar)")
	public void targetPointcut() {
		System.out.println("**** ASPECT CODE **** I am a PoshAndCostlyCar ****");
	}
}

interface PoshAndCostlyCar {
	public void race();
	public void engine(EngineModel model);
	public void engine() ;
	public void raceWithAnotherCar( Ferrari f1);
	public void amIaPoshCar();	
}

interface HighSpeedCar {

}

@FullyFittedCar
class Ferrari implements PoshAndCostlyCar, HighSpeedCar {
	Ferrari() {}
	
	@RaceCar (nameOfTheCar="Ferrari Model 123")
	public void race() {
		System.out.println("I am a piece of metal costs loads of money. I am called as Ferrari");
	}
	
	public void engine(EngineModel model) {
		System.out.println("Fix Ferrari Engine with " + model.name + " Engine");
	}
	
	public void engine() {
		System.out.println("Fix Ferrari Engine with WHAT?");
	}
	
	public void raceWithAnotherCar( Ferrari f1) {
		System.out.println("I can not race with another car :( " + f1);
	}
	
	public void amIaPoshCar() {
		System.out.println("Yes, I am a bloody costly and posh car");
	}
}

class EngineModel {
	public EngineModel() {}
	public void setName(String name) { this.name = name; }
	public String name;
}

@Configuration
@EnableAspectJAutoProxy
class MyConfigForAOPExamples {
	
	@Bean (name="ferraribean")
	public PoshAndCostlyCar createFerrari() {
		return new Ferrari();
	}
}