package ru.javaops.masterjava.service.mail;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import ru.javaops.masterjava.web.AuthUtil;
import ru.javaops.masterjava.web.WebStateException;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.handler.MessageContext;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.net.HttpHeaders.AUTHORIZATION;

public class MailServiceClient {

    public static void main(String[] args) throws MalformedURLException, WebStateException {
        Service service = Service.create(
                new URL("http://localhost:8080/mail/mailService?wsdl"),
                new QName("http://mail.javaops.ru/", "MailServiceImplService"));
        List<Attachment> attachments = ImmutableList.of(
                new Attachment("version.html", new DataHandler(new File("config_templates/version.html").toURI().toURL())));

        MailService mailService = service.getPort(MailService.class);

        Map<String, Object> req_ctx = ((BindingProvider)mailService).getRequestContext();
        req_ctx.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "http://localhost:8080/mail/mailService?wsdl");

        String AUTH_HEADER = AuthUtil.encodeBasicAuthHeader("user", "password");

        Map<String, List<String>> headers = new HashMap<>();
        headers.put(AUTHORIZATION, Collections.singletonList(AUTH_HEADER));
        req_ctx.put(MessageContext.HTTP_REQUEST_HEADERS, headers);

        String state = mailService.sendToGroup(ImmutableSet.of(new Addressee("andrunov@gmail.com", null)), null,
                "Group mail subject", "Group mail body", attachments);
        System.out.println("Group mail state: " + state);

        GroupResult groupResult = mailService.sendBulk(ImmutableSet.of(
                new Addressee("Мастер Java <masterjava@javaops.ru>"),
                new Addressee("Bad Email <bad_email.ru>")), "Bulk mail subject", "Bulk mail body", attachments);
        System.out.println("\nBulk mail groupResult:\n" + groupResult);
    }
}
