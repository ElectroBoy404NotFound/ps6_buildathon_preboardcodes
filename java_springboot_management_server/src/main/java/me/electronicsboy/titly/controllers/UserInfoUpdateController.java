package me.electronicsboy.titly.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import me.electronicsboy.titly.data.PrivilegeLevel;
import me.electronicsboy.titly.dtos.UpdateUserInfoDto;
import me.electronicsboy.titly.exceptions.InvalidRequestException;
import me.electronicsboy.titly.exceptions.UnprivilagedExpection;
import me.electronicsboy.titly.models.User;
import me.electronicsboy.titly.repositories.UserRepository;
import me.electronicsboy.titly.services.UserAuthenticationService;

@RequestMapping("/update/user")
@RestController
public class UserInfoUpdateController {
	@Autowired
    private UserAuthenticationService authService;
	@Autowired
	private UserRepository userRepository;

	@PostMapping("/update")
	public ResponseEntity<User> updateUserInfo(@RequestBody UpdateUserInfoDto info) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User doer = (User) authentication.getPrincipal();
		if(doer.getPrivilegeLevel().compareTo(PrivilegeLevel.ADMIN) <= 0)
			throw new UnprivilagedExpection("You aren't privilaged enough to do this!");
		
		if(info.getPrivilageLevel() != null) {
			doer.setPrivilageLevel(info.getPrivilageLevel());
		}
		if(info.getEmail() != null) {
			doer.setEmail(info.getEmail());
		}
		if(info.getPassword() != null) {
			doer.setPassword(authService.getEncodedPassword(info.getPassword()));
		}
		if(info.getFullname() != null) {
			doer.setFullname(info.getFullname());
		}
		if(info.getUsername() != null) {
			if(userRepository.findByUsername(info.getUsername()) == null)
				doer.setUsername(info.getUsername());
			else throw new InvalidRequestException("An user with same username already exists!");
		}
		
		userRepository.save(doer);
		
		return ResponseEntity.ok(doer);
	}
}
