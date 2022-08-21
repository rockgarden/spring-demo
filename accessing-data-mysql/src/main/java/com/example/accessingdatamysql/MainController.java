package com.example.accessingdatamysql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller // This means that this class is a Controller
@RequestMapping(path = "/demo") // This means URL's start with /demo (after Application path)
public class MainController {
	@Autowired
	// This means to get the bean called userRepository
	// Which is auto-generated by Spring, we will use it to handle the data
	private UserRepository userRepository;

	@PostMapping(path = "/add") // Map ONLY POST Requests
	public @ResponseBody String addNewUser(@RequestParam String name, @RequestParam Integer age,
			@RequestParam String email) {
		// @ResponseBody means the returned String is the response, not a view name
		// @RequestParam means it is a parameter from the GET or POST request
		User n = new User(name, age, email);
		userRepository.saveAndFlush(n);
		log.trace(n.toString());
		return n.toString();
	}

	@PostMapping(path = "/del") // Map ONLY POST Requests
	public @ResponseBody String delUser(@RequestParam String name) {
		User n = userRepository.findByName(name);
		if (n != null) {userRepository.delete(n);
			log.trace("delete " + n.toString());
			return ("delete " + n.getName());
		} else {
			return "User " + name + " does not exist";
		}
	}

	@GetMapping(path = "/all")
	public @ResponseBody Iterable<User> getAllUsers() {
		// This returns a JSON or XML with the users
		return userRepository.findAll();
	}

}
