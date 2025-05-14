package com.enterprise.auth.service;

import com.enterprise.auth.dto.AuthResponse;
import com.enterprise.auth.dto.LoginRequest;
import com.enterprise.auth.dto.RegisterRequest;
import com.enterprise.auth.entity.Role;
import com.enterprise.auth.entity.User;
import com.enterprise.auth.repository.RoleRepository;
import com.enterprise.auth.repository.UserRepository;
import com.enterprise.auth.security.JwtTokenProvider;
import com.enterprise.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("Username is already taken");
        }
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email is already in use");
        }
        
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new BusinessException("Default user role not found"));
        
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .roles(roles)
                .build();
        
        userRepository.save(user);
        
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);
        
        List<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());
        
        return AuthResponse.builder()
                .token(jwt)
                .tokenType("Bearer")
                .username(user.getUsername())
                .roles(roleNames)
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);
        
        User userDetails = (User) authentication.getPrincipal();
        
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.startsWith("ROLE_"))
                .map(authority -> authority.substring(5))
                .collect(Collectors.toList());
        
        return AuthResponse.builder()
                .token(jwt)
                .tokenType("Bearer")
                .username(userDetails.getUsername())
                .roles(roles)
                .build();
    }
}