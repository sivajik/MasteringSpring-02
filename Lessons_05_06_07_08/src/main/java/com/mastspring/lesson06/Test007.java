package com.mastspring.lesson06;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class Test007 {
	public static void main(String[] args) {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:META-INF/spring/tx-app-context.xml");
		try {
			Service cs1 = ctx.getBean("sugar_yes_power_yes", Service.class);
			cs1.prepareCandies();
		} catch (Throwable e) {
			e.printStackTrace();
		} 
		
		System.out.println("========");
		
		try {
			Service cs2 = ctx.getBean("sugar_yes_power_no", Service.class);
			cs2.prepareCandies();
		} catch (Throwable e) {
			;
		}
		System.out.println("========");
		
		try {
			Service cs3 = ctx.getBean("sugar_no_power_yes", Service.class);
			cs3.prepareCandies();
		} catch (Throwable e) {
			;
		}
	}
}

interface Service{
	public void prepareCandies() throws NoSugarAvailableException, PowerFailureException;
}

/*
 * IDEA is like below:
 * 	If there is no power - Rollback
 * 	If there is no sugar - dont rollback - go with honey
 * If all good - commit.
 */
@Transactional (isolation=Isolation.DEFAULT, propagation=Propagation.REQUIRED
				,rollbackFor=PowerFailureException.class,readOnly=false)
class CandyService implements Service{	
	@Autowired
	DataSourceTransactionManager dstm;
	double sugarStorage;
	boolean isPowerSupplerExists;
	
	public void setSugarStorage(double sugarStorage) {
		this.sugarStorage = sugarStorage;
	}

	public void setIsPowerSupplerExists(boolean isPowerSupplerExists) {
		this.isPowerSupplerExists = isPowerSupplerExists;
	}

	public CandyService() {}
	
	/*
	 * Food for thought: delete "rollbackFor=PowerFailureException.class" from below code and
	 * see what happens and think why? (second case you see no power but still gets committed)
	 * 
	 * Reason is, when you have local @Transactional annotation that completely overrides top level one.
	 */
	@Transactional(readOnly=true, noRollbackFor=NoSugarAvailableException.class, rollbackFor=PowerFailureException.class)
	public void prepareCandies() throws NoSugarAvailableException, PowerFailureException {
		if (sugarStorage > 1000) { // more than 1000 kgs: all good for today
			if (isPowerSupplerExists) {
				;// Means, all good. so this can be committed.
			} else {
				throw new PowerFailureException(); // On power failure, we rollback everything.
			}
		} else {
			throw new NoSugarAvailableException(); // If no sugar, nothing to worry.
		}
	}
}

class NoSugarAvailableException extends Throwable {
	NoSugarAvailableException() {
		super("No worries if sugar is not there, Let's use honey");
	}
}

class PowerFailureException extends Throwable {
	PowerFailureException() {
		super("Power gone. Nothing can be done now.");
	}
}