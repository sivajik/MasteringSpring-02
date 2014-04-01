package com.mastspring.lesson07;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.AbstractLobCreatingPreparedStatementCallback;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobCreator;

public class Test016 {
	public static void main(String[] args) throws ParseException, IOException {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:META-INF/spring/jdbc-app-context.xml");
		BlobClobDao dao = ctx.getBean("clobblobdao", BlobClobDao.class);
		dao.insertNewRow();
		dao.readBlobClobData();	
	}
}

/*
CREATE TABLE tbl_blobclobdata
(
  `id` INT NOT NULL PRIMARY KEY,
   `image` MEDIUMBLOB DEFAULT NULL,
   `history` BLOB DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
 */
class BlobClobDao {
	public BlobClobDao() {}
	
	private JdbcTemplate template;
	
	@Autowired
	private DefaultLobHandler lobHandler;
	
	@Autowired
	public void setDataSource(DataSource ds) {
		this.template = new JdbcTemplate(ds);
	}
	
	public void insertNewRow() throws IOException {		
		final File blobIn = new File(this.getClass().getClassLoader().getResource("frog.png").getFile());
		final InputStream blobIs = new FileInputStream(blobIn);
		
		final File clobIn = new File(this.getClass().getClassLoader().getResource("longtext.txt").getFile());
		final InputStream clobIs = new FileInputStream(clobIn);
		final InputStreamReader clobReader = new InputStreamReader(clobIs);
		
		System.out.println("DEBUG: Blob: " + blobIn + " , " + blobIs);
		System.out.println("DEBUG: Clob: " + clobIn + " , " + clobIs);
		
		int update = this.template.execute("insert into tbl_blobclobdata(id, history, image) values(?,?,?)", 
				new AbstractLobCreatingPreparedStatementCallback(lobHandler) {
					
					@Override
					protected void setValues(PreparedStatement ps, LobCreator lobCreator)
							throws SQLException, DataAccessException {
						ps.setInt(1, 1); // yes we are just overwriting...thats fine for testing...
						lobCreator.setClobAsCharacterStream(ps, 2, clobReader, (int) clobIn.length());
						lobCreator.setBlobAsBinaryStream(ps, 3, blobIs, (int)blobIn.length());
					}
				});
		blobIs.close();
		clobReader.close();
	}
	
	public void readBlobClobData() {
		List<Map <String, Object>> myList = this.template.query("select id,image, history from tbl_blobclobdata where id=1", 
				new RowMapper<Map<String, Object>> () {
					public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
						Map<String, Object> results = new HashMap<String, Object>();
						
						results.put("myId", rs.getInt(1));

						byte[] blobBytes = lobHandler.getBlobAsBytes(rs, 2);
						results.put("myBlobImage", blobBytes.toString()); 

						// Nasty. but ok for testing to see whether we retrieved frog "as blob" 
						// correctly or not. I am saving it to project space itself... 
						// you may need to change this path
						try {
							FileOutputStream fos = new FileOutputStream("/Users/ski/MasteringSpring/Lessons_05_06_07_08/src/main/resources/newfrog.png");
							fos.write(blobBytes);
							fos.close();
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
						
						String clobText = lobHandler.getClobAsString(rs, 3);
						results.put("myClobText", clobText);
						
						return results;
					}
				});
		System.out.println(myList.get(0).get("myId"));
		System.out.println("New frog image created..");
		System.out.println(myList.get(0).get("myClobText"));
	}
}

@Configuration
class Test016Config {
	
	@Bean(name="clobblobdao")
	public BlobClobDao petsdao() {
		BlobClobDao dao = new BlobClobDao();
		return dao;
	}
}