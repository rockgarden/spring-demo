package com.example.servingwebcontent;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class GreetingController {

	/**
	 * RequestParam binds the value of the query string parameter name into the name
	 * parameter of the greeting() method. This query string parameter is not
	 * required. If it is absent in the request, the defaultValue of World is used.
	 * The value of the name parameter is added to a Model object, ultimately making
	 * it accessible to the view template.
	 * 
	 * The implementation of the method body relies on a view technology (Thymeleaf)
	 * to perform server-side rendering of the HTML
	 * 
	 * @param name
	 * @param model
	 * @return
	 */
	@GetMapping("/greeting")
	public String greeting(@RequestParam(name = "name", required = false, defaultValue = "World") String name,
			Model model) {
		model.addAttribute("name", name);
		model.addAttribute("host", "https://www.baeldung.com");
		return "greeting";
	}

	// @GetMapping("/")
    // public String index(ModelMap map) {
    //     map.addAttribute("host", "https://www.baeldung.com");
    //     // return template name src/main/resources/templates/index.html
    //     return "index";
    // }

}
