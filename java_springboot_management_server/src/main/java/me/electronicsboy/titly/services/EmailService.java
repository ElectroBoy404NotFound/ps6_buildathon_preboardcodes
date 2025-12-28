package me.electronicsboy.titly.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    public static final String SIGNUP_BODY = "<table width=\"100%%\" cellpadding=\"0\" cellspacing=\"0\" style=\"max-width: 600px; margin: auto; font-family: Arial, sans-serif; border: 1px solid #dee2e6; background-color: #ffffff;\">\r\n"
    		+ "  <tr>\r\n"
    		+ "    <td style=\"background-color: #0d6efd; padding: 20px; text-align: center;\">\r\n"
    		+ "      <h2 style=\"color: #ffffff; margin: 0;\">Welcome to Our Platform!</h2>\r\n"
    		+ "    </td>\r\n"
    		+ "  </tr>\r\n"
    		+ "  <tr>\r\n"
    		+ "    <td style=\"padding: 20px;\">\r\n"
    		+ "      <p style=\"font-size: 16px; color: #212529;\">Hi <strong>%s</strong>,</p>\r\n"
    		+ "      <p style=\"font-size: 16px; color: #212529;\">\r\n"
    		+ "        Thank you for signing up. Your account is under review and will be approved by an administrator shortly.\r\n"
    		+ "      </p>\r\n"
    		+ "      <p style=\"font-size: 16px; color: #212529;\">\r\n"
    		+ "        You’ll receive an email once your account is activated.\r\n"
    		+ "      </p>\r\n"
    		+ "      <p style=\"font-size: 16px; color: #212529;\">Regards,<br>The Admin Team</p>\r\n"
    		+ "    </td>\r\n"
    		+ "  </tr>\r\n"
    		+ "  <tr>\r\n"
    		+ "    <td style=\"background-color: #f1f1f1; padding: 10px; text-align: center; font-size: 12px; color: #6c757d;\">\r\n"
    		+ "      &copy; 2025 Nikunj Doke\r\n"
    		+ "    </td>\r\n"
    		+ "  </tr>\r\n"
    		+ "</table>\r\n"
    		+ ""; 
    public static final String LOGGEDIN_BODY = "<table width=\"100%%\" cellpadding=\"0\" cellspacing=\"0\" style=\"max-width: 600px; margin: auto; font-family: Arial, sans-serif; border: 1px solid #dee2e6; background-color: #ffffff;\">\r\n"
    		+ "  <tr>\r\n"
    		+ "    <td style=\"background-color: #0d6efd; padding: 20px; text-align: center;\">\r\n"
    		+ "      <h2 style=\"color: #ffffff; margin: 0;\">Is it you?!</h2>\r\n"
    		+ "    </td>\r\n"
    		+ "  </tr>\r\n"
    		+ "  <tr>\r\n"
    		+ "    <td style=\"padding: 20px;\">\r\n"
    		+ "      <p style=\"font-size: 16px; color: #212529;\">Hi <strong>%s</strong>,</p>\r\n"
    		+ "      <p style=\"font-size: 16px; color: #212529;\">\r\n"
    		+ "        Someone has logged into your account. If this wasn't you, please contact the admin team as soon as possible.\r\n"
    		+ "      </p>\r\n"
    		+ "      <p style=\"font-size: 16px; color: #212529;\">Regards,<br>The Admin Team</p>\r\n"
    		+ "    </td>\r\n"
    		+ "  </tr>\r\n"
    		+ "  <tr>\r\n"
    		+ "    <td style=\"background-color: #f1f1f1; padding: 10px; text-align: center; font-size: 12px; color: #6c757d;\">\r\n"
    		+ "      &copy; 2025 Nikunj Doke\r\n"
    		+ "    </td>\r\n"
    		+ "  </tr>\r\n"
    		+ "</table>\r\n"
    		+ "";
    public static final String USERAPPROVED_BODY = "<table width=\"100%%\" cellpadding=\"0\" cellspacing=\"0\" style=\"max-width: 600px; margin: auto; font-family: Arial, sans-serif; border: 1px solid #dee2e6; background-color: #ffffff;\">\r\n"
    		+ "  <tr>\r\n"
    		+ "    <td style=\"background-color: #0d6efd; padding: 20px; text-align: center;\">\r\n"
    		+ "      <h2 style=\"color: #ffffff; margin: 0;\">User approved!</h2>\r\n"
    		+ "    </td>\r\n"
    		+ "  </tr>\r\n"
    		+ "  <tr>\r\n"
    		+ "    <td style=\"padding: 20px;\">\r\n"
    		+ "      <p style=\"font-size: 16px; color: #212529;\">Hi <strong>%s</strong>,</p>\r\n"
    		+ "      <p style=\"font-size: 16px; color: #212529;\">\r\n"
    		+ "        Your account has been approved by the admins!\r\n"
    		+ "        <br>"
    		+ "        Not your account? Pease reply to this email as soon as possible!"
    		+ "      </p>\r\n"
    		+ "      <p style=\"font-size: 16px; color: #212529;\">Regards,<br>The Admin Team</p>\r\n"
    		+ "    </td>\r\n"
    		+ "  </tr>\r\n"
    		+ "  <tr>\r\n"
    		+ "    <td style=\"background-color: #f1f1f1; padding: 10px; text-align: center; font-size: 12px; color: #6c757d;\">\r\n"
    		+ "      &copy; 2025 Nikunj Doke\r\n"
    		+ "    </td>\r\n"
    		+ "  </tr>\r\n"
    		+ "</table>\r\n"
    		+ "";
    public static final String USERREJECTED_BODY = "<table width=\"100%%\" cellpadding=\"0\" cellspacing=\"0\" style=\"max-width: 600px; margin: auto; font-family: Arial, sans-serif; border: 1px solid #dee2e6; background-color: #ffffff;\">\r\n"
    		+ "  <tr>\r\n"
    		+ "    <td style=\"background-color: #0d6efd; padding: 20px; text-align: center;\">\r\n"
    		+ "      <h2 style=\"color: #ffffff; margin: 0;\">User rejected!</h2>\r\n"
    		+ "    </td>\r\n"
    		+ "  </tr>\r\n"
    		+ "  <tr>\r\n"
    		+ "    <td style=\"padding: 20px;\">\r\n"
    		+ "      <p style=\"font-size: 16px; color: #212529;\">Hi <strong>%s</strong>,</p>\r\n"
    		+ "      <p style=\"font-size: 16px; color: #212529;\">\r\n"
    		+ "        Your account has been rejected by the admins!\r\n"
    		+ "        <br>"
    		+ "        Not your account? Pease reply to this email as soon as possible!"
    		+ "      </p>\r\n"
    		+ "      <p style=\"font-size: 16px; color: #212529;\">Regards,<br>The Admin Team</p>\r\n"
    		+ "    </td>\r\n"
    		+ "  </tr>\r\n"
    		+ "  <tr>\r\n"
    		+ "    <td style=\"background-color: #f1f1f1; padding: 10px; text-align: center; font-size: 12px; color: #6c757d;\">\r\n"
    		+ "      &copy; 2025 Nikunj Doke\r\n"
    		+ "    </td>\r\n"
    		+ "  </tr>\r\n"
    		+ "</table>\r\n"
    		+ "";
    public static final String USERDELETED_BODY = "<table width=\"100%%\" cellpadding=\"0\" cellspacing=\"0\" style=\"max-width: 600px; margin: auto; font-family: Arial, sans-serif; border: 1px solid #dee2e6; background-color: #ffffff;\">\r\n"
    		+ "  <tr>\r\n"
    		+ "    <td style=\"background-color: #0d6efd; padding: 20px; text-align: center;\">\r\n"
    		+ "      <h2 style=\"color: #ffffff; margin: 0;\">User deleted!</h2>\r\n"
    		+ "    </td>\r\n"
    		+ "  </tr>\r\n"
    		+ "  <tr>\r\n"
    		+ "    <td style=\"padding: 20px;\">\r\n"
    		+ "      <p style=\"font-size: 16px; color: #212529;\">Hi <strong>%s</strong>,</p>\r\n"
    		+ "      <p style=\"font-size: 16px; color: #212529;\">\r\n"
    		+ "        Your account, %s with ID %d, has been deleted by the admins for reason <b>%s</b>!\r\n"
    		+ "      </p>\r\n"
    		+ "      <p style=\"font-size: 16px; color: #212529;\">Regards,<br>The Admin Team</p>\r\n"
    		+ "    </td>\r\n"
    		+ "  </tr>\r\n"
    		+ "  <tr>\r\n"
    		+ "    <td style=\"background-color: #f1f1f1; padding: 10px; text-align: center; font-size: 12px; color: #6c757d;\">\r\n"
    		+ "      &copy; 2025 Nikunj Doke\r\n"
    		+ "    </td>\r\n"
    		+ "  </tr>\r\n"
    		+ "</table>\r\n"
    		+ "";
    public static final String RESETPASSWORD_BODY = "<table width=\"100%%\" cellpadding=\"0\" cellspacing=\"0\" style=\"max-width: 600px; margin: auto; font-family: Arial, sans-serif; border: 1px solid #dee2e6; background-color: #ffffff;\">\r\n"
    		+ "  <tr>\r\n"
    		+ "    <td style=\"background-color: #0d6efd; padding: 20px; text-align: center;\">\r\n"
    		+ "      <h2 style=\"color: #ffffff; margin: 0;\">Forgot password?</h2>\r\n"
    		+ "    </td>\r\n"
    		+ "  </tr>\r\n"
    		+ "  <tr>\r\n"
    		+ "    <td style=\"padding: 20px;\">\r\n"
    		+ "      <p style=\"font-size: 16px; color: #212529;\">Hi <strong>%s</strong>,</p>\r\n"
    		+ "      <p style=\"font-size: 16px; color: #212529;\">\r\n"
    		+ "        Please use the OTP <b>%s</b> to reset the password to your account!\r\n"
    		+ "      </p>\r\n"
    		+ "      <p style=\"font-size: 16px; color: #212529;\">Regards,<br>The Admin Team</p>\r\n"
    		+ "    </td>\r\n"
    		+ "  </tr>\r\n"
    		+ "  <tr>\r\n"
    		+ "    <td style=\"background-color: #f1f1f1; padding: 10px; text-align: center; font-size: 12px; color: #6c757d;\">\r\n"
    		+ "      &copy; 2025 Nikunj Doke\r\n"
    		+ "    </td>\r\n"
    		+ "  </tr>\r\n"
    		+ "</table>\r\n"
    		+ "";

    @Async
    public void sendSimpleEmail(String to, String subject, String text) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper;
        try {
			helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(text, true);
//			helper.setFrom(text);
		} catch (MessagingException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
//        message.setSubject(subject);
//        message.setText(text);
        mailSender.send(mimeMessage);
    }
}
