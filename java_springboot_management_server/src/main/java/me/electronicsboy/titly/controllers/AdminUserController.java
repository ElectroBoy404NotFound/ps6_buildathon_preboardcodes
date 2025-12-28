package me.electronicsboy.titly.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import me.electronicsboy.titly.data.PrivilegeLevel;
import me.electronicsboy.titly.dtos.UpdateUserInfoDto;
import me.electronicsboy.titly.exceptions.InvalidActionException;
import me.electronicsboy.titly.exceptions.InvalidRequestException;
import me.electronicsboy.titly.exceptions.UnprivilagedExpection;
import me.electronicsboy.titly.exceptions.UserNotFoundException;
import me.electronicsboy.titly.models.User;
import me.electronicsboy.titly.repositories.UserRepository;
import me.electronicsboy.titly.services.EmailService;
import me.electronicsboy.titly.services.UserAuthenticationService;
import me.electronicsboy.titly.services.UserService;

@RestController
@RequestMapping("/admin/users")
public class AdminUserController {
	@Autowired
	private UserRepository userRepo;
	@Autowired
	private EmailService emailService;
	@Autowired
	private UserService userService;
	@Autowired
    private UserAuthenticationService authService;
	
	@GetMapping("/list")
    public ResponseEntity<List<User>> allUsers() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User currentUser = (User) authentication.getPrincipal();
		if(currentUser.getPrivilegeLevel().ordinal() <= PrivilegeLevel.GUEST.ordinal())
			throw new UnprivilagedExpection("You aren't privilaged enough to do this!");
		
		List <User> users = userService.allUsers();
		return ResponseEntity.ok(users);
    }
	
	@GetMapping("/getUnapprovedUsers")
	public ResponseEntity<List<User>> getUsersUnapproved() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User currentUser = (User) authentication.getPrincipal();
		if(currentUser.getPrivilegeLevel().ordinal() <= PrivilegeLevel.GUEST.ordinal())
			throw new UnprivilagedExpection("You aren't privilaged enough to do this!");
		
		List<User> users = userRepo.findAllByEnabled(false);
		
		return ResponseEntity.ok(users);
	}
	
	@GetMapping("/approveUser/{id}")
	public ResponseEntity<User> approveBooking(@PathVariable long id) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User currentUser = (User) authentication.getPrincipal();
		if(currentUser.getPrivilegeLevel().ordinal() <= PrivilegeLevel.GUEST.ordinal())
			throw new UnprivilagedExpection("You aren't privilaged enough to do this!");
		
		if(!userRepo.existsById(id))
			throw new UserNotFoundException("User with id %d not found!".formatted(id));
			
		User user = userRepo.findById(id).get();
		user.setEnabled(true);
		userRepo.save(user);
		
		emailService.sendSimpleEmail(user.getEmail(), "User %s approved!".formatted(user.getUsername()), EmailService.USERAPPROVED_BODY.formatted(user.getFullname()));
		
		return ResponseEntity.ok(user);
	}
	
	@PostMapping("/rejectUser/{id}")
	public ResponseEntity<User> rejectBooking(@PathVariable long id, @RequestBody String reason) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User currentUser = (User) authentication.getPrincipal();
		if(currentUser.getPrivilegeLevel().ordinal() <= PrivilegeLevel.GUEST.ordinal())
			throw new UnprivilagedExpection("You aren't privilaged enough to do this!");
		
		if(!userRepo.existsById(id))
			throw new UserNotFoundException("User with id %d not found!".formatted(id));
			
		User user = userRepo.findById(id).get();
		userRepo.delete(user);
		
		emailService.sendSimpleEmail(user.getEmail(), "User %s has been rejected!".formatted(user.getUsername()), EmailService.USERREJECTED_BODY.formatted(user.getFullname()));
		
		return ResponseEntity.ok(user);
	}
	
	@PostMapping("/update")
	public ResponseEntity<User> updateUserInfo(@RequestBody UpdateUserInfoDto info) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User doer = (User) authentication.getPrincipal();
		if(doer.getPrivilegeLevel().compareTo(PrivilegeLevel.ADMIN) <= 0)
			throw new UnprivilagedExpection("You aren't privilaged enough to do this!");
		
		if(!userRepo.existsById(info.getUserid()))
			throw new UserNotFoundException("No user with id %d exists!".formatted(info.getUserid()));
		
		User actor = userRepo.findById(info.getUserid()).get();
		
		if(actor.getUsername().equals("guest"))
			throw new InvalidActionException("You can't modify or delete a default guest user!");
		
		if(info.getPrivilageLevel() != null) {
			actor.setPrivilageLevel(info.getPrivilageLevel());
		}
		if(info.getEmail() != null && !info.getEmail().equals(actor.getEmail())) {
//			actor.setEmail(info.getEmail());
			if(userRepo.findByEmail(info.getEmail()) == null)
				actor.setEmail(info.getEmail());
			else throw new InvalidRequestException("An user with same email already exists!");
		}
		if(info.getPassword() != null) {
			actor.setPassword(authService.getEncodedPassword(info.getPassword()));
		}
		if(info.getFullname() != null) {
			actor.setFullname(info.getFullname());
		}
		if(info.getUsername() != null && !info.getUsername().equals(actor.getUsername())) {
			if(userRepo.findByUsername(info.getUsername()) == null)
				actor.setUsername(info.getUsername());
			else throw new InvalidRequestException("An user with same username already exists!");
		}
		
		userRepo.save(actor);
		
		return ResponseEntity.ok(actor);
	}
	
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<User> deleteUser(@PathVariable long id, @RequestBody String reason) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User currentUser = (User) authentication.getPrincipal();
		if(currentUser.getPrivilegeLevel().compareTo(PrivilegeLevel.ADMIN) <= 0)
			throw new UnprivilagedExpection("You aren't privilaged enough to do this!");
		
		if(!userRepo.existsById(id))
			throw new UserNotFoundException("No user with id %d exists!".formatted(id));
		
		User actor = userRepo.findById(id).get();
		
		if(actor.getUsername().equals("guest"))
			throw new InvalidActionException("You can't modify or delete a default guest user!");
		
		if(currentUser.getId() == actor.getId())
			throw new InvalidRequestException("You can't delete yourself!");
		
		userRepo.delete(actor);
		
		emailService.sendSimpleEmail(actor.getEmail(), "Your account has been deleted.", EmailService.USERDELETED_BODY.formatted(actor.getFullname(), actor.getUsername(), actor.getId(), reason));
		
		return ResponseEntity.ok(actor);
	}
}
