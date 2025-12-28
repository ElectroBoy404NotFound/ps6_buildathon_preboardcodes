package me.electronicsboy.titly.controllers.device;

import java.util.Collection;
import java.util.HashMap;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import me.electronicsboy.titly.data.DeviceType;
import me.electronicsboy.titly.responses.LoginResponse;
import me.electronicsboy.titly.services.JwtService;

@RequestMapping("/deviceauth")
@RestController
public class DeviceAuthenticationController {
	private final JwtService jwtService;

    public DeviceAuthenticationController(JwtService jwtService) {
        this.jwtService = jwtService;
    }
        
    @GetMapping("/temporaryLogin")
    public ResponseEntity<LoginResponse> authenticateTemporaryLogin() {
        String jwtToken = jwtService.buildDeviceToken(new HashMap<String, Object>(), new UserDetails() {
			private static final long serialVersionUID = 1259883989161155108L;
			
			@Override
			public String getUsername() {
				return "preauth_device_token";
			}
			
			public String getPassword() { return null; }
			public Collection<? extends GrantedAuthority> getAuthorities() { return null; }
		}, 900000, DeviceType.PREAUTH_DEVICE);

        LoginResponse loginResponse = new LoginResponse().setToken(jwtToken).setExpiresIn(jwtService.getExpirationTime());

        return ResponseEntity.ok(loginResponse);
    }
}
