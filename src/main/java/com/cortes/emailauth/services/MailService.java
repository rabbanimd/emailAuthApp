package com.cortes.emailauth.services;

import com.cortes.emailauth.models.User;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

public interface MailService {
    public void sendMail(User user, String token) throws MessagingException, UnsupportedEncodingException;
}
