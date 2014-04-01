package com.mastspring.lesson07;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

public class Test011 {
	public static void main(String[] args) throws ParseException {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:META-INF/spring/jdbc-app-context.xml");
		OurPetsDAO petsdao = ctx.getBean("petsdao",OurPetsDAO.class);
		System.out.println("Total Count : " + petsdao.getTotalPetCount());
		System.out.println("______________________________________________");
		
		DateFormat df = new SimpleDateFormat("yyyy-dd-MM hh:mm:ss.SSS");//'2014-11-03 04:00:00.000'
		String formattedDate = df.format(new Date());
		Long l = df.parse(formattedDate).getTime();
		Date birthDate = new Date(l);
		
		Pet pet1 = new Pet("Siri", "Sivaji", "Parrot", 'F', birthDate , null);
		petsdao.addNewPet(pet1);
		
		Pet pet2 = new Pet("Happy", "Keerthi", "Pug Dog", 'M', birthDate , null);
		petsdao.addNewPet(pet2);
		
		Pet pet3 = new Pet("Oliver", "Keerthi", "Western Terrier", 'M', birthDate , null);
		petsdao.addNewPet(pet3);
		
		System.out.println("Total Count : " + petsdao.getTotalPetCount());
		petsdao.printAllPets();
		System.out.println("______________________________________________");
		petsdao.updatePetOwner("Oliver", "Sivaji&Keerthi");
		System.out.println("Full Details of Pet (Oliver) : " + petsdao.getPetInfo("Oliver"));
	}
}

/*
 * CREATE TABLE `tbl_ourpets` (
  `petname` varchar(20) DEFAULT NULL,
  `petowner` varchar(20) DEFAULT NULL,
  `species` varchar(20) DEFAULT NULL,
  `sex` char(1) DEFAULT NULL,
  `birth` date DEFAULT NULL,
  `death` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
 */
class Pet {
	String petname;
	String petowner;
	String species;
	Character sex;
	Date birth;
	Date death;
	
	public Pet(String petname, String petowner, String species, Character sex,
			Date birth, Date death) {
		super();
		this.petname = petname;
		this.petowner = petowner;
		this.species = species;
		this.sex = sex;
		this.birth = birth;
		this.death = death;
	}
	
	public String toString() {
		return  "(Pet Name: " + petname + ")," +
				"(Pet Owner: " + petowner + ")," +
				"(Pet Species: " + species + ")," +
				"(Pet Sex: " + sex + ")," +
				"(Pet Born On : " + birth + ")," +
				"(Pet Died On: " + death + "),";
	}
}

class PetRowMapper implements RowMapper<Pet> {

	public Pet mapRow(ResultSet rs, int rowNum) throws SQLException {
		String petname = rs.getString(1);
		String petowner = rs.getString(2);
		String species = rs.getString(3);
		Character sex = rs.getString(4).charAt(0); // Wow. why suddenly this?
		Date birth = rs.getDate(5);
		Date death = rs.getDate(6);
		
		Pet pet = new Pet(petname, petowner, species, sex, birth, death);
		return pet;
	}
}

@Repository // See I did nt make it as @Component since I want exceptions to be converted to dao hierarchy.
class OurPetsDAO {
	JdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setDataSource(DataSource d) {
		this.jdbcTemplate = new JdbcTemplate(d);
	}
	
	public void addNewPet(final Pet pet) {
		PreparedStatementCreator psc = new PreparedStatementCreator() {
			
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement pstmt = con.prepareStatement("insert into tbl_ourpets values(?,?,?,?,?,?) ");
				pstmt.setString(1, pet.petname);
				pstmt.setString(2, pet.petowner);
				pstmt.setString(3, pet.species); 
				pstmt.setString(4, "" + pet.sex); //but why adding a string prefixed
				pstmt.setDate(5, pet.birth != null ? new java.sql.Date(pet.birth.getTime()) : null);
				pstmt.setDate(6, pet.death != null ? new java.sql.Date(pet.death.getTime()) : null);
				return pstmt;
			}
		};
		this.jdbcTemplate.update(psc);
	}
	
	public Pet getPetInfo(String petname) {
		// oh.. still using ? to denote parameters.
		List<Pet> pets = this.jdbcTemplate.query("select petname, petowner,species,sex,birth,death from tbl_ourpets where petname=?", 
												new Object[]{petname}, 
												new PetRowMapper());
		return pets.get(0);
	}
	
	public int getTotalPetCount() {
		return this.jdbcTemplate.queryForObject("select count(*) from tbl_ourpets", Integer.class);
	}
	
	public void printAllPets() {
		System.out.println("Your pets: ");
		List<Map<String, Object>> pets = this.jdbcTemplate.queryForList("select * from tbl_ourpets");
		for (Map<String, Object> eachElement : pets) {
			for (String key : eachElement.keySet()) {
				System.out.print("(" + key + "," + eachElement.get(key) + ")");
			}
			System.out.println();
		}
	}
	
	public void updatePetOwner(String petName, String newOwner) {
		this.jdbcTemplate.update("update tbl_ourpets set petowner=? where petname=?",
				new Object[]{newOwner, petName});
	}
}

@Configuration
class Test011Config {
	
	@Bean(name="petsdao")
	public OurPetsDAO petsdao() {
		OurPetsDAO dao = new OurPetsDAO();
		return dao;
	}
}