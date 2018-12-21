package ru.javaops.masterjava.service.mail;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import ru.javaops.masterjava.config.Configs;

import java.util.List;

@Slf4j
public class MailSender {
    static void sendMail(List<Addressee> to, List<Addressee> cc, String subject, String body) {
        log.info("Send mail to \'" + to + "\' cc \'" + cc + "\' subject \'" + subject + (log.isDebugEnabled() ? "\nbody=" + body : ""));
        HtmlEmail email = new HtmlEmail();
        email.setHostName("smtp.yandex.ru");
        email.setSmtpPort(465);
        email.setAuthentication("Andrunov@yandex.ru", "Andro5775382");
        email.setSSL(true);
        email.setTLS(false);
        email.setDebug(true);
        try {
            email.setFrom("Andrunov@yandex.ru", "Andro");
            for (Addressee addressee : to) {
                email.addTo(addressee.getEmail(), addressee.getName());
            }
            for (Addressee addressee : cc) {
                email.addCc(addressee.getEmail(), addressee.getName());
            }
            email.send();

        } catch (EmailException e) {
            e.printStackTrace();
        }

    }
}
