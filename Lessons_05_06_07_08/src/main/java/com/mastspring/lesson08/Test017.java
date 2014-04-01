package com.mastspring.lesson08;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Test017 {
	public static void main(String[] args) throws ParseException, IOException {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:META-INF/spring/orm-app-context.xml");
		ProductDao dao = ctx.getBean("productdao", ProductDaoImpl.class);
		
		dao.addProduct("Macbook Pro", 1);
		dao.addProduct("iPod", 2);
		dao.addProduct("iPad", 1);
		
		dao.listAllProducts();
	}
}

interface ProductDao {
	public void addProduct(String name, int quantity);
	public List<Product> listAllProducts();
}

class ProductDaoImpl implements ProductDao {
	@Autowired
	SessionFactory mySessionFactory;//Observe: still I am using hibernate classes ONLY. No spring presence here
	
	public void addProduct(String name, int quantity) {
		Product product = new Product();
		product.setName(name);
		product.setQuantity(quantity);

		Session session = mySessionFactory.openSession();
		Transaction tx = session.getTransaction();
		try {
			tx.begin();
			session.save(product);
			tx.commit();
			System.out.println("Saved: " + product.getId());
		} catch (Exception e) {
			e.printStackTrace();
			tx.rollback();
		}
	}

	public List<Product> listAllProducts() {
		Session session = mySessionFactory.openSession();
		Transaction tx = session.getTransaction();
		List<Product> prods = new ArrayList<Product>();
		try {
			tx.begin();
			prods = session.createQuery("from Product").list();
			System.out.println("Total Objects: " + prods.size());
			tx.commit();
		} catch (Exception e) {
			e.printStackTrace();
			tx.rollback();
		}
		
		for(Product p : prods) {
			System.out.println(p);
		}
		return prods;
	}
}

@Configuration
class Test017Config {
	
	@Bean(name="productdao")
	public ProductDao petsdao() {
		ProductDao dao = new ProductDaoImpl();
		return dao;
	}
}