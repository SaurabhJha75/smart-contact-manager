package com.smart.service;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

	@Value("${mail.smtp.host:smtp.gmail.com}")
	private String host;

	@Value("${mail.smtp.port:465}")
	private String port;

	@Value("${mail.smtp.auth:true}")
	private String auth;

	@Value("${mail.smtp.starttls.enable:true}")
	private String starttls;

	@Value("${mail.smtp.ssl.enable:true}")
	private String sslEnable;

	@Value("${mail.from:}")
	private String fromEmail;

	@Value("${mail.password:}")
	private String emailPassword;

	public boolean sendEmail(String subject, String messageBody, String to) {

		boolean f = false;

		try {
			// Validate email configuration
			if (fromEmail == null || fromEmail.isEmpty()) {
				System.err.println("ERROR: mail.from property is not configured. Please set MAIL_FROM environment variable.");
				return false;
			}

			if (emailPassword == null || emailPassword.isEmpty()) {
				System.err.println("ERROR: mail.password property is not configured. Please set MAIL_PASSWORD environment variable.");
				return false;
			}

			System.out.println("Attempting to send email from: " + fromEmail);
			System.out.println("SMTP Host: " + host + ":" + port);

			// get the system properties
			Properties properties = System.getProperties();

			// setting important information to properties object
			properties.put("mail.smtp.host", host);
			properties.put("mail.smtp.port", port);
			properties.put("mail.smtp.auth", auth);
			properties.put("mail.smtp.starttls.enable", starttls);
			properties.put("mail.smtp.ssl.enable", sslEnable);
			properties.put("mail.smtp.ssl.protocols", "TLSv1.2");
			properties.put("mail.smtp.ssl.ciphers", "TLSv1.2");

			// Step 1: to get the session object
			Session session = Session.getInstance(properties, new Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(fromEmail, emailPassword);
				}
			});

			session.setDebug(false); // Set to true for debugging

			// Step 2: compose the message [text, multimedia]
			MimeMessage m = new MimeMessage(session);

			// from email
			m.setFrom(new InternetAddress(fromEmail));

			// adding recipient to message
			m.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

			// adding subject to message
			m.setSubject(subject);

			// adding content to message as HTML
			m.setContent(messageBody, "text/html; charset=UTF-8");

			// send the email
			// Step 3: send the email using transport method
			Transport.send(m);

			f = true;
			System.out.println("✓ Email sent successfully to: " + to);

		} catch (MessagingException e) {
			System.err.println("✗ Failed to send email to: " + to);
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("✗ Unexpected error while sending email");
			System.err.println("Error: " + e.getMessage());
			e.printStackTrace();
		}

		return f;

	}
}
