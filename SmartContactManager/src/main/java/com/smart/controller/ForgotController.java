package com.smart.controller;

import java.security.Principal;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.helper.sessionHelper;
import com.smart.model.User;
import com.smart.service.EmailService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ForgotController {

	Random random = new Random();

	@Autowired
	private EmailService emailService;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	// email id form open handler
	@RequestMapping("/forgot")
	public String openEmailForm() {
		return "forgot_email_form";
	}

	// send OTP handler
	@PostMapping("/send-otp")
	public String sendOTP(@RequestParam("email") String email, Principal p, HttpSession session) {

		// generating otp of 4 digit
		int otp = random.nextInt(9999);

		// code for sending otp to email

		String subject = "OTP from SCM";
		String message = " " + "<div style='border: 1px solid #e2e2e2'; padding: 20px'>" + "<h1>" + "OTP is " + "<b>"
				+ otp + "</n>" + "</h1>" + "</div>";
		String to = email;

		User user = this.userRepo.getUserByUsername(email);

		if (user == null) {

			session.setAttribute("message", "Email not recognized. Please enter a valid email to reset your password.");
			return "forgot_email_form";

		} else {

			boolean flag = this.emailService.sendEmail(subject, message, to);

			if (flag) {

				session.setAttribute("myotp", otp);
				session.setAttribute("email", email);

				return "verify_otp";

			} else {

				session.setAttribute("message", "Check your email id!!");
				return "forgot_email_form";
			}
		}

	}

	@PostMapping("/verify-otp")
	public String verifyOTP(@RequestParam("otp") int otp, HttpSession session) {

		int myOtp = (int) session.getAttribute("myotp");
		String email = (String) session.getAttribute("email");

		if (myOtp == otp) {

			/*
			 * //change password from User user = this.userRepo.getUserByUsername(email);
			 * 
			 * if(user == null) { //send error msg session.setAttribute("message",
			 * "User not found with this email"); return "forgot_email_form";
			 * 
			 * }else {
			 * 
			 * //send change password form
			 * 
			 * }
			 */

			return "change_password_form";

		} else {

			session.setAttribute("message", "Please enter valid OTP");
			return "verify_otp";
		}

	}

	// change password handler
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("newpassword") String newpassword, HttpSession session) {

		String email = (String) session.getAttribute("email");
		User user = this.userRepo.getUserByUsername(email);

		user.setPassword(passwordEncoder.encode(newpassword));
		this.userRepo.save(user);

		return "redirect:/signin?change=Password changed successfully!!";
	}
}
