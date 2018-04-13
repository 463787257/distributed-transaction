package com.luol.transaction.core.service.impl;

import com.luol.transaction.common.config.NtcConfig;
import com.luol.transaction.common.coordinator.CoordinatorService;
import com.luol.transaction.common.enums.RepositorySupportEnum;
import com.luol.transaction.common.enums.SerializeEnum;
import com.luol.transaction.common.serializer.ObjectSerializer;
import com.luol.transaction.common.spi.CoordinatorRepository;
import com.luol.transaction.common.utils.SpringBeanUtils;
import com.luol.transaction.core.service.NtcInitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.stream.StreamSupport;

/**
 * @author luol
 * @date 2018/4/3
 * @time 9:51
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
@Service("ntcInitService")
public class NtcInitServiceImpl implements NtcInitService {

    @Resource
    private CoordinatorService coordinatorService;

    private static final Logger LOGGER = LoggerFactory.getLogger(NtcInitServiceImpl.class);

    /**
     * ntc分布式事务初始化方法
     */
    @Override
    public void initialization() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> LOGGER.error("系统关闭")));
        try {
            //todo ntcConfig修改配置位置
            NtcConfig ntcConfig = new NtcConfig();
            loadSpiSupport(ntcConfig);
            coordinatorService.start(ntcConfig);
        } catch (Exception ex) {
            LOGGER.error("ntc事务初始化异常:{}", ex);
            //非正常关闭
            System.exit(1);
        }
        LOGGER.info("Ntc事务初始化成功！");
    }

    /**
     * 序列化spi，持久化spi加载
     */
    private void loadSpiSupport(NtcConfig ntcConfig) {
        //spi  serialize
        final SerializeEnum serializeEnum = SerializeEnum.acquire(ntcConfig.getSerializer());
        final ServiceLoader<ObjectSerializer> objectSerializers = ServiceLoader.load(ObjectSerializer.class);

        final Optional<ObjectSerializer> serializer = StreamSupport.stream(objectSerializers.spliterator(), false)
                .filter(objectSerializer -> Objects.equals(objectSerializer.getScheme(), serializeEnum)).findFirst();

        //spi  repository support
        final RepositorySupportEnum repositorySupportEnum = RepositorySupportEnum.acquire(ntcConfig.getRepositorySupport());
        final ServiceLoader<CoordinatorRepository> recoverRepositories = ServiceLoader.load(CoordinatorRepository.class);


        final Optional<CoordinatorRepository> repositoryOptional = StreamSupport.stream(recoverRepositories.spliterator(), false)
                .filter(recoverRepository -> Objects.equals(recoverRepository.getScheme(), repositorySupportEnum)).findFirst();

        //将CoordinatorRepository实现注入到spring容器
        repositoryOptional.ifPresent(repository -> {
            serializer.ifPresent(repository::setSerializer);
            SpringBeanUtils.getInstance().registerBean(CoordinatorRepository.class.getName(), repository);
        });
    }

}
