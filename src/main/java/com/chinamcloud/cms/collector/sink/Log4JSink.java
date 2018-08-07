package com.chinamcloud.cms.collector.sink;

import org.apache.flume.Channel;
import org.apache.flume.Context;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.Transaction;
import org.apache.flume.conf.Configurable;
import org.apache.flume.sink.AbstractSink;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class Log4JSink extends AbstractSink implements Configurable {

    private Logger logger = LogManager.getLogger(Log4JSink.class);

    @Override
    public Status process() throws EventDeliveryException {
        Status status = null;
        Channel ch = getChannel();
        Transaction txn = null;
        try{
            txn = ch.getTransaction();
            txn.begin();
            loggerMsg(new String(ch.take().getBody()));
            txn.commit();
            status = Status.READY;
        }catch (Throwable e) {
            if(txn!=null)
                txn.rollback();
            status = Status.BACKOFF;
        }finally{
            if(txn!=null)
                txn.close();
        }
        return status;
    }

    private void loggerMsg(String msg){
        logger.info(msg);
    }

    @Override
    public void configure(Context context) {
        //do nothing

    }
}
