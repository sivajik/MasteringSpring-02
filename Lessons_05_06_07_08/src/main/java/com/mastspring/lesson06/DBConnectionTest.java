package com.mastspring.lesson06;

import java.sql.SQLException;
import java.util.Random;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/*
 * This is a simple class to test whether I am able to connect to my local mysql
 * instance and checking transaction manager is up and running.
 */
public class DBConnectionTest {
	public static void main(String[] args) throws SQLException {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:META-INF/spring/tx-app-context.xml");
		DataSource ds = ctx.getBean("dataSource", DataSource.class);
		System.out.println("Checking Database Name: " + ds.getConnection().getCatalog());
		
		PlatformTransactionManager ptm = ctx.getBean(MyCustomTransactionManager.class);
		System.out.println("Checking Transaction Manager : " + ptm);
		
		MyService myServ = ctx.getBean("myserv", MyService.class);
		myServ.updateRow();
	}
}

@Component
class MyService {
	@Autowired
	MyCustomTransactionManager ptm;
	
	MyService() {}
	
	@Transactional //Don't worry, I will come to this later.
	public void updateRow() {
	  TransactionDefinition def = new DefaultTransactionDefinition();
      TransactionStatus status = ptm.getTransaction(def);
      try {
    	  Random r = new java.util.Random();
    	  int randomNumber = r.nextInt(100);
    	  System.out.println("Random Number: " + randomNumber);
    	  if (randomNumber %2 == 0) {
    		  System.out.println("UPDATE UPDATE UPDATE..."); //Typical DB logic stays here.
    		  ptm.commit(status);
    	  } else  {
    		  throw new Exception("I want to raise an exception so that operation will be rollbacked.");
    	  }
      } catch (Exception e) {
    	  ptm.rollback(status);
      }
	}
}

// Usually we don't implement like this rather we use out of the box implementation.
// this is just to show how typical implementation would be.
@Component ("mycusttxmgr")
class MyCustomTransactionManager implements PlatformTransactionManager {
	@Autowired
	DataSource dataSource;
	
	public void commit(TransactionStatus arg0) throws TransactionException {
		System.out.println("I have commited to my wife. Now going to commit to database as well.");
	}

	public TransactionStatus getTransaction(TransactionDefinition arg0)
			throws TransactionException {
		return null;
	}

	public void rollback(TransactionStatus arg0) throws TransactionException {
		System.out.println("Wish we had rollbacks in life too. BTW roll backed database operation");
	}
}

@Configuration
class DBConnCheckConfig {
	@Bean (name="myserv")
	public MyService getService() {
		return new MyService();
	}
}