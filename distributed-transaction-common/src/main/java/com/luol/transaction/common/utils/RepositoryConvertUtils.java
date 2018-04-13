package com.luol.transaction.common.utils;

import com.alibaba.fastjson.JSON;
import com.luol.transaction.common.bean.adapter.CoordinatorRepositoryAdapter;
import com.luol.transaction.common.bean.entity.NtcInvocation;
import com.luol.transaction.common.bean.model.NtcTransaction;
import com.luol.transaction.common.enums.NtcRoleEnum;
import com.luol.transaction.common.enums.NtcStatusEnum;
import com.luol.transaction.common.enums.PatternEnum;
import com.luol.transaction.common.exception.NtcException;
import com.luol.transaction.common.serializer.ObjectSerializer;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author luol
 * @date 2018/4/4
 * @time 15:11
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
public class RepositoryConvertUtils {

    public static byte[] convert(NtcTransaction ntcTransaction,
                                 ObjectSerializer objectSerializer) throws NtcException {
        CoordinatorRepositoryAdapter adapter = new CoordinatorRepositoryAdapter(ntcTransaction);

        if (!CollectionUtils.isEmpty(ntcTransaction.getRpcNtcInvocations())) {
            adapter.setRpcCallChain(JSON.toJSONString(ntcTransaction.getRpcNtcInvocations()));
        }
        adapter.setContents(objectSerializer.serialize(ntcTransaction.getRpcNtcInvocations()));
        return objectSerializer.serialize(adapter);
    }

    public static NtcTransaction transformBean(byte[] contents, ObjectSerializer objectSerializer) {

        if (Objects.isNull(contents)) {
            return null;
        }

        NtcTransaction ntcTransaction = new NtcTransaction();

        final CoordinatorRepositoryAdapter adapter =
                objectSerializer.deSerialize(contents, CoordinatorRepositoryAdapter.class);

        List<NtcInvocation> rpcNtcInvocations =
                objectSerializer.deSerialize(adapter.getContents(), ArrayList.class);

        ntcTransaction.setLastTime(adapter.getLastTime());
        ntcTransaction.setCreateTime(adapter.getCreateTime());
        ntcTransaction.setTransID(adapter.getTransID());
        ntcTransaction.setNtcStatusEnum(NtcStatusEnum.objectOf(adapter.getStatus()));
        ntcTransaction.setRpcNtcInvocations(rpcNtcInvocations);
        ntcTransaction.setNtcRoleEnum(NtcRoleEnum.objectOf(adapter.getRole()));
        ntcTransaction.setPatternEnum(PatternEnum.objectOf(adapter.getPattern()));
        ntcTransaction.setCurrentRetryCounts(adapter.getCurrentRetryCounts());
        ntcTransaction.setMaxRetryCounts(adapter.getMaxRetryCounts());
        ntcTransaction.setTargetClass(adapter.getTargetClass());
        ntcTransaction.setTargetMethod(adapter.getTargetMethod());

        ntcTransaction.setVersion(adapter.getVersion());
        return ntcTransaction;
    }

}
