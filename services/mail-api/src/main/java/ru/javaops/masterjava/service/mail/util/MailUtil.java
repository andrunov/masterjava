package ru.javaops.masterjava.service.mail.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.javaops.masterjava.service.mail.Addressee;

import java.io.Serializable;
import java.util.Set;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MailUtil implements Serializable {

    private Set<Addressee> addressees;
    private String subject;
    private String body;
    private String attachmentName;
    private byte[] attachment;

}
