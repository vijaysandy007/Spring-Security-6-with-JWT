package com.example.security.service;

import com.example.security.dtos.LoginDto;
import com.example.security.dtos.LoginResponse;
import com.example.security.dtos.RegisterDto;
import com.example.security.dtos.RegisterResponse;
import com.example.security.model.Roles;
import com.example.security.model.User;
import com.example.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public RegisterResponse registerDto(RegisterDto request){
        var user = User.builder()
                .firstName(request.getFirstname())
                .lastName(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Roles.ROLE_USER)
                .build();
        // Log user details
        System.out.println("User details before saving: " + user);
         userRepository.save(user);

         var response = RegisterResponse.builder()
                 .data(user)
                 .message("Successfully register")
                 .status(200)
                 .success(true)
                 .build();
         return response;
    }

    public LoginResponse login(LoginDto request){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(()-> new UsernameNotFoundException("User Not Found"));

        var jwtToken = jwtService.generateToken(user);
        return LoginResponse.builder()
                .message("Successfully Login")
                .token(jwtToken)
                .status(200)
                .success(true)
                .build();
    }
}
