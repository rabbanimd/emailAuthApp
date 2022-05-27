package com.cortes.emailauth.services;

import com.cortes.emailauth.dto.RegisterRequest;
import com.cortes.emailauth.exceptions.AuthException;
import com.cortes.emailauth.models.User;
import com.cortes.emailauth.models.VerificationToken;
import com.cortes.emailauth.repos.UserRepository;
import com.cortes.emailauth.repos.VerificationTokenRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import javax.validation.constraints.Null;
import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService{
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;

    private static final Integer EXPIRE_IN = 30;


    @Transactional
    @Override
    public void signup(RegisterRequest registerRequest) {
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setAuthenticated(false);

//        generate verification token and send mail
        String token = generateVerificationToken();

        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpireAt(getExpireInstant());
        user.setVerificationToken(verificationToken);
        verificationTokenRepository.save(verificationToken);
        userRepository.save(user);
        try{
            mailService.sendMail(user, token);
        }catch (Exception e){
            log.info("Cannot send email to "+user.getEmail());
        }
    }

    private Instant getExpireInstant() {
        return Instant.now().plus(EXPIRE_IN, ChronoUnit.MINUTES);
    }

    private String generateVerificationToken() {
        return UUID.randomUUID().toString();
    }

    @Transactional
    @Override
    public Boolean validateToken(String token) {
        VerificationToken verificationToken = new VerificationToken();
        try{
            verificationToken = verificationTokenRepository.findByToken(token);
            if(isTokenExpired(verificationToken.getExpireAt()))
            {
                return false;
            }
            User user = new User();
            user = verificationToken.getUser();
            user.setAuthenticated(true);
            userRepository.save(user);
        }catch (Exception e){
            log.info("Token not found "+token);
            return false;
        }
        return true;
    }


    @Override
    public void resendToken(RegisterRequest registerRequest)  {
        /*
            two cases
            1 : verification token is expired
            2 : verification is not expired
         */
        User user = userRepository.findByEmail(registerRequest.getEmail());
        VerificationToken verificationToken = user.getVerificationToken();
        if(isTokenExpired(verificationToken.getExpireAt()))
        {
//            generate new token

            verificationToken.setToken(generateVerificationToken());
            verificationToken.setExpireAt(this.getExpireInstant());
            verificationTokenRepository.save(verificationToken);
        }
        try{
            mailService.sendMail(user, verificationToken.getToken());
        }catch (Exception e){
            log.info("Cannot send email to "+user.getEmail());
        }
    }

    @Override
    public Boolean isEmailRegistered(String email) {
        User user = userRepository.findByEmail(email);
        if(user==null){
            return false;
        }
        return true;
    }

    @Override
    public boolean isAlreadyVerified(RegisterRequest registerRequest) {
        User user = userRepository.findByEmail(registerRequest.getEmail());
        return user.getAuthenticated();
    }


    private boolean isTokenExpired(Instant expireAt) {
        Instant instant = Instant.now();
        if(instant.compareTo(expireAt)>0)
        {
//            token is expired
            return true;
        }
        return false;
    }
}
