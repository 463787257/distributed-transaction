package com.luol.transaction.core.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.Weigher;
import com.luol.transaction.common.bean.model.NtcTransaction;
import com.luol.transaction.common.utils.SpringBeanUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

/**
 * @author luol
 * @date 2018/3/29
 * @time 14:51
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
public class NtcTransactionCacheManager {
    private static final int MAX_COUNT = 10000;

//    private static CoordinatorService coordinatorService
//            = SpringBeanUtils.getInstance().getBean(CoordinatorService.class);


    private static final NtcTransactionCacheManager Ntc_TRANSACTION_CACHE_MANAGER = new NtcTransactionCacheManager();

    private NtcTransactionCacheManager() {

    }

    public static NtcTransactionCacheManager getInstance() {
        return Ntc_TRANSACTION_CACHE_MANAGER;
    }


    private static final LoadingCache<String, NtcTransaction> LOADING_CACHE = CacheBuilder.newBuilder()
            .maximumWeight(MAX_COUNT)
            .weigher((Weigher<String, NtcTransaction>) (string, NtcTransaction) -> getSize())
            .build(new CacheLoader<String, NtcTransaction>() {
                @Override
                public NtcTransaction load(String key) throws Exception {
                    return cacheNtcTransaction(key);
                }
            });


    private static int getSize() {
        return (int) LOADING_CACHE.size();
    }


    /**
     * todo 获取持久化存储
     * */
    private static NtcTransaction cacheNtcTransaction(String key) {
        final NtcTransaction ntcTransaction = null;//coordinatorService.findByTransId(key);
        if (Objects.isNull(ntcTransaction)) {
            return new NtcTransaction();
        }
        return ntcTransaction;
    }


    /**
     * cache 缓存
     *
     * @param ntcTransaction 事务对象
     */
    public void cacheNtcTransaction(NtcTransaction ntcTransaction) {
        LOADING_CACHE.put(ntcTransaction.getTransID(), ntcTransaction);
    }

    /**
     * 获取task
     *
     * @param key 需要获取的key
     */
    public NtcTransaction getNtcTransaction(String key) {
        try {
            return LOADING_CACHE.get(key);
        } catch (ExecutionException e) {
            return new NtcTransaction();
        }
    }


    public void removeByKey(String key) {
        if (Objects.nonNull(key)) {
            LOADING_CACHE.invalidate(key);
        }
    }
}
