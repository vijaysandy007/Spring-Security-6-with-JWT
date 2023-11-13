package com.example.security.config;

import com.example.security.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {


        final String authHeader = request.getHeader("Authorization");
        System.out.println("authHeader============== " + authHeader);
        final String jwt;
        final String userEmail;

        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request,response);
            return;
        }
        jwt = authHeader.substring(7);
        System.out.println("Jwt======================1 " + jwt);
        userEmail = jwtService.extractUsername(jwt);
        System.out.println("userEmail======================1.2 " + userEmail);

        if(authHeader !=null && SecurityContextHolder.getContext().getAuthentication() ==null){
            System.out.println("1==============");

            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            System.out.println("2============== " + userDetails);
            if(jwtService.isTokenValid(jwt,userDetails)){
                System.out.println("3==============");
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails.getUsername(),
                        null,
                        userDetails.getAuthorities()
                );

                System.out.println("4 ==============");

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                System.out.println("5 ==============");
                SecurityContextHolder.getContext().setAuthentication(authToken);
                System.out.println("6 ==============");

            }
            System.out.println("7 ==============");
            filterChain.doFilter(request,response);
            System.out.println("8 ==============");

        }
    }
}
