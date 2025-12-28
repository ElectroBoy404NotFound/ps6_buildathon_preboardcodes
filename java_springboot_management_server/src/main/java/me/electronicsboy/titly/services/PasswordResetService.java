package me.electronicsboy.titly.services;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import me.electronicsboy.titly.exceptions.InvalidOtpException;
import me.electronicsboy.titly.exceptions.UserNotFoundException;
import me.electronicsboy.titly.models.PasswordResetOtp;
import me.electronicsboy.titly.models.User;
import me.electronicsboy.titly.repositories.PasswordResetOtpRepository;
import me.electronicsboy.titly.repositories.UserRepository;

@Service
public class PasswordResetService {

	@Autowired
    private UserRepository userRepository;
	@Autowired
    private UserAuthenticationService userAuthService;

    @Autowired
    private PasswordResetOtpRepository otpRepository;

    @Autowired
    private EmailService emailSender;

    public void generateOTP(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        String otp = String.valueOf((int)(Math.random() * 900000) + 100000); // 6-digit

        PasswordResetOtp otpEntity = new PasswordResetOtp(otp, email, LocalDateTime.now().plusMinutes(10));

        otpRepository.save(otpEntity);
        
        emailSender.sendSimpleEmail(email, "Password Reset OTP", EmailService.RESETPASSWORD_BODY.formatted(user.getFullname(), otp));
    }
    
    public User validateOTP(String email, String otp, String newPassword) {
        Optional<PasswordResetOtp> otpOptional = otpRepository.findByEmailAndOtp(email, otp);
        if (otpOptional.isEmpty())
        	throw new InvalidOtpException("OTP is invalid!");
        
        PasswordResetOtp resetOtp = otpOptional.get();

        if (resetOtp.getExpiryDate().isBefore(LocalDateTime.now()))
        	throw new InvalidOtpException("OTP has expired!");

        User user = userRepository.findByEmail(email).orElseThrow();
        user.setPassword(userAuthService.getEncodedPassword(newPassword));
        userRepository.save(user);

        otpRepository.delete(resetOtp);
        return user;
    }
}
