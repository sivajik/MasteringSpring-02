package com.mastspring.lesson05;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

public class Test005 {
	public static void main(String[] args) throws Exception {
		try {
			ClassPathXmlApplicationContext aContext = new ClassPathXmlApplicationContext("classpath:META-INF/spring/app-context.xml");
			BankAccountServiceSchemaBased hsbc = aContext.getBean("hsbc", BankAccountServiceSchemaBased.class);
			hsbc.withDraw(30);
			System.out.println(hsbc);
	
			System.out.println("==========================================");
			hsbc.withDraw(50);
			System.out.println(hsbc);
		} catch (NoBalanceExceptionSchemaBased nbe) {
			;
		}
	}
}

class BankAccountAspectSchemaBased {
	public void logMessages() {
		System.out.println("Welcome to Funds Transfer Service");
	}

	public void thankyou() {
		System.out.println("Thank you for using this service. Let me release all resources.");
	}

	public void completedOk(Object resultValue) {
		System.out.println("Service completed successfully with no exceptions. Result: " + resultValue);
	}
	
	public void faiedOops(Throwable myException) {
		System.out.println("Exception Occured!!! Let me share it in facebook. (" + myException.getMessage() + ")");
	}

	public Object beforeAndAfterLogic(ProceedingJoinPoint jp) throws Throwable { 
		System.out.println("Operation Started with Balance: GBP" + ((BankAccountServiceSchemaBased)jp.getTarget()).balance);
		long time = System.currentTimeMillis();
		Object retValue = jp.proceed();//jp.proceed(inputs);
		time = System.currentTimeMillis() - time;
		System.out.println("Operation Completed with Closing Balance of GBP" + retValue + " in " + time + " ms");	
		return retValue;
	}
}

class NoBalanceExceptionSchemaBased extends Exception {
	NoBalanceExceptionSchemaBased(String exceptionMsg) {
		super (exceptionMsg);
	}
}

class BankAccountServiceSchemaBased {
	public double balance;
	
	BankAccountServiceSchemaBased() {}
	BankAccountServiceSchemaBased(double openingBalance) {
		this.balance = openingBalance;
	}
	
	public double withDraw(double amount) throws NoBalanceExceptionSchemaBased{
		if (balance-amount > 0) {
			balance = balance - amount;
			return balance;
		} else {
			throw new NoBalanceExceptionSchemaBased("Insufficnet Funds. Learn new stuff and change your job.");
		}
	}
	
	public String toString() {
		return "\n**** Hello Customer, Your Balance: GBP" + balance + "\n";
	}
}

@Configuration
@EnableAspectJAutoProxy
class MyConfigForAOPSchemaBasedExamples {
	
	@Bean (name="hsbc")
	public BankAccountServiceSchemaBased getBankAccntService() {
		return new BankAccountServiceSchemaBased(70);
	}
}