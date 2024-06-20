package com.mukund.booknetwork.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
//@Data
//@RequiredArgsConstructor
//@AllArgsConstructor
//@NoArgsConstructor
public class JwtService {
    @Value("${application.security.jwt.expiration}")
   private long jwtExpiration;
    @Value("${application.security.jwt.secret-key}")
    private String secretKey;



//    generate token
    public String generateToken(UserDetails userDetails){
        var token=generateToken(new HashMap<>(),userDetails);
        System.out.println("token generated");
        System.out.println(token);
        return  token;
    }
    public String generateToken(Map<String,Object>extraClaims, UserDetails userDetails){

        return buildToken(extraClaims,userDetails,jwtExpiration);
    }


    private String buildToken(Map<String, Object> extraClaims,
                              UserDetails userDetails,
                              long jwtExpiration) {
        var authorities=userDetails.getAuthorities()
                        .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        System.out.println("Authorities in Jwtservice_build_token method");
        System.out.println(authorities);
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+jwtExpiration))
                .claim("authorities",authorities)
                .signWith(getSignInKey())
                .compact();




    }

    private Key getSignInKey() {
        byte[] keyBytes= Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }



//    validate token

    

    public boolean isTokenValid(String token,UserDetails userDetails){
        final String username=extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));


    }


    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());

    }

    private Date extractExpiration(String token) {
        return extractClaim(token,Claims::getExpiration);
    }

    public String extractUsername(String token) {
      return extractClaim(token, Claims::getSubject);

    }

    public <T> T extractClaim(String token, Function<Claims,T>claimResolver) {
        final Claims claims=extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

    }
}
