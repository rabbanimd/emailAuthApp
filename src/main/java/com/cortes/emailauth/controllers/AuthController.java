package com.cortes.emailauth.controllers;

import com.cortes.emailauth.dto.RegisterRequest;
import com.cortes.emailauth.services.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/auth")
@RestController
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity signup(@RequestBody RegisterRequest registerRequest){
//        if user already exist
        if(authService.isAlreadyRegistered(registerRequest)){
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
}
