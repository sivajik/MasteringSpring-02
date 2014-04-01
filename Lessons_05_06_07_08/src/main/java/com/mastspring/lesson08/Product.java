package com.mastspring.lesson08;

/*
create table tbl_products (ID bigint not null auto_increment, name varchar(255), quantity integer, primary key (ID))
 */
public class Product {
	
	public Product() {}
	
	public Product(long id, String name, int quantity) {
		super();
		this.id = id;
		this.name = name;
		this.quantity = quantity;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
	long id;
	String name;
	int quantity;
	
	@Override
	public String toString() {
		return "Product [id=" + id + ", name=" + name + ", quantity="
				+ quantity + "]";
	}
}
