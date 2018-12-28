package ru.javaops.masterjava.service.mail;

import com.google.common.io.Resources;
import lombok.extern.slf4j.Slf4j;
import ru.javaops.masterjava.web.WsClient;

import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.net.MalformedURLException;
import java.util.Set;

@Slf4j
public class MailWSClient {
    private static WsClient<MailService> WS_CLIENT;

    static {
        try {
            WS_CLIENT = new WsClient<MailService>( new File("/apps/masterjava/config/wsdl/mailService.wsdl").toURI().toURL(),
                    new QName("http://mail.javaops.ru/", "MailServiceImplService"),
                    MailService.class);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        WS_CLIENT.init("mail", "/mail/mailService?wsdl");
    }


    public static void sendToGroup(final Set<Addressee> to, final Set<Addressee> cc, final String subject, final String body) {
        log.info("Send mail to '" + to + "' cc '" + cc + "' subject '" + subject + (log.isDebugEnabled() ? "\nbody=" + body : ""));
        WS_CLIENT.getPort().sendToGroup(to, cc, subject, body);
    }
}
