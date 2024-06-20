package com.mukund.booknetwork.auth;

import com.mukund.booknetwork.role.RoleRepository;
import com.mukund.booknetwork.security.JwtService;
import com.mukund.booknetwork.user.Token;
import com.mukund.booknetwork.user.TokenRepository;
import com.mukund.booknetwork.user.User;
import com.mukund.booknetwork.user.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;


@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;
    private final JwtService jwtService;
    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;
    private final AuthenticationManager authenticationManager;

    public void register(RegistrationRequest request) throws MessagingException {
        var userRole=roleRepository.findByName("USER")
                .orElseThrow(()->new IllegalStateException("Role USER was not initialised"));
        var user= User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .accountLocked(false)
                .enabled(false)
                .roles(List.of(userRole))

                .build();
        userRepository.save(user);
        sendValidationEmail(user);
              
        
    }

    private void sendValidationEmail(User user) throws MessagingException {
        
        var newTokenOrActivationCode= generateAndSaveActivationToken(user);
//        send email
emailService.sendEmail(
        user.getEmail(),
        user.fullName(),
        EmailTemplateName.ACTIVATE_ACCOUNT,
        activationUrl,
        newTokenOrActivationCode,
        "Account Activation"

);


    }

    private String generateAndSaveActivationToken(User user) {
        String generatedActivationCode=generateActivationCode(6);
        var token= Token.builder()
                .token(generatedActivationCode)
                .createdAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusHours(8))
                .user(user)
                .build();
        tokenRepository.save(token);
        return generatedActivationCode;
    }

    private String generateActivationCode(int len) {
        String characters="0123456789";
        StringBuilder codeBuilder=new StringBuilder();
        SecureRandom secureRandom=new SecureRandom();
        for(int i=0;i<len;i++){
            int randomIndex=secureRandom.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));

        }
        return codeBuilder.toString();
    }




    public AuthenticationResponse authenticate(AuthenticationRequest request) {
var auth=authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword())
);

var claims=new HashMap<String,Object>();
var user=((User)auth.getPrincipal());
claims.put("fullName",user.fullName());
var jwtToken=jwtService.generateToken(claims,user);
return AuthenticationResponse.builder().token(jwtToken).build();

    }



//    @Transactional
    public void activateAccount(String token) throws MessagingException {

Token savedToken=tokenRepository.findByToken(token)
        .orElseThrow(()->new RuntimeException("Invalid token ,exception Occured in activate_account_AuthenticationService"));

if(LocalDateTime.now().isAfter(savedToken.getExpiredAt())){
    sendValidationEmail(savedToken.getUser());
    throw new RuntimeException("Activation Token has Expired. A new Token has been sent to the same email address");
}

var user=userRepository.findById(savedToken.getUser().getId())
        .orElseThrow(()->new UsernameNotFoundException("User not found Exception in activate_account_AuthenticateionService"));


user.setEnabled(true);
userRepository.save(user);
savedToken.setValidatedAt(LocalDateTime.now());
tokenRepository.save(savedToken);
    }
}
