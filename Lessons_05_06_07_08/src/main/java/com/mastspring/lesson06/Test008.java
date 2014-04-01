package com.mastspring.lesson06;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.Transactional;

public class Test008 {
	public static void main(String[] args) {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:META-INF/spring/tx-app-context.xml");
		DBActivity dbActivity = ctx.getBean("dbactivity", DBActivity.class);
		dbActivity.runDBCommand();
	}
}

class TimeCalculator implements Ordered{
	
	public void logCurrentTime() {
		System.out.println("About to run some DB logic..");
	}

	public void sayThankyou() {
		System.out.println("Completed exeuction..");
	}
	
	public int getOrder() {
		return 15; // keep something less than tx:annotation-driven's order
	}
}

@Transactional
class DBActivity {
	@Autowired
	DataSourceTransactionManager dstm;
	
	DBActivity() {}
	
	public void runDBCommand() {
		System.out.println("I am executing some trasactional DB Logic here");
	}
}