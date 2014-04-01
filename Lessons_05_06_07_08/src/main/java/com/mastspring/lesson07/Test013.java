package com.mastspring.lesson07;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

public class Test013 {
	public static void main(String[] args) throws ParseException {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:META-INF/spring/jdbc-app-context.xml");
		PersonDAO dao = ctx.getBean("personsdao", PersonDAO.class);
		// Usual JdbcTemplate way of creating a Persob Object.
		/*Person p = new Person();
		p.setFname("James");
		p.setLname("Gosing");
		dao.insertPerson(p);*/
		
		List<Person> personList = new ArrayList<Person>();
		for (int i=1; i<=8; i++) { // Observer we are giving 8 entries to the list.
			Person p = new Person();
			p.setFname("User: F" + i);
			p.setLname("User: L" + i);
			personList.add(p);
		}
		dao.batchInsert(personList); // if you uncomment this only first 5 entries will be inserted.
		//dao.batchInsertNamedParameterStyle(personList);
		dao.printAllPersons();
	}
}

/*
CREATE TABLE tbl_persons
(
  `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `fname` varchar(20) DEFAULT NULL,
  `lname` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
 */
class Person {
	int id;
	String fname;
	String lname;
	
	public Person() {}
	public Person(int id, String fname, String lname) {
		super();
		this.id = id;
		this.fname = fname;
		this.lname = lname;
	}
	
	public String toString() {
		return id + ")" + fname + ", " + lname;
	}
	public void setFname(String fname) {
		this.fname = fname;
	}
	public void setLname(String lname) {
		this.lname = lname;
	}
	// Following getter methods are need for "this.namedParamJdbcTemplate.batchUpdate" call.
	// because above call sets the values of query from the properties of the beans.
	public String getFname() {
		return fname;
	}
	public String getLname() {
		return lname;
	}
}

@Repository 
class PersonDAO {
	NamedParameterJdbcTemplate namedParamJdbcTemplate;
	JdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setDataSource(DataSource d) {
		this.namedParamJdbcTemplate = new NamedParameterJdbcTemplate(d);
		this.jdbcTemplate = new JdbcTemplate(d);
	}
	
	/*
	 * regualr jdbc template's way of savning data to database: but uses auto generated key.
	 */
	public void insertPerson(final Person p) {
		KeyHolder holder = new GeneratedKeyHolder();
		
		this.jdbcTemplate.update(new PreparedStatementCreator() {
			
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement pstmt = con.prepareStatement("insert into tbl_persons (fname, lname) values(?,?)", Statement.RETURN_GENERATED_KEYS );
				pstmt.setString(1, p.fname);
				pstmt.setString(2, p.lname);
				return pstmt;
			}
		}, holder);
		System.out.println("Generated Key: " + holder.getKey());
	}
	
	/*
	 * still no fun as we are using jdbctemplate's methods.
	 */
	public void printAllPersons() {
		List<Map<String, Object>> list = this.jdbcTemplate.queryForList("select * from tbl_persons");
		for (Map<String, Object> eachElement : list) {
			for (String key : eachElement.keySet()) {
				System.out.print("(" + key + "," + eachElement.get(key) + ")");
			}
			System.out.println();
		}
	}
	
	// Inserting using a batchUpdate with batch size of 5; Still using '?'
	public void batchInsert(final List<Person> personsList ){
		this.jdbcTemplate.batchUpdate("insert into tbl_persons (fname, lname) values(?,?)", new BatchPreparedStatementSetter() {
			
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setString(1, personsList.get(i).fname);
				ps.setString(2, personsList.get(i).lname);
			}
			
			public int getBatchSize() {
				return 5; //out of 8 entries only first 5 will be added to the database. Usually you use personsList.size()
			}
		});
	}
	
	public void batchInsertNamedParameterStyle(final List<Person> personsList ){
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		for (int i=0; i<personsList.size(); i++) {
			HashMap map = new HashMap();
			System.out.println(personsList.get(i).fname + " , " + personsList.get(i).lname);
			map.put("fname", personsList.get(i).fname);
			map.put("lname", personsList.get(i).lname);
			list.add(map);
		}
		Map<String,String>[] myDataArray=new HashMap[list .size()];
		this.namedParamJdbcTemplate.batchUpdate("insert into tbl_persons (fname, lname) values(:fname,:lname)", 
				list.toArray(myDataArray));
		
		/* Once comment out above code and try this as well
		 * Make sure you have getter methods written for fname, lname properties. otherwise you get error.
		 * */
/*		SqlParameterSource[] paramSource = SqlParameterSourceUtils.createBatch(personsList.toArray());
		this.namedParamJdbcTemplate.batchUpdate("insert into tbl_persons (fname, lname) values(:fname,:lname)", paramSource);*/
	}
}

@Configuration
class Test013Config {
	
	@Bean(name="personsdao")
	public PersonDAO petsdao() {
		PersonDAO dao = new PersonDAO();
		return dao;
	}
}