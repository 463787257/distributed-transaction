package com.btjf.distributed.common.bean.context;

import com.btjf.distributed.common.bean.entity.DistributedInvocation;
import com.btjf.distributed.common.enums.DistributedRoleEnum;
import com.btjf.distributed.common.enums.DistributedStatusEnum;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author luol
 * @date 2018/1/29
 * @time 13:46
 * @function 功能：
 * @describe 版本描述：
 * @modifyLog 修改日志：
 */
@Data
public class DistributedTransactionContext implements Serializable {
    private static final long serialVersionUID = 5383330309128023508L;

    private String transID;

    /**
     * 事务参与的角色
     */
    private DistributedRoleEnum role;

    /**
     * 全部调用链
     * */
    private List<DistributedInvocation> distributedInvocationList = new CopyOnWriteArrayList<>();

    /**
     * 调用链---与上面的list存储同一个key对象(更新方便)
     * */
    private Map<String, List<DistributedInvocation>> map = new ConcurrentHashMap<>();

    public void addInvocationChain(DistributedInvocation distributedInvocation) {
        if (distributedInvocation != null) {
            distributedInvocationList.add(distributedInvocation);
            map.putIfAbsent(distributedInvocation.toString(), new CopyOnWriteArrayList<>());
        }
    }

    public void addInvocationChainList(DistributedInvocation distributedInvocation) {
        c:for (int i = distributedInvocationList.size() - 1; i >= 0; i ++) {
            DistributedInvocation k = distributedInvocationList.get(i);
            DistributedStatusEnum distributedStatusEnum = k.getDistributedStatusEnum();
            if (Objects.equals(distributedStatusEnum, DistributedStatusEnum.COMMIT) || Objects.equals(distributedStatusEnum, DistributedStatusEnum.PRE_FAILURE)) {
                continue;
            }
            for (DistributedInvocation kk : distributedInvocationList) {
                if (Objects.equals(kk, distributedInvocation)) {
                    map.get(k.toString()).add(kk);
                    break c;
                }
            }
        }
    }

    public void updateInvocationChain(DistributedInvocation key, DistributedStatusEnum distributedStatusEnum) {
        for (DistributedInvocation k : distributedInvocationList) {
            if (Objects.equals(k, key) && Objects.equals(k.getDistributedStatusEnum(), DistributedStatusEnum.BEGIN)) {
                k.setDistributedStatusEnum(distributedStatusEnum);
                break;
            }
        }
    }

}
