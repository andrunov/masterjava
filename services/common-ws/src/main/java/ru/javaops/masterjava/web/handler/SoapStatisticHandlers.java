package ru.javaops.masterjava.web.handler;

import com.sun.xml.txw2.output.IndentingXMLStreamWriter;
import com.sun.xml.ws.api.handler.MessageHandlerContext;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.streaming.XMLStreamWriterFactory;
import lombok.extern.slf4j.Slf4j;
import ru.javaops.masterjava.web.Statistics;

import javax.xml.stream.XMLStreamWriter;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
public class SoapStatisticHandlers extends SoapBaseHandler {

    public SoapStatisticHandlers() {
    }

    @Override
    public boolean handleMessage(MessageHandlerContext context) {
        Statistics.countSuccess(context.getMessage().getClass().getCanonicalName(), System.currentTimeMillis(), Statistics.RESULT.SUCCESS);
        return true;
    }

    @Override
    public boolean handleFault(MessageHandlerContext context) {
        Statistics.countFault(context.getMessage().getClass().getCanonicalName(), System.currentTimeMillis(), Statistics.RESULT.FAIL);
        return true;
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
