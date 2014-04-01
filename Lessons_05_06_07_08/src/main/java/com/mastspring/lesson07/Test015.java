package com.mastspring.lesson07;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.jdbc.object.SqlUpdate;

public class Test015 {
	public static void main(String[] args) throws ParseException {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:META-INF/spring/jdbc-app-context.xml");
		FoodCourtDao dao = ctx.getBean("fcdao", FoodCourtDao.class);
		dao.createNewFC("gbk", "burgers");
		dao.createNewFC("yo", "sushi");
		dao.getFoodCourt(2);
	}
}

/*
CREATE TABLE tbl_foodcourts
(
  `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `name` varchar(100) DEFAULT NULL,
  `speciality` varchar(200) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
 */
class FoodCourt {
	int id;
	String name;
	String speciality;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSpeciality() {
		return speciality;
	}
	public void setSpeciality(String speciality) {
		this.speciality = speciality;
	}
	public FoodCourt(int id, String name, String speciality) {
		super();
		this.id = id;
		this.name = name;
		this.speciality = speciality;
	}
	@Override
	public String toString() {
		return "FoodCourt [id=" + id + ", name=" + name + ", speciality="
				+ speciality + "]";
	}
}

/*
 * Since this is a thread safe we can use it again and again... and again...
 * Remember This is for Querying only. Hope you observed you are writing one class for one query.
 */
class FoodCourtSqlQuery extends MappingSqlQuery<FoodCourt> {
	FoodCourtSqlQuery(DataSource ds){
		super(ds, "select id, name, speciality from tbl_foodcourts where id=?");
		super.declareParameter(new SqlParameter("id", Types.INTEGER));
		super.compile();
	}
	
	@Override
	protected FoodCourt mapRow(ResultSet rs, int rowNum) throws SQLException {
		FoodCourt fc = new FoodCourt(rs.getInt(1), rs.getString(2), rs.getString(3));
		return fc;
	}
	
	public FoodCourt findMeFoodCourty(int id) {
		return findObject(id);
	}
}

/*
 * Since this is a thread safe we can use it again and again... and again...
 * This is for update (insert also called as updates) only
 */
class FoodCourtSqlUpdate extends SqlUpdate {
	FoodCourtSqlUpdate(DataSource ds){
		super(ds, "insert into tbl_foodcourts (name, speciality) values (?, ?)");
		super.declareParameter(new SqlParameter("a",Types.VARCHAR));
		super.declareParameter(new SqlParameter("b",Types.VARCHAR));
		super.compile();
	}
	
	public void insertFoodCourt(String nameVal, String speciaVal) {
		super.update(nameVal, speciaVal); // it takes varargs, so we are cool here.
	}
}

class FoodCourtDao {
	FoodCourtSqlQuery fcsq;
	FoodCourtSqlUpdate fcsu;
	
	public FoodCourtDao() {}
	
	@Autowired
	public void setDataSource(DataSource ds) {
		this.fcsq = new FoodCourtSqlQuery(ds);
		this.fcsu = new FoodCourtSqlUpdate(ds);
	}
	
	public FoodCourt getFoodCourt(int id) {
		FoodCourt fc =  fcsq.findMeFoodCourty(id);
		System.out.println(fc);
		return fc;
	}
	
	public void createNewFC(String x, String y) {
		fcsu.insertFoodCourt(x, y);
	}
}


@Configuration
class Test015Config {
	
	@Bean(name="fcdao")
	public FoodCourtDao petsdao() {
		FoodCourtDao dao = new FoodCourtDao();
		return dao;
	}
}