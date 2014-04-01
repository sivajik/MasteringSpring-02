package com.mastspring.lesson07;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

public class Test014 {
	public static void main(String[] args) throws ParseException {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:META-INF/spring/jdbc-app-context.xml");
		AuthorDAO dao = ctx.getBean("authordao", AuthorDAO.class);
		
		Author a = new Author();
		a.setBookname("A Tale of Two Cities");
		a.setAuthor("Charles Dickens");
		dao.insertAuthor(a);
		
		Author b = new Author();
		b.setBookname("The Hobbit");
		b.setAuthor("J. R. R. Tolkien");
		dao.insertAuthor(b);
		
		dao.printAllAuthors();
		
		dao.queryAuthorTable(2);
	}
}

/*
CREATE TABLE tbl_authors
(
  `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `bookname` varchar(100) DEFAULT NULL,
  `author` varchar(200) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
 */
/*
DELIMITER $$

CREATE PROCEDURE get_author_info (IN in_id INT,  OUT book_name VARCHAR(100),  OUT book_author VARCHAR(200))
BEGIN
  SELECT bookname, author INTO book_name, book_author FROM tbl_authors where id = in_id;
END$$

DELIMITER ;
 */
/*
GRANT EXECUTE ON PROCEDURE test.get_author_info TO 'root'@'localhost';
GRANT EXECUTE ON PROCEDURE test.get_author_info TO ''@'localhost';
 */
class Author {
	int id;
	String bookname;
	String author;
	
	public Author() {}
	public Author(int id, String bookname, String author) {
		super();
		this.id = id;
		this.bookname = bookname;
		this.author = author;
	}
	
	public String toString() {
		return id + ")" + bookname + " By " + author;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getBookname() {
		return bookname;
	}
	public void setBookname(String bookname) {
		this.bookname = bookname;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}

}

@Repository 
class AuthorDAO {
	SimpleJdbcInsert simpleJdbcInsert;
	JdbcTemplate jdbcTemplate; //we need this to print rows.
	SimpleJdbcCall simpleJdbcCall;
	
	@Autowired
	public void setDataSource(DataSource d) {
		// simpleJdbcInsert instance can't be changed once its compiled/created. If so you get exceptions like below
		// org.springframework.dao.InvalidDataAccessApiUsageException: Configuration can't be altered once the class has been compiled or used
		simpleJdbcInsert = new SimpleJdbcInsert(d);
		simpleJdbcInsert = simpleJdbcInsert.withTableName("tbl_authors").usingGeneratedKeyColumns("id");
		jdbcTemplate = new JdbcTemplate(d);
		
		// Initiate SimpleJdbcCall here
		simpleJdbcCall = new SimpleJdbcCall(d).withProcedureName("get_author_info");
	}
	
	public void insertAuthor(Author a) {
		Map <String, Object> args = new HashMap<String, Object>();
		args.put("bookname", a.getBookname());
		args.put("author", a.getAuthor());
		KeyHolder k = this.simpleJdbcInsert.executeAndReturnKeyHolder(args); // you could use SqlParameterSource too
		System.out.println("Inserted : " + k.getKey().toString());
	}
	
	public void queryAuthorTable(int id) {
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("in_id", id);
		Map<String, Object> outputs = simpleJdbcCall.execute(paramSource);
		System.out.println("Output: " + outputs.get("book_name") + " , " + outputs.get("book_author"));
	}
	
	
	public void printAllAuthors() {
		List<Map<String, Object>> list = jdbcTemplate.queryForList("select * from tbl_authors");
		for (Map<String, Object> eachElement : list) {
			for (String key : eachElement.keySet()) {
				System.out.print("(" + key + "," + eachElement.get(key) + ")");
			}
			System.out.println();
		}
	}
}

@Configuration
class Test014Config {
	
	@Bean(name="authordao")
	public AuthorDAO petsdao() {
		AuthorDAO dao = new AuthorDAO();
		return dao;
	}
}