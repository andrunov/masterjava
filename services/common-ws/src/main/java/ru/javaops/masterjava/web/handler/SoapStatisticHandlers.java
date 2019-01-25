package ru.javaops.masterjava.web.handler;

import com.sun.xml.ws.api.handler.MessageHandlerContext;
import lombok.extern.slf4j.Slf4j;
import ru.javaops.masterjava.web.Statistics;

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

}
