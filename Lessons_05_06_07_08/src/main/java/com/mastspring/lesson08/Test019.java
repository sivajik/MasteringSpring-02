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
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

public class Test019 {
	public static void main(String[] args) throws ParseException, IOException {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:META-INF/spring/orm-app-context.xml");
		ProductDaoTxProg dao = ctx.getBean("producttxdaoProgTx", ProductDaoTxProg.class);
		
		dao.addProduct("McVities", 10);
		dao.addProduct("m&m", 20); 
		dao.addProduct("Haribos", 1000); // we expect to rollback this as its >500
		
		dao.listAllProducts();
	}
}

class HighQuantytyExceptionProxTx extends Exception {
	HighQuantytyExceptionProxTx(String msg) {
		super (msg);
	}
}

interface ProductDaoTxProg {
	public void addProduct(String name, int quantity);
	public List<Product> listAllProducts();
}

@Repository
class ProductDaoTxProgImpl implements ProductDaoTxProg {
	@Autowired
	SessionFactory mySessionFactory;//Observe: still I am using hibernate classes ONLY. No spring presence here
	
	TransactionTemplate txTempalte; // Watch out for spring APIs in your code...
	
	@Autowired
	public void setPlatformTransactionMaager(PlatformTransactionManager mgr) {
		this.txTempalte = new TransactionTemplate(mgr);
	}
	
	//Programmatic approach for transaction management.
	public void addProduct(final String name, final int quantity) {
		txTempalte.execute(new TransactionCallbackWithoutResult() {
			
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				Product product = new Product();
				product.setName(name);
				product.setQuantity(quantity);
				Session session = mySessionFactory.openSession();
				try {
					if (product.getQuantity() > 500) {
						throw new HighQuantytyExceptionProxTx("Higher Quantities are not allowed");
					} else {
						session.save(product);				
					}
					System.out.println("Saved: " + product.getId());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}); 
	}

	@Transactional (readOnly=true) // Again Declarative Approach, Can't leave them.. can we?
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