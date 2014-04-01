package com.mastspring.lesson05;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

public class Test003 {
	public static void main(String[] args) throws Exception {
		try {
			ClassPathXmlApplicationContext aContext = new ClassPathXmlApplicationContext("classpath:META-INF/spring/app-context.xml");
			BankAccountService barclays = aContext.getBean("barclays", BankAccountService.class);
			barclays.withDraw(30);
			System.out.println(barclays);
	
			System.out.println("==========================================");
			barclays.withDraw(50);
			System.out.println(barclays);
		} catch (NoBalanceException nbe) {
			;
		}
	}
}

@Aspect
@Component
class BankAccountAspect {
	@Pointcut (value="execution(* com.mastspring.lesson05.BankAccountService.withDraw(*) throws NoBalanceException)")
	public void myPointCut() {}
	
	// Before Advice
	@Before (value="myPointCut()")
	public void logMessages() {
		System.out.println("Welcome to Funds Transfer Service");
	}

	// After Finally Advice
	@After (value="myPointCut()")
	public void thankyou() {
		System.out.println("Thank you for using this service. Let me release all resources.");
	}

	// After Returning Advice
	@AfterReturning (value="myPointCut()", returning="resultValue") // capture the return value
	public void completedOk(Object resultValue) {
		System.out.println("Service completed successfully with no exceptions. Result: " + resultValue);
	}
	
	// After Throwing Advice
	@AfterThrowing (value="myPointCut()", throwing="myException") // capture the exception
	public void faiedOops(Throwable myException) {
		System.out.println("Exception Occured!!! Let me share it in facebook. (" + myException.getMessage() + ")");
	}

	//Around Advice, Observer, first argument MUST be ProceedingJoinPoint only
	@Around (value="myPointCut()")
	public Object beforeAndAfterLogic(ProceedingJoinPoint jp) throws Throwable { 
		System.out.println("Operation Started with Balance: GBP" + ((BankAccountService)jp.getTarget()).balance);
		long time = System.currentTimeMillis();
		Object retValue = jp.proceed();//jp.proceed(inputs);
		time = System.currentTimeMillis() - time;
		System.out.println("Operation Completed with Closing Balance of GBP" + retValue + " in " + time + " ms");	
		return retValue;
	}
}

class NoBalanceException extends Exception {
	NoBalanceException(String exceptionMsg) {
		super (exceptionMsg);
	}
}

class BankAccountService {
	public double balance;
	
	BankAccountService() {}
	BankAccountService(double openingBalance) {
		this.balance = openingBalance;
	}
	
	public double withDraw(double amount) throws NoBalanceException{
		if (balance-amount > 0) {
			balance = balance - amount;
			return balance;
		} else {
			throw new NoBalanceException("Insufficnet Funds. Learn new stuff and change your job.");
		}
	}
	
	public String toString() {
		return "\n**** Hello Customer, Your Balance: GBP" + balance + "\n";
	}
}


@Configuration
@EnableAspectJAutoProxy
class MyConfigForAOPAdviceExamples {
	
	@Bean (name="barclays")
	public BankAccountService getBankAccntService() {
		return new BankAccountService(70);
	}
}