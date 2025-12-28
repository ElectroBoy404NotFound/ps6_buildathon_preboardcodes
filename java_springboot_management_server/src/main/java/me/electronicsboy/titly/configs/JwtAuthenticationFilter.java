package me.electronicsboy.titly.configs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.electronicsboy.titly.data.DeviceType;
import me.electronicsboy.titly.exceptions.InvalidJWTException;
import me.electronicsboy.titly.models.User;
import me.electronicsboy.titly.services.InvalidatedJWTService;
import me.electronicsboy.titly.services.JwtService;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final InvalidatedJWTService jwtInvalidationService;

    public JwtAuthenticationFilter(
        JwtService jwtService,
        UserDetailsService userDetailsService,
        HandlerExceptionResolver handlerExceptionResolver,
        InvalidatedJWTService jwtInvalidationService
    ) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.handlerExceptionResolver = handlerExceptionResolver;
        this.jwtInvalidationService = jwtInvalidationService;
    }

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
		String path = request.getRequestURI();
		
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String jwt = authHeader.substring(7);
            final String userName = jwtService.extractUsername(jwt);

            if(jwtInvalidationService.isTokenInvalidated(jwt)) {
            	throw new InvalidJWTException("The JWT has been invalidated!");
            }
            
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (userName != null && authentication == null) {
                if (jwtService.isTokenValid(jwt)) {
                	if(jwtService.getDeviceType(jwt) == DeviceType.DEVICE || jwtService.getDeviceType(jwt) == DeviceType.PREAUTH_DEVICE) {
                		if(!path.startsWith("/device/"))
                			throw new AccessDeniedException("Devices can access only device endpoints!");
                		
                		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                new UserDetails() {
									private static final long serialVersionUID = 2866442390994513418L;

									@Override
									public String getUsername() {
										return userName;
									}
									
									@Override
									public String getPassword() {
										return jwt;
									}
									
									@Override
									public Collection<? extends GrantedAuthority> getAuthorities() {
										return new ArrayList<>();
									}
								},
                                null,
                                new ArrayList<>()
                        );

                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                	}
                	if(jwtService.getDeviceType(jwt) == DeviceType.USER) {
                		if(path.startsWith("/device/"))
                			throw new AccessDeniedException("Users cannot access device endpoints!");
                		
                		UserDetails userDetails = this.userDetailsService.loadUserByUsername(userName);
                		
                		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        if(!((User) authToken.getPrincipal()).isEnabled())
                        	throw new DisabledException("User account is disabled");
                	}
                }
            }

            filterChain.doFilter(request, response);
        } catch (Exception exception) {
            handlerExceptionResolver.resolveException(request, response, null, exception);
        }
    }
}
