package me.electronicsboy.titly.services;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import me.electronicsboy.titly.models.RefreshToken;
import me.electronicsboy.titly.repositories.RefreshTokenRepository;

@Service
public class RefreshTokenService {

    @Value("${security.refresh-token.expiration}")
    private long refreshTokenDurationMs;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    public RefreshToken createRefreshToken(String username) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUsername(username);
        refreshToken.setToken(UUID.randomUUID().toString() + "-" + UUID.randomUUID().toString() + "-" + UUID.randomUUID().toString() + "-" + UUID.randomUUID().toString() + "-" + UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> verifyToken(String token) {
        return refreshTokenRepository.findByToken(token)
            .filter(rt -> rt.getExpiryDate().isAfter(Instant.now()));
    }

//    @Transactional
//    public void deleteByUsername(String username) {
//        refreshTokenRepository.deleteByUsername(username);
//    }
    
    @Transactional
    public void deleteByToken(String token) {
        refreshTokenRepository.delete(refreshTokenRepository.findByToken(token).orElseThrow());
    }
}