package ru.javaops.masterjava.service.mail.listeners;

import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;
import ru.javaops.masterjava.service.mail.Attachment;
import ru.javaops.masterjava.service.mail.GroupResult;
import ru.javaops.masterjava.service.mail.MailServiceExecutor;
import ru.javaops.masterjava.service.mail.MailWSClient;
import ru.javaops.masterjava.service.mail.util.Attachments;
import ru.javaops.masterjava.service.mail.util.MailUtil;

import javax.jms.*;
import javax.naming.InitialContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.Part;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.Collections;

@WebListener
@Slf4j
public class JmsMailListener implements ServletContextListener {
    private Thread listenerThread = null;
    private QueueConnection connection;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        final String[] result = new String[1];
        try {
            InitialContext initCtx = new InitialContext();
            ActiveMQConnectionFactory connectionFactory = (ActiveMQConnectionFactory) initCtx.lookup("java:comp/env/jms/ConnectionFactory");
            connectionFactory.setTrustAllPackages(true);
            connection = connectionFactory.createQueueConnection();
            QueueSession queueSession = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue queue = (Queue) initCtx.lookup("java:comp/env/jms/queue/MailQueue");
            QueueReceiver receiver = queueSession.createReceiver(queue);
            connection.start();
            log.info("Listen JMS messages ...");
            listenerThread = new Thread(() -> {
                try {
                    while (!Thread.interrupted()) {
                        Message m = receiver.receive();
                        if (m instanceof ObjectMessage) {
                            ObjectMessage objectMessage = (ObjectMessage) m;
                            MailUtil mailUtil = (MailUtil) objectMessage.getObject();
                            Attachment attachment = Attachments.getAttachment( mailUtil.getAttachmentName(), new ByteArrayInputStream(mailUtil.getAttachment()));
                            GroupResult groupResult = MailServiceExecutor.sendBulk( mailUtil.getAddressees(),
                                                                                    mailUtil.getSubject(),
                                                                                    mailUtil.getSubject(),
                                                                                    Collections.singletonList(attachment));
                                result[0] = groupResult.toString();
                            log.info("Processing finished with result: {}", result[0]);
                        }
                    }
                } catch (Exception e) {
                    log.error("Receiving messages failed: " + e.getMessage(), e);
                }
            });
            listenerThread.start();
        } catch (Exception e) {
            log.error("JMS failed: " + e.getMessage(), e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (connection != null) {
            try {
                connection.close();
            } catch (JMSException ex) {
                log.warn("Couldn't close JMSConnection: ", ex);
            }
        }
        if (listenerThread != null) {
            listenerThread.interrupt();
        }
    }
}