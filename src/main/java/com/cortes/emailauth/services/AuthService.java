package com.cortes.emailauth.services;

import com.cortes.emailauth.dto.RegisterRequest;

public interface AuthService {
    public Boolean isAlreadyRegistered(RegisterRequest registerRequest);
    public void signup(RegisterRequest registerRequest);
    public Boolean validateToken(String token);
}
