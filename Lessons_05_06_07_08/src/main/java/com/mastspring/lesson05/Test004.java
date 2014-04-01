package com.mastspring.lesson05;

import java.util.ArrayList;
import java.util.List;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

public class Test004 {
	public static void main(String[] args) throws Exception {
		ClassPathXmlApplicationContext aContext = new ClassPathXmlApplicationContext("classpath:META-INF/spring/app-context.xml");
		FoodMenu menu = aContext.getBean("british", FoodMenu.class);
		
		menu.printMenu(new FoodItem("Salmon Fishcake", 12.50));
		System.out.println("-----------------------");
		
		List<FoodItem> items = new ArrayList<FoodItem>();
		items.add(new FoodItem("Onion Rings", 3.77));
		items.add(new FoodItem("Fish & Chips", 9.0));
		items.add(new FoodItem("Sticky Toffy Pudding", 5.50));
		menu.printMenu(items);
	}
}

@Component
@Aspect
@Order(2)
class MenuAspect {
	@Pointcut ("execution (* com.mastspring.lesson05.FoodMenu.printMenu(*)) && args(item)")
	public void myPointCut(FoodItem item) {}
	
	@Before(value="myPointCut(item)", argNames="item") //Observe: I didnt mark "jp" here
	public void beforePrintMenuAdvice(JoinPoint jp, FoodItem item) {
		for(Object obj : jp.getArgs()) {
			System.out.println("1) getargs: "+ obj);
		}
		System.out.println("1) Passed in Parameter: " + item);
		System.out.println("1) =======================");
	}
		
	// Support of generics
	@Before("execution (* com.mastspring.lesson05.FoodMenu.printMenu(*)) && args(items)")
	public void beforePrintMenuAsListy(JoinPoint jp, List<FoodItem> items) {
		for (FoodItem item : items) {
			System.out.println("Passed: " + item);
		}
	}
}

@Component
@Aspect
@Order(1) // Lower ordered than above one
class MenuAspectButSameAsAbove {
@Before("execution (* com.mastspring.lesson05.FoodMenu.printMenu(*)) && args(item)") // can you keep argNames here? No
	public void beforePrintMenuAdviceAnotherWay(JoinPoint jp, FoodItem item) {
		for(Object obj : jp.getArgs()) {
			System.out.println("2) getargs: "+ obj);
		}
		System.out.println("2) Passed in Parameter: " + item);
		System.out.println("2) =======================");
	}
}

class FoodItem {
	String itemName;
	double itemPrice;
	
	public FoodItem(String itemName, double itemPrice) {
		super();
		this.itemName = itemName;
		this.itemPrice = itemPrice;
	}
	
	public String toString() {
		return "\"Item : " +  itemName + " (GBP " + itemPrice + ") \"";
	}
}

class FoodMenu {
	String menuName;
	
	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}

	public FoodMenu(String menuName) {
		super();
		this.menuName = menuName;
	}
	
	public void printMenu(List<FoodItem> items) {
		for(FoodItem item : items) {
			System.out.println(menuName + "Menu item : " + item);
		}
	}
	
	public void printMenu(FoodItem item) {
		System.out.println(menuName + " Menu is : " + item);
	}
	
	FoodMenu() {}
}

@Configuration
@EnableAspectJAutoProxy
class MyConfigForAOPArgumentsExamples {
	@Bean (name="british")
	public FoodMenu getBritishFoodMenu() {
		FoodMenu menu = new FoodMenu("British");	
		return menu;
	}

}