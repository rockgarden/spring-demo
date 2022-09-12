package com.example.relationaldataaccess;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
public class RelationalDataAccessApplication implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(RelationalDataAccessApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(RelationalDataAccessApplication.class, args);
	}

	/**
	 * Spring provides a template class called JdbcTemplate that makes it easy to
	 * work with SQL relational databases and JDBC. Most JDBC code is mired in
	 * resource acquisition, connection management, exception handling, and general
	 * error checking that is wholly unrelated to what the code is meant to achieve.
	 * The JdbcTemplate takes care of all of that for you. All you have to do is
	 * focus on the task at hand.
	 * 
	 * Spring Boot supports H2 (an in-memory relational database engine) and
	 * automatically creates a connection. Because we use spring-jdbc, Spring Boot
	 * automatically creates a JdbcTemplate. The @Autowired JdbcTemplate field
	 * automatically loads it and makes it available.
	 */
	@Autowired
	JdbcTemplate jdbcTemplate;

	/**
	 * This Application class implements Spring Boot’s CommandLineRunner, which
	 * means it will execute the run() method after the application context is
	 * loaded.
	 */
	@Override
	public void run(String... strings) throws Exception {

		log.info("Creating tables");

		// First, install some DDL by using the execute method of JdbcTemplate.
		jdbcTemplate.execute("DROP TABLE customers IF EXISTS");
		jdbcTemplate.execute("CREATE TABLE customers(" + "id SERIAL, first_name VARCHAR(255), last_name VARCHAR(255))");

		/**
		 * Second, take a list of strings and, by using Java 8 streams, split them into
		 * firstname/lastname pairs in a Java array.
		 */
		List<Object[]> splitUpNames = Arrays.asList("John Woo", "Jeff Dean", "Josh Bloch", "Josh Long").stream()
				.map(name -> name.split(" ")).collect(Collectors.toList());

		// Use a Java 8 stream to print out each tuple of the list
		splitUpNames.forEach(name -> log.info(String.format("Inserting customer record for %s %s", name[0], name[1])));

		/**
		 * Third, install some records in your newly created table by using the
		 * batchUpdate method of JdbcTemplate. The first argument to the method call is
		 * the query string. The last argument (the array of Object instances) holds the
		 * variables to be substituted into the query where the ? characters are.
		 * 
		 * For single insert statements, the insert method of JdbcTemplate is good.
		 * However, for multiple inserts, it is better to use batchUpdate.
		 * 
		 * Use ? for arguments to avoid SQL injection attacks by instructing JDBC to
		 * bind variables.
		 * 
		 * 采用 ？通过指示JDBC绑定变量来避免SQL注入攻击的参数。
		 */
		jdbcTemplate.batchUpdate("INSERT INTO customers(first_name, last_name) VALUES (?,?)", splitUpNames);

		log.info("Querying for customer records where first_name = 'Josh':");

		/**
		 * Finally, use the query method to search your table for records that match the
		 * criteria. You again use the ? arguments to create parameters for the query,
		 * passing in the actual values when you make the call. The last argument is a
		 * Java 8 lambda that is used to convert each result row into a new Customer
		 * object.
		 * 
		 * Java 8 lambdas map nicely onto single method interfaces, such as Spring’s
		 * RowMapper.
		 */
		jdbcTemplate.query("SELECT id, first_name, last_name FROM customers WHERE first_name = ?",
				new Object[] { "Josh" },
				(rs, rowNum) -> new Customer(rs.getLong("id"), rs.getString("first_name"), rs.getString("last_name")))
				.forEach(customer -> log.info(customer.toString()));
	}
}
