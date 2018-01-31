package com.btjf.distributed.core.spi.mapper;

import com.btjf.distributed.core.spi.model.DistributedTransactionData;
import com.btjf.distributed.core.spi.model.DistributedTransactionDataExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DistributedTransactionDataMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table s_distributed_transaction
     *
     * @mbg.generated
     */
    long countByExample(DistributedTransactionDataExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table s_distributed_transaction
     *
     * @mbg.generated
     */
    int deleteByExample(DistributedTransactionDataExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table s_distributed_transaction
     *
     * @mbg.generated
     */
    int insert(DistributedTransactionData record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table s_distributed_transaction
     *
     * @mbg.generated
     */
    int insertSelective(DistributedTransactionData record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table s_distributed_transaction
     *
     * @mbg.generated
     */
    List<DistributedTransactionData> selectByExample(DistributedTransactionDataExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table s_distributed_transaction
     *
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") DistributedTransactionData record, @Param("example") DistributedTransactionDataExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table s_distributed_transaction
     *
     * @mbg.generated
     */
    int updateByExample(@Param("record") DistributedTransactionData record, @Param("example") DistributedTransactionDataExample example);
}