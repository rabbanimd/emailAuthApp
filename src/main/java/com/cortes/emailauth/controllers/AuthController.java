package com.cortes.emailauth.controllers;

import com.cortes.emailauth.dto.RegisterRequest;
import com.cortes.emailauth.services.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

@RequestMapping("/api/auth")
@RestController
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity signup(@RequestBody RegisterRequest registerRequest){
//        if user already exist
        if(authService.isEmailRegistered(registerRequest.getEmail())){
            return new ResponseEntity("Email already registered !", HttpStatus.CONFLICT);
        }
        authService.signup(registerRequest);
        return new ResponseEntity("Registered Successfully, please activate your account", HttpStatus.OK);
    }

    @GetMapping("/verify/{token}")
    public ResponseEntity verifyToken(@PathVariable("token") String token)
    {
        if(authService.validateToken(token))
        {
            return new ResponseEntity("Account activated successfully", HttpStatus.OK);
        }
        return new ResponseEntity("Invalid token. Account cannot be activated !", HttpStatus.NOT_FOUND);
    }
    @PostMapping("/resendToken")
    public ResponseEntity resendActivationToken(@RequestBody RegisterRequest registerRequest) throws MessagingException, UnsupportedEncodingException {
        if(authService.isEmailRegistered(registerRequest.getEmail())==false)
        {
            return new ResponseEntity("email is not registered !", HttpStatus.NOT_FOUND);
        }
        if(authService.isAlreadyVerified(registerRequest)){
            return new ResponseEntity("Account is already verified !", HttpStatus.OK);
        }
        authService.resendToken(registerRequest);
        return new ResponseEntity("Email sent, please check your email", HttpStatus.OK);

    }
}
