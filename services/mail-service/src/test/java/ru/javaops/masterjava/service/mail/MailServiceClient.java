package ru.javaops.masterjava.service.mail;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.slf4j.event.Level;
import ru.javaops.masterjava.web.AuthUtil;
import ru.javaops.masterjava.web.WebStateException;
import ru.javaops.masterjava.web.handler.SoapLoggingHandlers;

import javax.activation.DataHandler;
import javax.annotation.Resource;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import static com.google.common.net.HttpHeaders.AUTHORIZATION;

public class MailServiceClient {
/*
    public static final String USER = "user";
    public static final String PASSWORD = "password";

    public static String AUTH_HEADER = AuthUtil.encodeBasicAuthHeader(USER, PASSWORD);

    @Resource
    private static WebServiceContext wsContext;
*/
    public static void main(String[] args) throws MalformedURLException, WebStateException {
        Service service = Service.create(
                new URL("http://localhost:8080/mail/mailService?wsdl"),
                new QName("http://mail.javaops.ru/", "MailServiceImplService"));

/*/

        MessageContext mCtx = wsContext.getMessageContext();
        Map<String, List<String>> headers = (Map<String, List<String>>) mCtx.get(MessageContext.HTTP_REQUEST_HEADERS);
        headers.put(AUTHORIZATION, AUTH_HEADER);
*/
        List<Attachment> attachments = ImmutableList.of(
                new Attachment("version.html", new DataHandler(new File("config_templates/version.html").toURI().toURL())));

        MailService mailService = service.getPort(MailService.class);



        String state = mailService.sendToGroup(ImmutableSet.of(new Addressee("masterjava@javaops.ru", null)), null,
                "Group mail subject", "Group mail body", attachments);
        System.out.println("Group mail state: " + state);

        GroupResult groupResult = mailService.sendBulk(ImmutableSet.of(
                new Addressee("Мастер Java <masterjava@javaops.ru>"),
                new Addressee("Bad Email <bad_email.ru>")), "Bulk mail subject", "Bulk mail body", attachments);
        System.out.println("\nBulk mail groupResult:\n" + groupResult);
    }
}
