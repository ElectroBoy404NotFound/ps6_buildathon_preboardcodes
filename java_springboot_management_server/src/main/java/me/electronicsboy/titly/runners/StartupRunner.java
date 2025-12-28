package me.electronicsboy.titly.runners;

import java.security.SecureRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import me.electronicsboy.titly.data.PrivilegeLevel;
import me.electronicsboy.titly.dtos.RegisterUserDto;
import me.electronicsboy.titly.models.User;
import me.electronicsboy.titly.repositories.UserRepository;
import me.electronicsboy.titly.services.EmailService;
import me.electronicsboy.titly.services.UserAuthenticationService;

@Component
public class StartupRunner implements CommandLineRunner {
	private static final Logger LOGGER = LoggerFactory.getLogger(UserAuthenticationService.class);
	
	@Autowired
    private UserRepository userRepository;
	@Autowired
    private UserAuthenticationService authService;
	@Autowired
	private EmailService emailService;
	
	private final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%&*!";
    private final int PASSWORD_LENGTH = 12; // Change as needed
    private final SecureRandom RANDOM = new SecureRandom();

    @Value("${security.adminemail}")
    private String adminEmail;
    
    private String generateSecurePassword() {
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            password.append(CHARACTERS.charAt(index));
        }
        return password.toString();
    }

    private void makeAdminUser() {
		RegisterUserDto rud = new RegisterUserDto();
		rud.setFullname("Super Admin");
		rud.setUsername("chmscmsadminuser");
		rud.setEmail(adminEmail);
		rud.setLocation("None");
//		rud.setPrivilegeLevel(PrivilegeLevel.CMSADMIN);
		
		String password = generateSecurePassword();
		
		rud.setPassword(password);
        User newuser = authService.signup(rud);
        newuser.setPrivilageLevel(PrivilegeLevel.CMSADMIN);
        newuser.setEnabled(true);
        userRepository.save(newuser);
        
        emailService.sendSimpleEmail(adminEmail, "Account Created", "Hi %s,\r\nAn account with username <b>'%s'</b> and password <b>'%s'</b> has been created with <b>%s</b> privilages.\r\nUse this info to login.".formatted(newuser.getFullname(), newuser.getUsername(), password, newuser.getPrivilegeLevel().toString()));
        
        LOGGER.info("Created user 'chmscmsadminuser' with email '%s' and with password '%s'".formatted(adminEmail, password));
	}
    
	@Override
	public void run(String... args) throws Exception {	
		boolean admin = false;
		for(User u : userRepository.findAll()) {
    		if(u.getPrivilegeLevel() == PrivilegeLevel.CMSADMIN) {
    			LOGGER.info("A CMSADMIN (%s) already exists, not creating a new user...".formatted(u.getUsername()));
    			admin = true;
    			break;
    		}
    	}
		
		if(!admin)
			makeAdminUser();
    }
}
