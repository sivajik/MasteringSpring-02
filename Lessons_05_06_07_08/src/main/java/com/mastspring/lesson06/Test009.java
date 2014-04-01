package com.mastspring.lesson06;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

public class Test009 {
	public static void main(String[] args) {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:META-INF/spring/tx-app-context.xml");
		PizzaService service1 = ctx.getBean("pizzaservice1", PizzaService.class);
		service1.orderPizza();
		System.out.println("===============");
		PizzaService service2 = ctx.getBean("pizzaservice2", PizzaService.class);
		service2.orderPizza();
	}
}

interface PizzaService {
	public void orderPizza(); 
	public boolean cookPizza() throws Exception;
}

class PizzaServiceImpl implements PizzaService {
	TransactionTemplate txTemplate;
	String mainIngredient;
	
	public PizzaServiceImpl() {}
	
	public PizzaServiceImpl(PlatformTransactionManager ptm, String mainIngredient) {
		this.txTemplate = new TransactionTemplate(ptm);
		this.mainIngredient = mainIngredient;
	}
	
	public boolean cookPizza() throws Exception{
		if ("chocolate".equalsIgnoreCase(mainIngredient)) {
			throw new RuntimeException("Pizzas can not be made with Chocolate");
		} else {
			System.out.println("Cooking " + mainIngredient + " Pizza...");
			return true;
		}
	}
	
	public void orderPizza() {
		// Usually you need to set them from spring bean XML with out hardcoding here.
		txTemplate.setName("Pizza Transaction");
		txTemplate.setReadOnly(true);
		txTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		txTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_DEFAULT);
		
		txTemplate.execute(new TransactionCallback<Boolean>() {
			public Boolean doInTransaction(TransactionStatus arg0) {
				try {
					return cookPizza();
				} catch (Exception e) {
					arg0.setRollbackOnly(); // Rollback if something happens wrong.
				}
				return false;
			}
		});
	}	 
}