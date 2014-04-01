package com.mastspring.lesson06;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;


public class Test010 {
	public static void main(String[] args) {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:META-INF/spring/tx-app-context.xml");
		IceCreamService service1 = ctx.getBean("icecreamservice1", IceCreamService.class);
		service1.orderIceCream();
		System.out.println("===============");
		IceCreamService service2 = ctx.getBean("icecreamservice2", IceCreamService.class);
		service2.orderIceCream();
	}
}

interface IceCreamService {
	public void orderIceCream(); 
	public boolean servceIceScream() throws Exception;
}

class IceCreamServiceImpl implements IceCreamService {
	PlatformTransactionManager ptm;
	String mainIngredient;
	
	public IceCreamServiceImpl() {}
	
	public IceCreamServiceImpl(PlatformTransactionManager ptm, String mainIngredient) {
		this.ptm = ptm;
		this.mainIngredient = mainIngredient;
	}
	
	public boolean servceIceScream() throws Exception{
		if ("Cheese_and_Onion".equalsIgnoreCase(mainIngredient)) {
			throw new RuntimeException("No matter how you scream, ice cream cant be made of cheese & onion");
		} else {
			System.out.println("Serving " + mainIngredient + " Pizza...");
			return true;
		}
	}
	
	public void orderIceCream() {
		DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
		definition.setTimeout(10);
		definition.setIsolationLevel(TransactionDefinition.ISOLATION_DEFAULT);
		definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		
		TransactionStatus status = ptm.getTransaction(definition);
		
		try {
			boolean result = servceIceScream();
			ptm.commit(status);
		} catch (Exception e) {
			System.out.println(e.toString());
			ptm.rollback(status);
		}
	}	 
}