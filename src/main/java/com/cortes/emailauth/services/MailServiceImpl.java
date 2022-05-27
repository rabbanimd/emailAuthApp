package com.cortes.emailauth.services;

import com.cortes.emailauth.models.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

@Service
@AllArgsConstructor
@Slf4j
public class MailServiceImpl implements MailService{
    private static final String SENDER_EMAIL = "waviangroup@gmail.com";
    private static final String SENDER_NAME = "cortes";
    private static final String SUBJECT = "Activate your account";
    private static final String VERIFICATION_URL = "http://localhost:8080/api/auth/verify/[token]";
    private static final String CONTENT = "Dear [name],\n" +
            "Please click the link below to activate your account\n" +
            "[url] \n" +
            "Thank you\n" +
            "Wavian Group";


    private final JavaMailSender mailSender;

    @Async
    public void sendMail(User user, String token) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        helper.setFrom(SENDER_EMAIL, SENDER_NAME);
        helper.setTo(user.getEmail());
        helper.setSubject(SUBJECT);
        String content = CONTENT.replace("[name]", user.getUsername());
        content = content.replace("[url]", VERIFICATION_URL);
        content = content.replace("[token]", token);
        helper.setText(content);
        try{
            mailSender.send(message);
            log.info("Verfication mail sent to : "+user.getEmail());

        }catch (Exception e)
        {
            log.error("Something went wrong while sending mail to : "+user.getEmail());
        }

    }
}
