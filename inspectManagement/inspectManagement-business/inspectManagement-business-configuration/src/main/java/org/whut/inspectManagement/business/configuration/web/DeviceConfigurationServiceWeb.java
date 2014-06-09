package org.whut.inspectManagement.business.configuration.web;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.whut.inspectManagement.business.configuration.service.DeviceConfigurationService;
import org.whut.inspectManagement.business.device.entity.DeviceTypeTag;
import org.whut.inspectManagement.business.device.service.DeviceService;
import org.whut.platform.fundamental.logger.PlatformLogger;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 14-5-29
 * Time: 下午6:29
 * To change this template use File | Settings | File Templates.
 */
@Component
@Path("/deviceConfiguration")
public class DeviceConfigurationServiceWeb {
    private static PlatformLogger logger = PlatformLogger.getLogger(DeviceConfigurationServiceWeb.class);

    @Autowired
    DeviceConfigurationService deviceConfigurationService;

    @Autowired
    DeviceService deviceService;
    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    @Path("/importDeviceConfiguration/{data}")
    @GET
    public void importPersonnelConfiguration(@PathParam("data") String data,@Context HttpServletResponse response){
        List<DeviceTypeTag> deviceList = new ArrayList<DeviceTypeTag>();
        String[] tagIds = data.split(",");
        for(String s:tagIds){
            long tagId  = Long.parseLong(s);
            List<Map<String,String>> mapList = deviceService.getListByTagId(tagId);
            for(Map<String,String> map:mapList){
                DeviceTypeTag dtt = new DeviceTypeTag();
                dtt.setDeviceNumber(map.get("deviceNumber"));
                dtt.setDeviceType(map.get("deviceType"));
                dtt.setDeviceTypeNumber(map.get("deviceTypeNumber"));;
                dtt.setTagId(tagId);
                dtt.setTagName(map.get("tagName"));
                dtt.setTagNumber(map.get("tagNumber"));
                deviceList.add(dtt);
            }
        }
        String result = deviceConfigurationService.configurationConstruction(deviceList);
        System.out.println(result);
        if(result!=""){
            try{
                String fileName = "tags.xml";
                response.setContentType("text/plain");
                response.setHeader("Location",new String(fileName.getBytes("GBK"),"UTF-8"));
                response.setHeader("Content-Disposition","attachment;filename="+new String(fileName.getBytes("gb2312"),"ISO8859-1"));
                OutputStream outputStream = response.getOutputStream();
                outputStream.write(result.getBytes());
                outputStream.flush();
                outputStream.close();
            }
            catch(Exception e){
                e.printStackTrace();
                logger.error(e.getMessage());
            }
        }
    }
}