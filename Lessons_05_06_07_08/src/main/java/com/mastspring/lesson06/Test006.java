package com.mastspring.lesson06;

import java.sql.SQLException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.annotation.EnableTransactionManagement;

public class Test006 {
	public static void main(String[] args) throws SQLException {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:META-INF/spring/tx-app-context.xml");
		EmployementService eServ = ctx.getBean("empserv", EmployementService.class);
		System.out.println(eServ);
		eServ.addEmployee(new Employee());
	}
}

class Employee {
	Employee() {}
}

interface EmployementService {
	public void addEmployee(Employee e);
	public String printEmployee(Employee e);
}

class EmployeeServiceImpl implements EmployementService {

	public void addEmployee(Employee e) {
		throw new UnsupportedOperationException("BANG...");
	}

	public String printEmployee(Employee e) {
		return e.toString();
	}
}
@Configuration
class Test006Config {
	
	@Bean (name="empserv")
	public EmployementService getEmpServ() {
		return new EmployeeServiceImpl();
	}
}