package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.helper.Message;
import com.smart.model.Contact;
import com.smart.model.User;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private ContactRepository contactRepo;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	// method for adding common data to response
	@ModelAttribute
	public void addCommonData(Model m, Principal p) {
		
		String userName = p.getName();
		User user = userRepo.getUserByUsername(userName);
		
		m.addAttribute("user", user);
	}
	
	
	// Home dashboard handler
	@GetMapping("/index")
	public String dashboard(Model model, Principal principal) {
		
		model.addAttribute("title", "Dashboard - Smart Contact Manager");
		return "normal/user_dashboard";
	}
	
	// Add contact handler
	@GetMapping("/addcontact")
	public String addContact(Model model) {
		
		model.addAttribute("title", "Add Contact - Smart Contact Manager");
		model.addAttribute("contact", new Contact());
		
		return "normal/add_contact";
	}
	
	// processing add contact data handler
	@PostMapping("/savecontact")
	public String saveContact(@Valid @ModelAttribute Contact contact, BindingResult result,
			@RequestParam("profileImage") MultipartFile file, 
			Principal p, Model model, HttpSession session) {
		
		try {
			String name = p.getName();
			User user = this.userRepo.getUserByUsername(name);
			
			//processing and uploading file
			if(file.isEmpty()) {
				
				//if file is empty the try our message
				System.out.println("File is Empty");
				contact.setImage("default.png");
				
			}else {
				
				//upload the file to folder and update the name to contact
				contact.setImage(file.getOriginalFilename());
				
				File saveFile = new ClassPathResource("static/IMG").getFile();

				Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
				
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				
				System.out.println("Image successfully uploaded");
			}
			
			// for field validation
			if(result.hasErrors()) {
				System.out.println("Error: "+result.toString());
				model.addAttribute("contact", contact);
				return "normal/add_contact";
			}
			
			contact.setUser(user);
			user.getContacts().add(contact);
			this.userRepo.save(user);
			
			System.out.println("Data: "+contact);
			System.out.println("Added to the database");
			
			// Success message
			session.setAttribute("message", new Message("Contact successfully added!", "success"));
			
		} catch (Exception e) {
			
			System.out.println("Error: "+e.getMessage());
			e.printStackTrace();
			
			// Error message
			session.setAttribute("message", new Message("Something went wrong !! Try again...", "danger"));
		}
		
		return "normal/add_contact";
	}
	
	// view contact handler
	// per page 5[n]
	// current page = 0[page]
	@GetMapping("/viewcontacts/{page}")
	public String viewContacts(@PathVariable("page") int page, Model m, Principal p) {
		m.addAttribute("title", "All Contacts - Smart Contact Manager");
		
		//fetching the details of loggedIn user
		String userName = p.getName();
		User user = this.userRepo.getUserByUsername(userName);
		
		//currentPage=page
		//contact per page = 5
		Pageable pageable =	PageRequest.of(page, 5);
		
		// sending the list of contacts
		Page<Contact> contacts = this.contactRepo.findContactsByUser(user.getId(), pageable);
		
		m.addAttribute("contacts", contacts);
		m.addAttribute("currentPage", page);
		m.addAttribute("totalPages", contacts.getTotalPages());
		
		return "normal/view_contacts";
	}
	
	//showing particuler contact details
	@GetMapping("/contact/{cId}")
	public String showContactDetail(@PathVariable("cId") int cId, Model model, Principal p) {
		
		Optional<Contact> contactOptional = this.contactRepo.findById(cId);
		Contact contact = contactOptional.get();
		
		//fetching loggedIn user
		String userName = p.getName();
		User user = this.userRepo.getUserByUsername(userName);
		
		//check authorization
		if(user.getId()==contact.getUser().getId())
		{
			model.addAttribute("contact", contact);
			model.addAttribute("title", contact.getName());
		}
		
		System.out.println("CId: "+cId);
		
		return "normal/contact_detail";
	}
	
	//user profile handler
	@GetMapping("/yourprofile")
	public String yourProfile(Model model, Principal p) {
		
		String userName = p.getName();
		User user = this.userRepo.getUserByUsername(userName);
		
		model.addAttribute("user", user);
		model.addAttribute("title", user.getName());	
		
		//System.out.println("USer: "+user);
		
		return "normal/your_profile";
	}
	
	//delete contact
	@GetMapping("/delete/{cId}")
	public String deleteContact(@PathVariable("cId") int cId, Model model, HttpSession session) {
		
	Optional<Contact> contactOptional = this.contactRepo.findById(cId);
	Contact contact = contactOptional.get();
	
	//check authorization
	this.contactRepo.delete(contact);
	
	//for unlinking the contact
	//contact.setUser(null);
		
	session.setAttribute("message", new Message("Contact deleted successfully!!", "success"));
	
		return "redirect:/user/viewcontacts/0";
	}
	
	//open update form handler
	@PostMapping("/updateform/{cId}")
	public String updateForm(@PathVariable("cId") int cId, Model m) {
		
		Contact contact = this.contactRepo.findById(cId).get();
		
		m.addAttribute("title", "Update Contact - Smart Contact Manager");
		m.addAttribute("contact", contact);
		
		return "normal/update_form";
	}
	
	//update contact handler
	@PostMapping("/updatecontact")
	public String updateContact(@ModelAttribute Contact contact,
			@RequestParam("profileImage") MultipartFile file, Principal p, 
			Model model, HttpSession session) {
		
		try {
			
			//old contact details
			Contact oldContactDetails = this.contactRepo.findById(contact.getcId()).get();
			
			
			//image
			if(!file.isEmpty()) {
				
				//file work
				//rewrite
				
				//delete old photo
				File deleteFile = new ClassPathResource("static/IMG").getFile();
				File file1 = new File(deleteFile, oldContactDetails.getImage());
				file1.delete();
				
				//update new photo
				File saveFile = new ClassPathResource("static/IMG").getFile();

				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				
				contact.setImage(file.getOriginalFilename());
				
			}else {
				contact.setImage(oldContactDetails.getImage());
			}
			
			String email = p.getName();
			User user = this.userRepo.getUserByUsername(email);
			
			contact.setUser(user);
			
			this.contactRepo.save(contact);
			
			session.setAttribute("message", new Message("Contact updated successfully", "success"));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("Contact Name: "+contact.getName());
		System.out.println("Contact email: "+contact.getEmail());
		
		return "redirect:/user/contact/"+contact.getcId();
	}
	
	//setting handler
	@GetMapping("/settings")
	public String openSettings(Model model) {
		
		model.addAttribute("title", "Settings - Smart Contact Manager");
		
		return "normal/settings";
	}
	
	//change password handler
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldPassword") String oldPassword, 
			@RequestParam("newPassword") String newPassword, Principal p, HttpSession session) {
		
		/*
		 * System.out.println("Old Password  "+oldPassword);
		 * System.out.println("New Password  "+newPassword);
		 */
		
		String userName = p.getName();
		User currentUser = this.userRepo.getUserByUsername(userName);
		
		//System.out.println(currentUser.getPassword());
		
		if(this.passwordEncoder.matches(oldPassword, currentUser.getPassword())) {
			
			//change the password
			currentUser.setPassword(this.passwordEncoder.encode(newPassword));
			this.userRepo.save(currentUser);
			
			session.setAttribute("message", new Message("Password changed successfully!!", "success"));
			
		}else {
			
			//error....
			session.setAttribute("message", new Message("Please Enter Valid Password !!", "danger"));
			return "redirect:/user/settings";
		}
		
		return "redirect:/user/index";
	}
	
	/*
	 * // Update profile
	 * 
	 * @GetMapping("/updateprofile") public String updateProfile(Model m) { // Add
	 * any necessary model attributes if needed return "normal/update_profile"; }
	 */
    
}
