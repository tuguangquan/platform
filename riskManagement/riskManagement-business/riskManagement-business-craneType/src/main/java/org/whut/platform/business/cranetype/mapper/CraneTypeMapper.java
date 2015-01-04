package org.whut.platform.business.cranetype.mapper;

import org.apache.ibatis.annotations.Param;
import org.whut.platform.business.cranetype.entity.CraneType;
import org.whut.platform.fundamental.orm.mapper.AbstractMapper;

import java.util.List;
import java.util.Map;


/**
 * Created with IntelliJ IDEA.
 * User: hadoop
 * Date: 14-12-30
 * Time: 下午4:11
 * To change this template use File | Settings | File Templates.
 */
public interface CraneTypeMapper extends AbstractMapper<CraneType> {
    public List<CraneType> findByCondition(Map<String,Object> map);
    public int deleteByCraneId(@Param("craneTypeId")Long craneTypeId);
    public List<Map<String,String>>listModel();
    public List<Map<String,String>>list();
    public String findIdByModelName(@Param("modelName") String modelName);
}
