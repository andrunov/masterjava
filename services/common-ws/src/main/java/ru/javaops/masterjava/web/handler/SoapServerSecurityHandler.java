package ru.javaops.masterjava.web.handler;

import com.sun.xml.txw2.output.IndentingXMLStreamWriter;
import com.sun.xml.ws.api.handler.MessageHandlerContext;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.streaming.XMLStreamWriterFactory;
import lombok.extern.slf4j.Slf4j;
import ru.javaops.masterjava.web.AuthUtil;

import javax.xml.stream.XMLStreamWriter;
import javax.xml.ws.handler.MessageContext;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Slf4j
public class SoapServerSecurityHandler extends SoapBaseHandler {

    @Override
    public boolean handleMessage(MessageHandlerContext context) {
        String AUTH_HEADER = AuthUtil.encodeBasicAuthHeader("user", "password");
        Map<String, List<String>> headers = (Map<String, List<String>>) context.get(MessageContext.HTTP_REQUEST_HEADERS);
        int code = AuthUtil.checkBasicAuth(headers, AUTH_HEADER);
        if (code != 0) {
            context.put(MessageContext.HTTP_RESPONSE_CODE, code);
            throw new SecurityException();
        }
        return true;
    }

    @Override
    public boolean handleFault(MessageHandlerContext context) {
        log.error("Fault SOAP request:\n" + getMessageText(((Message) context.getMessage())));
        return false;
    }

    protected static String getMessageText(Message msg) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            XMLStreamWriter writer = XMLStreamWriterFactory.create(out, "UTF-8");
            IndentingXMLStreamWriter wrap = new IndentingXMLStreamWriter(writer);
            msg.writeTo(wrap);
            return out.toString(StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            log.warn("Coudn't get SOAP message for logging", e);
            return null;
        }
    }
}
