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
		String subject = "🔐 Secure Your Account - OTP Verification Code";

		String message = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;'>"
				+ "<div style='background: linear-gradient(135deg, #27ae60 0%, #229954 100%); padding: 30px; text-align: center; border-radius: 10px 10px 0 0;'>"
				+ "<h1 style='color: white; margin: 0; font-size: 28px;'>Smart Contact Manager</h1>"
				+ "<p style='color: rgba(255,255,255,0.9); margin: 10px 0 0 0;'>Password Reset Verification</p>"
				+ "</div>"
				+ "<div style='background: #f8f9fa; padding: 40px 30px; border-radius: 0 0 10px 10px;'>"
				+ "<p style='color: #2c3e50; font-size: 16px; margin-top: 0;'>Hello,</p>"
				+ "<p style='color: #555; font-size: 15px; line-height: 1.6;'>You requested to reset your password for your Smart Contact Manager account. "
				+ "To proceed with the password reset, please verify your identity using the One-Time Password (OTP) below.</p>"
				+ "<div style='background: white; border: 2px solid #27ae60; border-radius: 8px; padding: 20px; margin: 30px 0; text-align: center;'>"
				+ "<p style='color: #7f8c8d; font-size: 13px; margin: 0 0 15px 0; text-transform: uppercase; letter-spacing: 2px;'>Your OTP Code</p>"
				+ "<h2 style='color: #27ae60; font-size: 48px; margin: 0; letter-spacing: 8px; font-weight: bold;'>" + String.format("%04d", otp) + "</h2>"
				+ "<p style='color: #e74c3c; font-size: 13px; margin: 15px 0 0 0;'>⚠️ This code expires in 10 minutes</p>"
				+ "</div>"
				+ "<div style='background: #fff3cd; border-left: 4px solid #ffc107; padding: 15px; margin: 20px 0; border-radius: 4px;'>"
				+ "<p style='color: #856404; font-size: 14px; margin: 0;'><strong>⚡ Important:</strong> Never share this code with anyone. Our support team will never ask for your OTP.</p>"
				+ "</div>"
				+ "<p style='color: #555; font-size: 14px; margin: 25px 0;'>If you didn't request this password reset, please ignore this email and your account will remain secure.</p>"
				+ "<p style='color: #7f8c8d; font-size: 14px; margin: 30px 0 10px 0;'>Steps to reset your password:</p>"
				+ "<ol style='color: #555; font-size: 14px; padding-left: 20px;'>"
				+ "<li>Copy or note the OTP code above</li>"
				+ "<li>Go back to the password reset page</li>"
				+ "<li>Enter the OTP code when prompted</li>"
				+ "<li>Create a new strong password</li>"
				+ "<li>Complete the password reset process</li>"
				+ "</ol>"
				+ "<hr style='border: none; border-top: 1px solid #ecf0f1; margin: 30px 0;'>"
				+ "<p style='color: #7f8c8d; font-size: 12px; text-align: center; margin: 20px 0;'>"
				+ "© 2026 Smart Contact Manager. All rights reserved.<br>"
				+ "This is an automated message, please do not reply to this email."
				+ "</p>"
				+ "</div>"
				+ "</div>";

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
