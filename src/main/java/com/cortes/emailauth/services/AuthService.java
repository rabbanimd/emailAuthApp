package com.cortes.emailauth.services;

import com.cortes.emailauth.dto.RegisterRequest;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

public interface AuthService {
    public void signup(RegisterRequest registerRequest);
    public Boolean validateToken(String token);


    void resendToken(RegisterRequest registerRequest) throws MessagingException, UnsupportedEncodingException;

    Boolean isEmailRegistered(String email);

    boolean isAlreadyVerified(RegisterRequest registerRequest);
}
