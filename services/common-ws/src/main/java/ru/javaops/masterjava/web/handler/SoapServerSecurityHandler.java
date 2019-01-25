package ru.javaops.masterjava.web.handler;

import com.sun.xml.ws.api.handler.MessageHandlerContext;
import com.sun.xml.ws.api.message.Message;
import com.typesafe.config.Config;
import lombok.extern.slf4j.Slf4j;
import ru.javaops.masterjava.config.Configs;
import ru.javaops.masterjava.web.AuthUtil;

import javax.xml.ws.handler.MessageContext;
import java.util.List;
import java.util.Map;

@Slf4j
public class SoapServerSecurityHandler extends SoapBaseHandler {

    @Override
    public boolean handleMessage(MessageHandlerContext context) {
        Config configs = Configs.getConfig("hosts.conf", "hosts").getConfig("mail");
        String AUTH_HEADER = AuthUtil.encodeBasicAuthHeader(configs.getString("user"), configs.getString("password"));
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

}
