package com.mastspring.lesson07;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

public class Test012 {
	public static void main(String[] args) throws ParseException {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:META-INF/spring/jdbc-app-context.xml");
		OurPetsDAONamedParameters petsdao = ctx.getBean("petsdaonp",OurPetsDAONamedParameters.class);
		System.out.println("Total Count : " + petsdao.getTotalPetCount());
		System.out.println("______________________________________________");
		
		DateFormat df = new SimpleDateFormat("yyyy-dd-MM hh:mm:ss.SSS");//'2014-11-03 04:00:00.000'
		String formattedDate = df.format(new Date());
		Long l = df.parse(formattedDate).getTime();
		Date birthDate = new Date(l);
		
		Pet pet4 = new Pet("Dukey", "Siri", "English Spaniel", 'M', birthDate , null);
		petsdao.addNewPet(pet4);
		
		System.out.println("Total Count : " + petsdao.getTotalPetCount());
		petsdao.printAllPets();
		System.out.println("______________________________________________");

	}
}

@Repository // See I did nt make it as @Component since I want exceptions to be converted to dao hierarchy.
class OurPetsDAONamedParameters {
	NamedParameterJdbcTemplate namedParamJdbcTemplate;
	
	@Autowired
	public void setDataSource(DataSource d) {
		this.namedParamJdbcTemplate = new NamedParameterJdbcTemplate(d);
	}
	
	public void addNewPet(final Pet pet) {
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		
		paramSource.addValue("a", pet.petname);
		paramSource.addValue("b", pet.petowner);
		paramSource.addValue("c", pet.species); 
		paramSource.addValue("d", "" + pet.sex); //but why adding a string prefixed
		paramSource.addValue("e", pet.birth != null ? new java.sql.Date(pet.birth.getTime()) : null);
		paramSource.addValue("f", pet.death != null ? new java.sql.Date(pet.death.getTime()) : null);
		
		this.namedParamJdbcTemplate.update("insert into tbl_ourpets values(:a, :b, :c, :d, :e, :f)", paramSource);
	}
	
	public Pet getPetInfo(String petname) {
		Map paramSource = new HashMap();
		paramSource.put("PETNANAME", petname);
		List<Pet> pets = this.namedParamJdbcTemplate.query("select petname, petowner,species,sex,birth,death from tbl_ourpets where petname=:PETNAME", 
												paramSource, 
												new PetRowMapper());
		return pets.get(0);
	}
	
	public int getTotalPetCount() {
		return this.namedParamJdbcTemplate.queryForInt("select count(*) from tbl_ourpets", new HashMap());
	}
	
	public void printAllPets() {
		System.out.println("Your pets: ");
		List<Map<String, Object>> pets = this.namedParamJdbcTemplate.queryForList("select * from tbl_ourpets", new HashMap());
		for (Map<String, Object> eachElement : pets) {
			for (String key : eachElement.keySet()) {
				System.out.print("(" + key + "," + eachElement.get(key) + ")");
			}
			System.out.println();
		}
	}
}

@Configuration
class Test012Config {
	
	@Bean(name="petsdaonp")
	public OurPetsDAONamedParameters petsdao() {
		OurPetsDAONamedParameters dao = new OurPetsDAONamedParameters();
		return dao;
	}
}