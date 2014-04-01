package com.mastspring.lesson08;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

public class Test018 {
	public static void main(String[] args) throws ParseException, IOException {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:META-INF/spring/orm-app-context.xml");
		ProductDaoTx dao = ctx.getBean("producttxdao", ProductDaoTx.class);
		
		dao.addProduct("The North Face Jacket", 1);
		dao.addProduct("Hand Gloves", 2000); // we expect to roll back this as its >500
		dao.addProduct("Nike Shoes", 1);
		
		dao.listAllProducts();
	}
}

class HighQuantytyException extends Exception {
	HighQuantytyException(String msg) {
		super (msg);
	}
}

interface ProductDaoTx {
	public void addProduct(String name, int quantity);
	public List<Product> listAllProducts();
}

@Repository
class ProductDaoTxImpl implements ProductDaoTx {
	@Autowired
	SessionFactory mySessionFactory;//Observe: still I am using hibernate classes ONLY. No spring presence here
	
	@Transactional (rollbackFor=HighQuantytyException.class) // Here comes the magic
	public void addProduct(String name, int quantity) {
		Product product = new Product();
		product.setName(name);
		product.setQuantity(quantity);

		Session session = mySessionFactory.openSession();
		try {
			if (product.getQuantity() > 500) {
				throw new HighQuantytyException("Higher Quantities are not allowed");
			} else {
				session.save(product);				
			}
			System.out.println("Saved: " + product.getId());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Transactional (readOnly=true) // Here comes the magic (again)
	public List<Product> listAllProducts() {
		Session session = mySessionFactory.openSession();
		List<Product> prods = new ArrayList<Product>();
		try {
			prods = session.createQuery("from Product").list();
			System.out.println("Total Objects: " + prods.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for(Product p : prods) {
			System.out.println(p);
		}
		return prods;
	}
}