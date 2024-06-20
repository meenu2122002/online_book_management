package com.mukund.booknetwork.security;

import com.mukund.booknetwork.user.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

private final JwtService jwtService;
private final UserDetailsService userDetailsService;
    @Override
    protected void doFilterInternal(
         @NonNull HttpServletRequest request,
         @NonNull      HttpServletResponse response,
         @NonNull    FilterChain filterChain)
            throws ServletException, IOException {




        System.out.println("I am in JwtFilter OncePerRequestFilter");
        System.out.println(request.getServletPath());
        System.out.println("Meenu");




        if(request.getServletPath().contains("/api/v1/auth")){
    filterChain.doFilter(request,response);

    return;
}


        final  String authHeader=request.getHeader(HttpHeaders.AUTHORIZATION);
        final String token;
        final String userEmail;
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            filterChain.doFilter(request,response);
            return;
        }

        token=authHeader.substring(7);
        userEmail =jwtService.extractUsername(token);
        if(userEmail!=null && SecurityContextHolder.getContext().getAuthentication()==null){
            UserDetails userDetails= userDetailsService.loadUserByUsername(userEmail);
            if(jwtService.isTokenValid(token,userDetails)){
                UsernamePasswordAuthenticationToken authentication=new UsernamePasswordAuthenticationToken(
                        userDetails,
null,
                userDetails.getAuthorities()
                );
                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
//                System.out.println("I am in JwtFilter outside ");
//                Authentication authentication1 = null;
                SecurityContextHolder.getContext().setAuthentication(authentication);
Authentication connectedUser =SecurityContextHolder.getContext().getAuthentication();
                System.out.println("connectedUser");
                User user=(User)connectedUser.getPrincipal();
                System.out.println(user.getEmail());
                System.out.println(connectedUser);


//                User user1=(User)authentication1.getPrincipal();
//                System.out.println("jwtFilter authentication1");
//                System.out.println(user1.getId());
            }

        }

filterChain.doFilter(request,response);


    }
}
