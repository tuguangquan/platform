package org.whut.rentManagement.business.device.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.whut.rentManagement.business.device.entity.Device;
import org.whut.rentManagement.business.device.mapper.DeviceMapper;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Steven
 * Date: 14-10-10
 * Time: 上午11:17
 * To change this template use File | Settings | File Templates.
 */
public class DeviceService {
    @Autowired
    private DeviceMapper deviceMapper;

    public void add(Device device){
        deviceMapper.add(device);
    }

    public int delete(Device device){
        return deviceMapper.delete(device);
    }

    public int update(Device device){
        return deviceMapper.update(device);
    }

    public Long getIdByNumber(String number, long appId) {
        return deviceMapper.getIdByNumber(number,appId);
    }

    public List<Device> getListByAppId(long appId) {
        return deviceMapper.getListByAppId(appId);
    }
}