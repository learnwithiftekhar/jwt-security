package com.learnwithiftekhar.jwtsecurity.service;

import com.learnwithiftekhar.jwtsecurity.dto.JwtResponse;
import com.learnwithiftekhar.jwtsecurity.dto.LoginRequest;
import com.learnwithiftekhar.jwtsecurity.dto.RegisterRequest;
import com.learnwithiftekhar.jwtsecurity.dto.TokenPair;
import com.learnwithiftekhar.jwtsecurity.jwt.JwtProvider;
import com.learnwithiftekhar.jwtsecurity.model.User;
import com.learnwithiftekhar.jwtsecurity.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, UserDetailsService userDetailsService, PasswordEncoder passwordEncoder, JwtProvider jwtProvider, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public void registerUser(RegisterRequest registerRequest) {
        // check if username already exist
        if(userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new IllegalArgumentException("Username is already in use");
        }

        // crate a new user
        User user = User
                .builder()
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(registerRequest.getRole())
                .build();

        userRepository.save(user);
    }

    @Transactional
    public JwtResponse login(LoginRequest loginRequest) {
        // Authenticate the user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        // set authentication in security context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate JWT token pair
        TokenPair tokenPair = jwtProvider.generateTokenPair(authentication);

        return new JwtResponse(
                tokenPair.getAccessToken(),
                tokenPair.getRefreshToken()
        );
    }

    public JwtResponse refreshToken(String refreshToken) {
        // verify if it is refresh token
        if(jwtProvider.isRefreshToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        // extract username from refresh token
        String username = jwtProvider.extractUsernameFromToken(refreshToken);

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if(userDetails == null) {
            throw new IllegalArgumentException("User not found");
        }

        // Create new authentication object
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        String newAccessToken = jwtProvider.generateAccessToken(authentication);

        return new JwtResponse(
                newAccessToken,
                refreshToken
        );
    }
}
