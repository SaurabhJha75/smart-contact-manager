package com.smart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.helper.Message;
import com.smart.model.User;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController {

	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	// home page handler
	@GetMapping("/")
	public String Home(Model model) {
		model.addAttribute("title", "Home - Smart Conatct Manager");
		return "home";
	}

	// about page handler
	@GetMapping("/about")
	public String About(Model model) {
		model.addAttribute("title", "About - Smart Conatct Manager");
		return "about";
	}

	// SignUp page handler
	@GetMapping("/signup")
	public String Signup(Model model) {
		model.addAttribute("title", "Register - Smart Conatct Manager");
		model.addAttribute("user", new User());
		return "register";
	}
	
	// custom login page handler
	@GetMapping("/signin")
	public String login(Model model) {
		model.addAttribute("title", "Login - Smart Contact Manager");
		return "login";
	}
	
	// Error handler
	@GetMapping("/login-fail")
	public String loginFail(Model model) {
		model.addAttribute("title", "Error - Login Failure");
		return "error";
	}
	
	// Handler for saving the registration data
	@PostMapping("/do_register")
	public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result,
			@RequestParam(value = "agreement", defaultValue = "false") boolean agreement, 
			Model model, HttpSession session) {
		
		try {
			
			if(!agreement) {
				System.out.println("You have not agreed terms and conditions");
				throw new Exception("You have not agreed terms and conditions");
			}
			
			// for field validation 
			if(result.hasErrors()) {
				System.out.println("Error: "+result.toString());
				model.addAttribute("user", user);
				return "register";
			}
			
			user.setRole("ROLE_USER");
			user.setEnanled(true);
			user.setImageUrl("default.png");
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			
			
			System.out.println("Agreement: "+agreement);
			System.out.println("User: "+user);
			
			User result1 = this.userRepo.save(user);
			
			model.addAttribute("user", new User());
			session.setAttribute("message", new Message("Successfully Registered!!", "alert-success"));
			
			return "register";
			
		}catch(Exception e) {
			e.printStackTrace();
			
			model.addAttribute("user",user);
			session.setAttribute("message", new Message("Something Went Wrong!! "+e.getMessage(), "alert-danger"));
			
			return "register";
		}
		
	}

}
