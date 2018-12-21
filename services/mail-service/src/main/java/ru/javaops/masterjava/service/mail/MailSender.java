package ru.javaops.masterjava.service.mail;

import com.typesafe.config.Config;
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
        Config config = Configs.getConfig("mail.conf", "mail");
        email.setHostName(config.getString("host"));
        email.setSmtpPort(config.getInt("port"));
        email.setAuthentication(config.getString("username"), config.getString("password"));
        email.setSSL(config.getBoolean("useSSL"));
        email.setTLS(config.getBoolean("useTLS"));
        email.setDebug(config.getBoolean("debug"));
        try {
            email.setFrom(config.getString("username"), config.getString("fromName"));
            for (Addressee addressee : to) {
                email.addTo(addressee.getEmail(), addressee.getName());
            }
            for (Addressee addressee : cc) {
                email.addCc(addressee.getEmail(), addressee.getName());
            }
            email.setSubject(subject);
            email.setHtmlMsg(body);
            email.send();

        } catch (EmailException e) {
            e.printStackTrace();
        }

    }
}
