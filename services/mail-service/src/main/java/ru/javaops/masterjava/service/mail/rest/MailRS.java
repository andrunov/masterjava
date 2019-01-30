package ru.javaops.masterjava.service.mail.rest;


import org.glassfish.jersey.media.multipart.BodyPartEntity;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.hibernate.validator.constraints.NotBlank;
import ru.javaops.masterjava.service.mail.Attachment;
import ru.javaops.masterjava.service.mail.GroupResult;
import ru.javaops.masterjava.service.mail.MailServiceExecutor;
import ru.javaops.masterjava.service.mail.MailWSClient;
import ru.javaops.masterjava.service.mail.util.Attachments;
import ru.javaops.masterjava.web.WebStateException;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@Path("/")
public class MailRS {
    @GET
    @Path("test")
    @Produces(MediaType.TEXT_PLAIN)
    public String test() {
        return "Test";
    }

    @POST
    @Path("send")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public GroupResult send(@NotBlank @FormDataParam("users") String users,
                            @FormDataParam("subject") String subject,
                            @NotBlank @FormDataParam("body") String body,
                            @FormDataParam("attach") FormDataBodyPart attachBodyPart) throws WebStateException {

        List<Attachment> attachments = new ArrayList<>();
        BodyPartEntity bodyPartEntity = ((BodyPartEntity) attachBodyPart.getEntity());
        String attachName = attachBodyPart.getContentDisposition().getFileName();
//          UTF-8 encoding workaround: https://java.net/jira/browse/JERSEY-3032
        String utf8name = null;
        try {
            utf8name = new String(attachName.getBytes("ISO8859_1"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Attachment attachment = new Attachment(utf8name,  new DataHandler(new DataSource() {
            @Override
            public InputStream getInputStream() throws IOException {
                return bodyPartEntity.getInputStream();
            }

            @Override
            public OutputStream getOutputStream() throws IOException {
                throw new UnsupportedOperationException("Not implemented");
            }

            @Override
            public String getContentType() {
                return "application/octet-stream";
            }

            @Override
            public String getName() {
                return "";
            }
        }));
        attachments.add(attachment);
        return MailServiceExecutor.sendBulk(MailWSClient.split(users), subject, body, attachments);
    }

}