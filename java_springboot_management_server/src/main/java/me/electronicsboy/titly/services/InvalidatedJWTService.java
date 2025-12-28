package me.electronicsboy.titly.services;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import me.electronicsboy.titly.models.InvalidatedJWT;
import me.electronicsboy.titly.repositories.InvalidatedJWTRepository;

@Service
public class InvalidatedJWTService {
	@Autowired
	private InvalidatedJWTRepository invalidJwtRepo;
	@Autowired
	private JwtService jwtService;

	public void invalidateJwtToken(String jwt) {
		if(invalidJwtRepo.existsByToken(jwt))
			return;
		
		invalidJwtRepo.save(new InvalidatedJWT(0, jwt, jwtService.extractExpiration(jwt)));
	}
	
	public boolean isTokenInvalidated(String jwt) {
		return invalidJwtRepo.existsByToken(jwt);
	}
	
	@Scheduled(fixedRate = 3600000) 
    public void deleteExpiredEntries() {
		invalidJwtRepo.deleteByExpiryTimeBefore(LocalDateTime.now());
    }
}
