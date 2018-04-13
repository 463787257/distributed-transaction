package com.luol.transaction.common.coordinator.impl;

import com.luol.transaction.common.bean.model.NtcTransaction;
import com.luol.transaction.common.config.NtcConfig;
import com.luol.transaction.common.coordinator.CoordinatorService;
import com.luol.transaction.common.spi.CoordinatorRepository;
import com.luol.transaction.common.utils.SpringBeanUtils;
import org.springframework.stereotype.Service;

/**
 * @author luol
 * @date 2018/4/4
 * @time 9:12
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
@Service("coordinatorService")
public class CoordinatorServiceImpl implements CoordinatorService {

    private CoordinatorRepository coordinatorRepository;

    /**
     * 启动本地补偿事务，根据配置是否进行补偿
     *
     * @throws Exception 异常
     */
    @Override
    public void start(NtcConfig ntcConfig) throws Exception {
        coordinatorRepository = SpringBeanUtils.getInstance()
                .getBean(CoordinatorRepository.class);
        //todo modelName需要指定
        coordinatorRepository.init(ntcConfig.getModelName());
    }

    /**
     * 保存补偿事务信息
     *
     * @param ntcTransaction 实体对象
     * @return 主键id
     */
    @Override
    public String save(NtcTransaction ntcTransaction) {
        final int rows = coordinatorRepository.create(ntcTransaction);
        if (rows > 0) {
            return ntcTransaction.getTransID();
        }
        return null;
    }

    /**
     * 根据事务id获取NtcTransaction
     *
     * @param transID 事务id
     * @return NtcTransaction
     */
    @Override
    public NtcTransaction findByTransID(String transID) {
        return coordinatorRepository.findByTransID(transID);
    }

    /**
     * 根据id,类名，方法名获取对象
     *
     * @param transID      transID
     * @param targetClass  类名
     * @param targetMethod 方法名
     * @return NtcTransaction
     */
    @Override
    public NtcTransaction getByTransIDAndName(String transID, String targetClass, String targetMethod) {
        return coordinatorRepository.getByTransIDAndName(transID, targetClass, targetMethod);
    }

    /**
     * 删除补偿事务信息
     *
     * @param transID 事务id
     * @return true成功 false 失败
     */
    @Override
    public boolean remove(String transID) {
        return coordinatorRepository.remove(transID) > 0;
    }

    /**
     * 更新
     *
     * @param ntcTransaction 实体对象
     */
    @Override
    public void update(NtcTransaction ntcTransaction) {
        coordinatorRepository.update(ntcTransaction);
    }

}
