package me.electronicsboy.titly.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.jsonwebtoken.Claims;
import me.electronicsboy.titly.dtos.JWTInfoDto;
import me.electronicsboy.titly.dtos.LoginUserDto;
import me.electronicsboy.titly.dtos.LogoutDto;
import me.electronicsboy.titly.dtos.RefreshDto;
import me.electronicsboy.titly.dtos.RegisterUserDto;
import me.electronicsboy.titly.dtos.ResetPasswordDto;
import me.electronicsboy.titly.exceptions.InvalidRefreshTokenException;
import me.electronicsboy.titly.exceptions.LogoutJWTTokenMismatchException;
import me.electronicsboy.titly.exceptions.LogoutRefreshTokenMismatchException;
import me.electronicsboy.titly.models.FileObject;
import me.electronicsboy.titly.models.RefreshToken;
import me.electronicsboy.titly.models.User;
import me.electronicsboy.titly.repositories.FileObjectRepository;
import me.electronicsboy.titly.repositories.RefreshTokenRepository;
import me.electronicsboy.titly.repositories.UserRepository;
import me.electronicsboy.titly.responses.JWTInfoResponse;
import me.electronicsboy.titly.responses.LoginResponse;
import me.electronicsboy.titly.responses.LogoutResponse;
import me.electronicsboy.titly.responses.OkResponse;
import me.electronicsboy.titly.services.EmailService;
import me.electronicsboy.titly.services.InvalidatedJWTService;
import me.electronicsboy.titly.services.JwtService;
import me.electronicsboy.titly.services.PasswordResetService;
import me.electronicsboy.titly.services.RefreshTokenService;
import me.electronicsboy.titly.services.UserAuthenticationService;

@RequestMapping("/userauth")
@RestController
public class UserAuthenticationController {
	private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserAuthenticationService authenticationService;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordResetService otpService;
    private final InvalidatedJWTService jwtInvalidationService;
    
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    @Autowired
    private FileObjectRepository fileObjectRepository;

    public UserAuthenticationController(JwtService jwtService, UserAuthenticationService authenticationService, RefreshTokenService refreshTokenService, UserRepository userRepository, EmailService emailService, PasswordResetService otpService, InvalidatedJWTService jwtInvalidationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.refreshTokenService = refreshTokenService;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.otpService = otpService;
        this.jwtInvalidationService = jwtInvalidationService;
    }
    
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegisterUserDto registerUserDto) {
    	User registeredUser = authenticationService.signup(registerUserDto);
    	emailService.sendSimpleEmail(registerUserDto.getEmail(), "Registration complete!", EmailService.SIGNUP_BODY.formatted(registerUserDto.getFullname()));
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto) {
        User authenticatedUser = authenticationService.authenticate(loginUserDto);

        String jwtToken = jwtService.generateToken(authenticatedUser);
        String jwtRefreshToken = refreshTokenService.createRefreshToken(authenticatedUser.getUsername()).getToken();

        LoginResponse loginResponse = new LoginResponse().setToken(jwtToken).setExpiresIn(jwtService.getExpirationTime()).setRefreshToken(jwtRefreshToken);

        emailService.sendSimpleEmail(loginUserDto.getEmail(), "Someone has logged in into your account.", EmailService.LOGGEDIN_BODY.formatted(authenticatedUser.getFullname()));
        
        return ResponseEntity.ok(loginResponse);
    }
    
    @PostMapping("/jwtInfo")
    public ResponseEntity<JWTInfoResponse> checkJWTToken(@RequestBody JWTInfoDto jwtInfoDto) {
    	String token = jwtInfoDto.getToken();
        return ResponseEntity.ok(new JWTInfoResponse(token, jwtService.isTokenExpired(token), jwtService.extractUsername(token), jwtService.extractClaim(token, Claims::getIssuedAt).toString()));
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshDto request) {
        String refreshTokenStr = request.getRefreshToken();
        
        Optional<RefreshToken> optionalToken = refreshTokenService.verifyToken(refreshTokenStr);

        if (optionalToken.isPresent()) {
            RefreshToken rt = optionalToken.get();
            User user = userRepository.findByUsername(rt.getUsername()).orElseThrow();
            String newAccessToken = jwtService.generateToken(user);
            return ResponseEntity.ok(new LoginResponse().setToken(newAccessToken).setRefreshToken(null).setExpiresIn(jwtService.getExpirationTime()));
        }
        throw new InvalidRefreshTokenException("Invalid refresh token!");    
    }
    
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody LogoutDto logoutDto) {
    	RefreshToken refreshToken = refreshTokenRepository.findByToken(logoutDto.getRefreshToken()).orElseThrow();
    	String currentUserUsername = SecurityContextHolder.getContext().getAuthentication().getName();
    	
    	if(!refreshToken.getUsername().equals(currentUserUsername))
    		throw new LogoutRefreshTokenMismatchException("The refresh token doesn't belong to this user!");
    	if(!jwtService.extractUsername(logoutDto.getJwtToken()).equals(currentUserUsername))
    		throw new LogoutJWTTokenMismatchException("The JWT Token doesn't belong to this user!");
        
    	refreshTokenService.deleteByToken(refreshToken.getToken());
    	jwtInvalidationService.invalidateJwtToken(logoutDto.getJwtToken());
    	
        return ResponseEntity.ok(new LogoutResponse().setStatus(0));
    }
    
    @GetMapping("/resetPassword")
    public ResponseEntity<OkResponse> resetPasswordRequestOTP(@RequestParam String email) {
    	otpService.generateOTP(email);
        return ResponseEntity.ok(new OkResponse("OTP Sent to your email"));
    }
    
    @PostMapping("/resetPassword")
    public ResponseEntity<User> resetPassword(@RequestBody ResetPasswordDto resetPasswordDto) {
        return ResponseEntity.ok(otpService.validateOTP(resetPasswordDto.getEmail(), resetPasswordDto.getOtp(), resetPasswordDto.getNewpassword()));
    }
    
    @GetMapping("/donesubtitling")
    public ResponseEntity<OkResponse> donesubtitling(@PathVariable String hash) {
    	fileObjectRepository.findByHash(hash).orElseThrow().forEach((e) -> {
    		emailService.sendSimpleEmail(e.getUser().getEmail(), "File generated!", "your file %s has finished subtitling!".formatted(e.getFilename()));
    		e.setTranscribed(true);
    		fileObjectRepository.save(e);
    	});
        return ResponseEntity.ok(new OkResponse("Okie"));
    }
}
