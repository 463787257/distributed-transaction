package com.luol.transaction.notify.disruptor.logs.handler;

import com.alibaba.fastjson.JSON;
import com.lmax.disruptor.EventHandler;
import com.luol.transaction.common.coordinator.CoordinatorService;
import com.luol.transaction.notify.disruptor.logs.event.NtcTransactionLogs;
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
        if (Objects.isNull(ntcTransactionLogs)) {
            return;
        }
        switch (ntcTransactionLogs.getEventTypeEnum()) {
            case SAVE:
                LOGGER.warn("======SAVE======" + JSON.toJSONString(ntcTransactionLogs));
                coordinatorService.save(ntcTransactionLogs.getNtcTransaction());
                break;
            case UPDATE:
                LOGGER.warn("======UPDATE======" + JSON.toJSONString(ntcTransactionLogs));
                coordinatorService.update(ntcTransactionLogs.getNtcTransaction());
                break;
            case DELETE:
                LOGGER.warn("======DELETE======" + JSON.toJSONString(ntcTransactionLogs));
                coordinatorService.remove(ntcTransactionLogs.getNtcTransaction().getTransID());
                break;
            default:
                LOGGER.warn("======DEFAULT======" + JSON.toJSONString(ntcTransactionLogs));
                break;
        }
        ntcTransactionLogs.clear();
    }
}
