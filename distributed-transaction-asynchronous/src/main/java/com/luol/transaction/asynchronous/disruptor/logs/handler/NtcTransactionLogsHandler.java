package com.luol.transaction.asynchronous.disruptor.logs.handler;

import com.alibaba.fastjson.JSON;
import com.lmax.disruptor.EventHandler;
import com.luol.transaction.asynchronous.disruptor.logs.event.NtcTransactionLogs;
import com.luol.transaction.common.coordinator.CoordinatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author luol
 * @date 2018/3/12
 * @time 10:07
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
@Component
public class NtcTransactionLogsHandler implements EventHandler<NtcTransactionLogs> {

    private final static Logger LOGGER = LoggerFactory.getLogger(NtcTransactionLogsHandler.class);

    @Resource
    private CoordinatorService coordinatorService;

    @Override
    public void onEvent(NtcTransactionLogs ntcTransactionLogs,
                        long sequence, boolean endOfBatch) throws Exception {
        LOGGER.warn("======NtcTransactionLogsHandler开始执行======");
        if (Objects.isNull(ntcTransactionLogs)) {
            return;
        }
        coordinatorService.handlerLogs(ntcTransactionLogs.getNtcTransaction(), ntcTransactionLogs.getEventTypeEnum());
        ntcTransactionLogs.clear();
        LOGGER.warn("======NtcTransactionLogsHandler执行结束======");
    }
}
