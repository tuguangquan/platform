package org.whut.platform.business.craneinspectreport.web;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.whut.platform.business.address.entity.Address;
import org.whut.platform.business.address.service.AddressService;
import org.whut.platform.business.craneinspectreport.entity.CraneInspectReport;
import org.whut.platform.business.craneinspectreport.riskcalculate.ICalculateRisk;
import org.whut.platform.business.craneinspectreport.service.CraneInspectReportService;
import org.whut.platform.business.user.entity.Power;
import org.whut.platform.business.user.security.MyUserDetail;
import org.whut.platform.business.user.service.UserService;
import org.whut.platform.fundamental.config.FundamentalConfigProvider;
import org.whut.platform.fundamental.fileupload.FileInfo;
import org.whut.platform.fundamental.fileupload.FileService;
import org.whut.platform.fundamental.fileupload.MultipartRequestParser;
import org.whut.platform.fundamental.fileupload.MultipartRequestResult;
import org.whut.platform.fundamental.logger.PlatformLogger;
import org.whut.platform.fundamental.map.BaiduMapUtil;
import org.whut.platform.fundamental.util.json.JsonMapper;
import org.whut.platform.fundamental.util.json.JsonResultUtils;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: zhuzhenhua
 * Date: 14-3-17
 * Time: 下午10:40
 * To change this template use File | Settings | File Templates.
 */
@Component
@Path("/craneinspectreport")
public class CraneInspectReportServiceWeb {
    private static PlatformLogger logger = PlatformLogger.getLogger(CraneInspectReportServiceWeb.class);
    @Autowired
    private CraneInspectReportService craneInspectReportService;
    @Autowired
    private AddressService addressService;
    @Autowired
    private UserService userService;

    private String singlePicURL="";
    private BaiduMapUtil baiduMapUtil=new BaiduMapUtil();
    private MultipartRequestParser multipartRequestParser=new MultipartRequestParser();
    private static List<Map<String,String>> mList=new ArrayList<Map<String, String>>();
    //缓存查出的所有地址
    private static List<Address> addressList=new ArrayList<Address>();
    private static List<Address> idList=new ArrayList<Address>();

    @Produces(MediaType.MULTIPART_FORM_DATA)
    @Path("/upload")
    @POST
    public String upload(@Context HttpServletRequest request){     //excel上传功能
        if (request == null) {
            return JsonResultUtils
                    .getCodeAndMesByStringAsDefault(JsonResultUtils.Code.ERROR);
        }
        List<String> unExistEquipmentVarietyInCraneType=new ArrayList<String>();
        try {
            request.setCharacterEncoding("UTF-8");
            int uploadMaxSize= Integer.parseInt(FundamentalConfigProvider.get("uploadMaxSize"));
            FileService fileService=new FileService("xls");
            FileInfo fileInfo=new FileInfo();
            fileInfo=parseRequst(request,multipartRequestParser,fileService,uploadMaxSize);
            if(fileInfo.getName()==null){
                //文件名为空
            }else{
                unExistEquipmentVarietyInCraneType=craneInspectReportService.getUnExistInCraneType(fileInfo.getInputStream());
                if(unExistEquipmentVarietyInCraneType==null||unExistEquipmentVarietyInCraneType.size()==0){
                craneInspectReportService.upload(fileInfo.getInputStream(),fileInfo.getName());
                    // 新增操作时，返回操作状态和状态码给客户端，数据区是为空的
                    List<CraneInspectReport> list=craneInspectReportService.getRepeatList();
                    if (list.size()==0||list==null){
                        return JsonResultUtils.getObjectResultByStringAsDefault(list,JsonResultUtils.Code.SUCCESS);
                    }
                    else {
                        return JsonResultUtils.getObjectResultByStringAsDefault(list,JsonResultUtils.Code.DUPLICATE);
                    }
                }else{
                return JsonResultUtils.getObjectResultByStringAsDefault(unExistEquipmentVarietyInCraneType,JsonResultUtils.Code.QUESTION);
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            logger.error(e.getMessage());
            return JsonResultUtils.getCodeAndMesByStringAsDefault(JsonResultUtils.Code.ERROR);
        }
        return null;
}
      public FileInfo parseRequst(@Context HttpServletRequest request,MultipartRequestParser multipartRequestParser,FileService fileService,int uploadMaxSize){
               FileInfo fileInfo=null;
               try{
               fileInfo=multipartRequestParser.parseRequest(request,uploadMaxSize,fileService);
               }catch (Exception e){
                   e.printStackTrace();
               }
             return fileInfo;
      }
    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    @Path("/list")
    @GET
      public String list(){
          List<CraneInspectReport> list=craneInspectReportService.list();
          return JsonResultUtils.getObjectResultByStringAsDefault(list,JsonResultUtils.Code.SUCCESS);
      }
    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    @Path("/delete")
    @POST
      public String delete(@FormParam("jsonString") String jsonString){
          CraneInspectReport craneInspectReport= JsonMapper.buildNonDefaultMapper().fromJson(jsonString,CraneInspectReport.class);
          int result=craneInspectReportService.delete(craneInspectReport);
          if (result>0){
              return JsonResultUtils.getCodeAndMesByStringAsDefault(JsonResultUtils.Code.SUCCESS);
          }else{
              return JsonResultUtils.getCodeAndMesByStringAsDefault(JsonResultUtils.Code.ERROR);
          }
      }
/*    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    @Path("/refreshRepeatList")
    @POST
    public String refreshRepeatList(){
        craneInspectReportService.refreshRepeatList();
        return JsonResultUtils.getCodeAndMesByStringAsDefault(JsonResultUtils.Code.SUCCESS);
    }
    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    @Path("/refreshList")
    @POST
    public String refreshList(){
        craneInspectReportService.refreshList();
        return JsonResultUtils.getCodeAndMesByStringAsDefault(JsonResultUtils.Code.SUCCESS);
    }*/
    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    @Path("/addRepeat")
    @POST
    public String addRepeat(){
        CraneInspectReport craneInspectReport=new CraneInspectReport();
        List<CraneInspectReport> list=craneInspectReportService.getRepeatList();
        for(CraneInspectReport repeat:list){
            craneInspectReport.setReportNumber(repeat.getReportNumber());
            craneInspectReport.setUnitAddress(repeat.getUnitAddress());
            craneInspectReport.setAddressId(repeat.getAddressId());
            craneInspectReport.setOrganizeCode(repeat.getOrganizeCode());
            craneInspectReport.setUserPoint(repeat.getUserPoint());
            craneInspectReport.setSafeManager(repeat.getSafeManager());
            craneInspectReport.setContactPhone(repeat.getContactPhone());
            craneInspectReport.setEquipmentVariety(repeat.getEquipmentVariety());
            craneInspectReport.setUnitNumber(repeat.getUnitNumber());
            craneInspectReport.setManufactureUnit(repeat.getManufactureUnit());
            craneInspectReport.setManufactureLicenseNumber(repeat.getManufactureLicenseNumber());
            craneInspectReport.setManufactureDate(repeat.getManufactureDate());
            craneInspectReport.setSpecification(repeat.getSpecification());
            craneInspectReport.setpNumber(repeat.getpNumber());
            craneInspectReport.setWorkLevel(repeat.getWorkLevel());
            craneInspectReportService.update(craneInspectReport);
        }
        list.clear();
        return JsonResultUtils.getCodeAndMesByStringAsDefault(JsonResultUtils.Code.SUCCESS);
    }
    @Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Path("/getAreaInfo")
    @POST
    public String getAreaInfo(@FormParam("city") String city,@FormParam("area") String area){
        //根据市和区得到craneinpectreport的信息
        if(city==null||city.trim().equals("")||area==null||area.trim().equals("")){
            return JsonResultUtils.getCodeAndMesByStringAsDefault(JsonResultUtils.Code.ERROR);
        }
         List<CraneInspectReport> list=craneInspectReportService.getAreaInfo(city,area);
         return JsonResultUtils.getObjectResultByStringAsDefault(list,JsonResultUtils.Code.SUCCESS);
    }
    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    @POST
    @Path("/getAreaInfoByUnitAddress")
    public String  getAreaInfoByUnitAddress(@FormParam("unitAddress") String unitAddress){
        //通过unitAddress得到区域信息
        if(unitAddress==null||unitAddress.trim().equals("")){
            return  JsonResultUtils.getCodeAndMesByStringAsDefault(JsonResultUtils.Code.ERROR);
        }
        List<CraneInspectReport> list=craneInspectReportService.getInfoByUnitAddress(unitAddress);
        return JsonResultUtils.getObjectStrResultByStringAsDefault(list,200,unitAddress);
    }
    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    @POST
    @Path("/getCraneInspectReportInfoByAddressAndEquipment")
    public String getCraneInspectReportInfoByAddressAndEquipment(@FormParam("address_equipmentvariety") String address_equipmentvariety,@FormParam("itemInfoId") String itemInfo){
        //根据unitAddress以及设备种类得到craneinspectreport信息
        if(address_equipmentvariety==null||address_equipmentvariety.trim().equals("")){
            return JsonResultUtils.getCodeAndMesByStringAsDefault(JsonResultUtils.Code.ERROR);
        }
           String[] str=address_equipmentvariety.split(",");
           List<CraneInspectReport> list=craneInspectReportService.getCraneInspectReportInfoByAddressAndEquipment(str[0],str[1]);
           return JsonResultUtils.getObjectStrResultByStringAsDefault(list,200,itemInfo);
    }

    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    @Path("/getUnitaddressByArea")
    @POST
    public String getUnitaddressByArea(@FormParam("province") String province,@FormParam("city") String city,@FormParam("area") String area)
    {
        //根据省市区得到unitAddress(公司)
        List<CraneInspectReport> list=craneInspectReportService.getUnitaddressByArea(province,city,area);
        return JsonResultUtils.getObjectResultByStringAsDefault(list,JsonResultUtils.Code.SUCCESS);

    }
    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    @Path("/showRiskRank")
    @POST
    public String showRiskRank(@FormParam("city") String city,@FormParam("area") String area)
    {
        //根据城市以及地区得到风险
        List<CraneInspectReport> list=craneInspectReportService.showRiskRank(city,area);

        return JsonResultUtils.getObjectResultByStringAsDefault(list,JsonResultUtils.Code.SUCCESS);

    }
    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    @Path("/showRiskRankByValueRange")
    @POST
    public String showRiskRankByValueRange(@FormParam("value") String value,@FormParam("city") String city,@FormParam("area") String area){
        String[] values= value.split(";");
        float startValue = Float.parseFloat(values[0]);
        float endValue=Float.parseFloat(values[1]);
        List<CraneInspectReport> list = craneInspectReportService.showRiskRankByValueRange(startValue,endValue,city,area);
        return JsonResultUtils.getObjectResultByStringAsDefault(list, JsonResultUtils.Code.SUCCESS);
    }
    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    @POST
    @Path("/getOneUnitAddressInfo")
    public String getOneUnitAddressInfo(@FormParam("unitAddress") String unitAddress){
             //根据unitAddress得到一条craneinspectreport信息
            List<CraneInspectReport> list=new ArrayList<CraneInspectReport>();
            CraneInspectReport craneInspectReport=craneInspectReportService.getOneUnitAddressInfo(unitAddress);
            Long riskValue=craneInspectReportService.findReportNumberByUnitAddress(craneInspectReport.getUnitAddress());
            craneInspectReport.setRiskValue(riskValue);
            list.add(craneInspectReport);
            return JsonResultUtils.getObjectResultByStringAsDefault(list,JsonResultUtils.Code.SUCCESS);
    }
    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    @POST
    @Path("/imageupload")
    public String imageUpload(@Context HttpServletRequest request){
        int uploadMaxSize= Integer.parseInt(FundamentalConfigProvider.get("uploadMaxSize"));
        FileService fileService=new FileService("jpg");
        /*singlePicURL=request.getSession().getServletContext().getInitParameter("UPLOAD_IMAGE_PATH");*/
        String realPath=request.getSession().getServletContext().getRealPath("../imageupload");
        try{
            MultipartRequestResult mrr=multipartRequestParser.parse(request,realPath,uploadMaxSize,fileService);
            String path=mrr.getFileInfos().get(0).getPath();
            String name=mrr.getFileInfos().get(0).getName();
            singlePicURL="../imageupload"+path;
           /* System.out.println(name+"jjjjjjjjj"+path);
            System.out.println(singlePicURL);
            singlePicURL=singlePicURL.replaceAll("\\\\","/");*/

        }catch (Exception e){
            e.printStackTrace();
        }
       // return  JsonResultUtils.getCodeAndMesByStringAsDefault(JsonResultUtils.Code.SUCCESS);
        //return JsonResultUtils.getObjectResultByStringAsDefault(singlePicURL,JsonResultUtils.Code.SUCCESS);
        return  JsonResultUtils.getCodeAndMesByStringAsDefault(JsonResultUtils.Code.SUCCESS);

    }
    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    @POST
    @Path("/getLatLngByProvince")
    public String getLatLngByProvince(@FormParam("province") String province){
        //根据省得到经纬度
        String latLng=null;
        try{
           Map map=baiduMapUtil.getCoordinate(province);
           latLng=map.get("lng")+","+map.get("lat");
        }catch (Exception e){
            e.printStackTrace();
        }
        return JsonResultUtils.getObjectStrResultByStringAsDefault(null,200,latLng);
    }
    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    @POST
    @Path("/getLatLngByCity")
    public String getLatLngByCity(@FormParam("province") String province,@FormParam("city") String city){
        //根据省城市得到经纬度
        String latLng=null;
        try{
            String address=province+""+city;
            Map map=baiduMapUtil.getCoordinate(address);
            latLng=map.get("lng")+","+map.get("lat");
        }catch (Exception e){
            e.printStackTrace();
        }
        return JsonResultUtils.getObjectStrResultByStringAsDefault(null,200,latLng);
    }
    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    @POST
    @Path("/getLatLngByArea")
    public String getLatLngByArea(@FormParam("province") String province,@FormParam("city") String city,@FormParam("area") String area){
        //根据省市区得到经纬度
        String latLng=null;
        try{
            String address=province+""+city+""+area;
            Map map=baiduMapUtil.getCoordinate(address);
            latLng=map.get("lng")+","+map.get("lat");
        }catch (Exception e){
            e.printStackTrace();
        }
        return JsonResultUtils.getObjectStrResultByStringAsDefault(null,200,latLng);
    }
    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    @POST
    @Path("/getCraneInspectReportInfoById")
    public String getCraneInspectReportInfoById(@FormParam("id") long id){
        //通过id得到craneinpectreport信息
         List<CraneInspectReport> list=craneInspectReportService.getCraneInspectReportInfoById(id);
         return JsonResultUtils.getObjectResultByStringAsDefault(list,JsonResultUtils.Code.SUCCESS);
    }
    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    @POST
    @Path("/getCraneInspectReportInfoFromCircle")
    public String getCraneInspectReportInfoFromCircle(@FormParam("radius") String radius,@FormParam("centerlng") String centerlng,@FormParam("centerlat") String centerlat){
        Map map=baiduMapUtil.getAround(Double.parseDouble(centerlat), Double.parseDouble(centerlng), Double.parseDouble(radius));
        String maxLng=map.get("maxLng").toString();
        String maxLat=map.get("maxLat").toString();
        String minLng=map.get("minLng").toString();
        String minLat=map.get("minLat").toString();
        List<CraneInspectReport> list=craneInspectReportService.getCraneInspectReportInfoFromCircle(maxLng,maxLat,minLng,minLat);
        return JsonResultUtils.getObjectResultByStringAsDefault(list,JsonResultUtils.Code.SUCCESS);

    }
    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    @Path("/update")
    @POST
    public String update(@FormParam("reportNumber") String reportNumber){
        CraneInspectReport craneInspectReport=craneInspectReportService.getCraneInspectReportByReportNumber(reportNumber);
        craneInspectReport.setSinglePicURL(singlePicURL);
        craneInspectReport.setTypePicURL("");
        craneInspectReportService.update(craneInspectReport);
        return JsonResultUtils.getCodeAndMesByStringAsDefault(JsonResultUtils.Code.SUCCESS);
    }
    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    @Path("/getEquipmentVarietyList")
    @POST
    public String getEquipmentVarietyList()
    {
        List<CraneInspectReport> list=craneInspectReportService.getEquipmentVarietyList();
        return JsonResultUtils.getObjectResultByStringAsDefault(list,JsonResultUtils.Code.SUCCESS);
    }

    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    @Path("/getProvinceAvgRiskValue")
    @GET
    public String getProvinceAvgRiskValue(){
        List<Map<String,Float>> list=craneInspectReportService.getProvinceAvgRiskValue();
        return JsonResultUtils.getObjectResultByStringAsDefault(list,JsonResultUtils.Code.SUCCESS);
    }
    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    @Path("/getCityAvgRiskValueByProvince")
    @POST
    public String getCityAvgRiskValueByProvince(@FormParam("province") String province){
        List<Map<String,Float>> list=craneInspectReportService.getCityAvgRiskValueByProvince(province);
        return JsonResultUtils.getObjectResultByStringAsDefault(list,JsonResultUtils.Code.SUCCESS);
    }
    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    @Path("/getAreaAvgRiskValueByProvinceAndCity")
    @POST
    public String getAreaAvgRiskValueByProvinceAndCity(@FormParam("province") String province,@FormParam("city") String city){
        List<Map<String,Float>> list=craneInspectReportService.getAreaAvgRiskValueByProvinceAndCity(province,city);
        return JsonResultUtils.getObjectResultByStringAsDefault(list,JsonResultUtils.Code.SUCCESS);
    }

    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    @Path("/getSession")
    @GET
    public String getSession(){
        MyUserDetail myUserDetail=userService.getMyUserDetailFromSession() ;
        System.out.print(myUserDetail.getUsername()+"用户名");
        System.out.print(myUserDetail.getPassword()+"密码");
        return JsonResultUtils.getCodeAndMesByStringAsDefault(JsonResultUtils.Code.SUCCESS);
    }

    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    @Path("/fuzzyQuery")
    @POST
    public String fuzzyQuery(@FormParam("city") String city,@FormParam("area") String area,@FormParam("require") String require){
        List<CraneInspectReport> list=craneInspectReportService.fuzzyQuery(city,area,require);
        return JsonResultUtils.getObjectResultByStringAsDefault(list,JsonResultUtils.Code.SUCCESS);
    }
    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    @Path("/fuzzyQueryByUnitAddress")
    @POST
    public String fuzzyQueryByUnitAddress(@FormParam("city") String city,@FormParam("area") String area,@FormParam("require") String require){
        List<CraneInspectReport> list=craneInspectReportService.fuzzyQueryByUnitAddress(city,area,require);
        return JsonResultUtils.getObjectResultByStringAsDefault(list,JsonResultUtils.Code.SUCCESS);
    }
    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    @Path("/fuzzyQueryByUserPoint")
    @POST
    public String fuzzyQueryByUserPoint(@FormParam("city") String city,@FormParam("area") String area,@FormParam("require") String require){
        List<CraneInspectReport> list=craneInspectReportService.fuzzyQueryByUserPoint(city, area, require);
        return JsonResultUtils.getObjectResultByStringAsDefault(list,JsonResultUtils.Code.SUCCESS);
    }
    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    @Path("/fuzzyQueryBySafeManager")
    @POST
    public String fuzzyQueryBySafeManager(@FormParam("city") String city,@FormParam("area") String area,@FormParam("require") String require){
        List<CraneInspectReport> list=craneInspectReportService.fuzzyQueryBySafeManager(city, area, require);
        return JsonResultUtils.getObjectResultByStringAsDefault(list,JsonResultUtils.Code.SUCCESS);
    }
    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    @Path("/fuzzyQueryByEquipmentVariety")
    @POST
    public String fuzzyQueryByEquipmentVariety(@FormParam("city") String city,@FormParam("area") String area,@FormParam("require") String require){
        List<CraneInspectReport> list=craneInspectReportService.fuzzyQueryByEquipmentVariety(city, area, require);
        return JsonResultUtils.getObjectResultByStringAsDefault(list,JsonResultUtils.Code.SUCCESS);
    }
    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    @Path("/getProvinceRiskRankFormRiskRange")
    @POST
    public String getProvinceRiskRankFormRiskRange(@FormParam("value") String value){
        String[] values= value.split(";");
        float startValue = Float.parseFloat(values[0]);
        float endValue=Float.parseFloat(values[1]);
        List<Map<String,Float>> list = craneInspectReportService.getProvinceRiskRankFormRiskRange(startValue,endValue);
        return JsonResultUtils.getObjectResultByStringAsDefault(list, JsonResultUtils.Code.SUCCESS);
    }
    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    @Path("/getCityRiskRankFormRiskRange")
    @POST
    public String getCityRiskRankFormRiskRange(@FormParam("value") String value,@FormParam("province") String province){
        String[] values= value.split(";");
        float startValue = Float.parseFloat(values[0]);
        float endValue=Float.parseFloat(values[1]);
        List<Map<String,Float>> list = craneInspectReportService.getCityRiskRankFormRiskRange(startValue,endValue,province);
        return JsonResultUtils.getObjectStrResultByStringAsDefault(list,200,province);
    }
    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    @Path("/getCityInfoByCondition")
    @POST
    public String getCityInfoByCondition(@FormParam("province") String province,@FormParam("equipmentVariety")String equipmentVariety,@FormParam("useTime")String useTime,@FormParam("value") String value){
        List<Map<String,Float>> list=new ArrayList<Map<String, Float>>();
        if(value.equals("0")&&useTime.equals("0")){
            list= craneInspectReportService.getCityInfoByCondition(province,equipmentVariety,"0","0",0f,0f);
        }else if(!value.equals("0")&&useTime.equals("0")){
            String[] values= value.split(";");
            float startValue = Float.parseFloat(values[0]);
            float endValue=Float.parseFloat(values[1]);
            list= craneInspectReportService.getCityInfoByCondition(province,equipmentVariety,"0","0",startValue,endValue);
        }else if(value.equals("0")&&!useTime.equals("0")){
            String[] useTimes=useTime.split(";");
            list= craneInspectReportService.getCityInfoByCondition(province,equipmentVariety,useTimes[0],useTimes[1],0f,0f);
        }else if(!value.equals("0")&&!useTime.equals("0")){
            String[] values= value.split(";");
            float startValue = Float.parseFloat(values[0]);
            float endValue=Float.parseFloat(values[1]);
            String[] useTimes=useTime.split(";");
            list= craneInspectReportService.getCityInfoByCondition(province,equipmentVariety,useTimes[0],useTimes[1],startValue,endValue);
        }
        return JsonResultUtils.getObjectStrResultByStringAsDefault(list,200,province);
    }
    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    @Path("/getAreaRiskRankFormRiskRange")
    @POST
    public String getAreaRiskRankFormRiskRange(@FormParam("value") String value,@FormParam("province") String province,@FormParam("city")String city){
        String[] values= value.split(";");
        float startValue = Float.parseFloat(values[0]);
        float endValue=Float.parseFloat(values[1]);
        String province_city=province+","+city;
        List<Map<String,Float>> list = craneInspectReportService.getAreaRiskRankFormRiskRange(startValue,endValue,province,city);
        return JsonResultUtils.getObjectStrResultByStringAsDefault(list,200,province_city);
    }

    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    @Path("/getAreaInfoByCondition")
    @POST
    public String getAreaInfoByCondition(@FormParam("province") String province,@FormParam("city")String city,@FormParam("equipmentVariety")String equipmentVariety,@FormParam("useTime")String useTime,@FormParam("value") String value){
        String province_city=province+","+city;
        List<Map<String,Float>> list =new ArrayList<Map<String, Float>>();
        if(value.equals("0")&&useTime.equals("0")){
            list=craneInspectReportService.getAreaInfoByCondition(province, city, equipmentVariety, "0","0",0f,0f);
        }else if(!value.equals("0")&&useTime.equals("0")){
            String[] values= value.split(";");
            float startValue = Float.parseFloat(values[0]);
            float endValue=Float.parseFloat(values[1]);
            list=craneInspectReportService.getAreaInfoByCondition(province, city, equipmentVariety, "0","0",startValue,endValue);
        }else if(value.equals("0")&&!useTime.equals("0")){
            String[] useTimes=useTime.split(";");
            list=craneInspectReportService.getAreaInfoByCondition(province, city, equipmentVariety, useTimes[0],useTimes[1],0f,0f);
        }else if(!value.equals("0")&&!useTime.equals("0")){
            String[] values= value.split(";");
            float startValue = Float.parseFloat(values[0]);
            float endValue=Float.parseFloat(values[1]);
            String[] useTimes=useTime.split(";");
            list=craneInspectReportService.getAreaInfoByCondition(province, city, equipmentVariety, useTimes[0],useTimes[1],startValue,endValue);
        }
        return JsonResultUtils.getObjectStrResultByStringAsDefault(list,200,province_city);
    }
    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    @Path("/getCraneInfoByFuzzyUnitAddress")
    @POST
    public String getCraneInfoByFuzzyUnitAddress(@FormParam("city") String city,@FormParam("area") String area,@FormParam("require") String require){
        List<String> list=craneInspectReportService.getCraneInfoByFuzzyUnitAddress(city, area, require);
        return JsonResultUtils.getObjectResultByStringAsDefault(list,JsonResultUtils.Code.SUCCESS);
    }
    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    @Path("/getCraneInfoByFuzzyUsePoint")
    @POST
    public String getCraneInfoByFuzzyUsePoint(@FormParam("city") String city,@FormParam("area") String area,@FormParam("require") String require){
        List<String> list=craneInspectReportService.getCraneInfoByFuzzyUsePoint(city, area, require);
        return JsonResultUtils.getObjectResultByStringAsDefault(list,JsonResultUtils.Code.SUCCESS);
    }
    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    @Path("/getCraneInfoByFuzzySafeManager")
    @POST
    public String getCraneInfoByFuzzySafeManager(@FormParam("city") String city,@FormParam("area") String area,@FormParam("require") String require){
        List<String> list=craneInspectReportService.getCraneInfoByFuzzySafeManager(city, area, require);
        return JsonResultUtils.getObjectResultByStringAsDefault(list,JsonResultUtils.Code.SUCCESS);
    }
    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    @Path("/getCraneInfoByFuzzyEquipmentVariety")
    @POST
    public String getCraneInfoByFuzzyEquipmentVariety(@FormParam("city") String city,@FormParam("area") String area,@FormParam("require") String require){
        List<String> list=craneInspectReportService.getCraneInfoByFuzzyEquipmentVariety(city, area, require);
        return JsonResultUtils.getObjectResultByStringAsDefault(list,JsonResultUtils.Code.SUCCESS);
    }
    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    @Path("/getCraneInfoByFuzzyManufactureUnit")
    @POST
    public String getCraneInfoByFuzzyManufactureUnit(@FormParam("city") String city,@FormParam("area") String area,@FormParam("require") String require){
        List<String> list=craneInspectReportService.getCraneInfoByFuzzyManufactureunit(city, area, require);
        return JsonResultUtils.getObjectResultByStringAsDefault(list,JsonResultUtils.Code.SUCCESS);
    }
    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    @Path("/getCraneInfoByFuzzyQuery")
    @POST
    public String getCraneInfoByFuzzyQuery(@FormParam("city") String city,@FormParam("area") String area,@FormParam("require") String require){
        List<String> list=craneInspectReportService.getCraneInfoByFuzzyQuery(city, area, require);
        return JsonResultUtils.getObjectResultByStringAsDefault(list,JsonResultUtils.Code.SUCCESS);
    }
    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    @POST
    @Path("/getUseTimeList")
    public String getUseTimeList(){
        List<String> list=craneInspectReportService.getUseTimeList();
        return JsonResultUtils.getObjectResultByStringAsDefault(list,JsonResultUtils.Code.SUCCESS);
    }
    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    @POST
    @Path("/getCraneInfoByCondition")
    public String getCraneInfoByCondition(@FormParam("province")String province,@FormParam("city")String city,@FormParam("area")String area,@FormParam("equipmentVariety")String equipVariety,@FormParam("useTime")String useTime,@FormParam("value") String value){
         List<CraneInspectReport> list=new ArrayList<CraneInspectReport>();
         if(value.equals("0")&&useTime.equals("0")){
             list=craneInspectReportService.getCraneInfoByCondition(province,city,area,equipVariety,"0","0",0f,0f);
         }else if(!value.equals("0")&&useTime.equals("0")){
             String[] values= value.split(";");
             float startValue = Float.parseFloat(values[0]);
             float endValue=Float.parseFloat(values[1]);
             list=craneInspectReportService.getCraneInfoByCondition(province, city, area, equipVariety,"0","0",startValue,endValue);
         }else if(value.equals("0")&&!useTime.equals("0")){
             String[] useTimes=useTime.split(";");
             list=craneInspectReportService.getCraneInfoByCondition(province, city, area, equipVariety,useTimes[0],useTimes[1],0f,0f);
         }else if(!value.equals("0")&&!useTime.equals("0")){
             String[] values= value.split(";");
             float startValue = Float.parseFloat(values[0]);
             float endValue=Float.parseFloat(values[1]);
             String[] useTimes=useTime.split(";");
             list=craneInspectReportService.getCraneInfoByCondition(province, city, area, equipVariety,useTimes[0],useTimes[1],startValue,endValue);
         }
         return JsonResultUtils.getObjectResultByStringAsDefault(list,JsonResultUtils.Code.SUCCESS);
    }
    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    @POST
    @Path("/calculateRiskValue")
    public String calculateRiskValue(@FormParam("reportId") String reportId){
        //计算风险值，传过来的是uploaded_reportId,通过这些reportId找到对应的起重机，
        // 将相应的信息分装到craneInspectReport对象中，然后根据equipmentVariety
        //来查找craneTypeId，从而找到相应的riskModelId,然后找到className,动态的
        //选择class类来进行计算
        String[] str=reportId.split(",");
        List<Map<String,String>>calculatedReportList=new ArrayList<Map<String, String>>();
        for(int i=0;i<str.length;i++){
        String className=null;
        List<CraneInspectReport> craneList=craneInspectReportService.getCraneListByUploadReportId(Long.parseLong(str[i]));
        List<CraneInspectReport> craneInspectReportList=new ArrayList<CraneInspectReport>();
        List<Map<String,String>>mapList=new ArrayList<Map<String, String>>();
        craneInspectReportService.getDbArrayListFromMongo();
        for(CraneInspectReport craneInspectReport:craneList){
               //根据reportnumber从mongodb中拿出数据封装到craneinspectreport中
               className=craneInspectReportService.getClassNameByEquipmentVariety(craneInspectReport.getEquipmentVariety());
               //通过每个reportnumber从mongodb中拿出数据封装成craneinspectreport对象，然后加载
               CraneInspectReport craneReport=new CraneInspectReport();
               craneReport=craneInspectReportService.getCraneInfoFromMongoByReportNumber(craneInspectReport.getReportNumber(),craneInspectReport.getEquipmentVariety());
               craneInspectReportList.add(craneReport);
        }
        for(CraneInspectReport cr:craneInspectReportList){
            Long craneTypeId=craneInspectReportService.getCraneTypeIdByCraneEquipment(cr.getEquipmentVariety());
            if(craneTypeId!=null){
            Float r=calculateRisk(className,cr,String.valueOf(craneTypeId));
            int riskValue=Math.round(r);
            Map<String,String> m=new HashMap<String,String>();
            if(cr!=null){
                m.put("reportNumber",cr.getReportNumber());
                m.put("riskvalue",String.valueOf(riskValue));
                mapList.add(m);
                mList.add(m);
            }
        }
        }
            Map<String,String> uploadReport=craneInspectReportService.validateReportIsCalculated(Long.parseLong(str[i]));
            if(uploadReport.get("status").equals("未计算")){
            //批量插入riskValue
            if(craneInspectReportService.InsertToRiskValue(mapList)){
                //更新
              craneInspectReportService.updateUploadedReportByReportId(Long.parseLong(str[i]),"已计算");
            };
        }else{
            //将重复的记录保存到list中
            calculatedReportList.add(uploadReport);
        }
    }
        //向前台带的信息为report的信息以及计算的mapList
        return JsonResultUtils.getObjectResultByStringAsDefault(calculatedReportList,JsonResultUtils.Code.SUCCESS);
    }
    public Float calculateRisk(String className,CraneInspectReport craneInspectReport,String craneType){
         Float riskValue=0f;
         try{
         Class c=Class.forName(className);
         ICalculateRisk iCalculateRisk=(ICalculateRisk)c.newInstance();
         riskValue=iCalculateRisk.calculateRisk(craneInspectReport,craneType);
         }catch (Exception e){
             e.printStackTrace();
         }
        return riskValue;
    }
    //计算最大值
    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    @POST
    @Path("/calculateMaxValue")
    public String calculateMaxValue(){
        String r=craneInspectReportService.insertToCraneInspectReportMaxValueCollection();
        if(r.equals("0")){
        return JsonResultUtils.getCodeAndMesByStringAsDefault(JsonResultUtils.Code.MONGOUNCONNECT);
        }else if(r.equals("1")){
        return JsonResultUtils.getCodeAndMesByStringAsDefault(JsonResultUtils.Code.SUCCESS);
        }
        return null;
    }
    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    @POST
    @Path("/listUploadedReport")
    public String listUploadedReport(){
        List<Map<String,String>> list=craneInspectReportService.listUploadedReport();
        return JsonResultUtils.getObjectResultByStringAsDefault(list,JsonResultUtils.Code.SUCCESS);
    }
    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    @POST
    @Path("/updateRiskValueByChooseReport")
    public String updateRiskValueByChooseReport(@FormParam("reportId")String reportId){
          if(reportId.equals("")||reportId.trim().equals("")){
              return JsonResultUtils.getCodeAndMesByStringAsDefault(JsonResultUtils.Code.ERROR);
          }
          List<Map<String,String>>updateRiskValueList=new ArrayList<Map<String, String>>();
          String[] str=reportId.split(",");
          for(int i=0;i<str.length;i++){
              List<CraneInspectReport> craneList=craneInspectReportService.getCraneListByUploadReportId(Long.parseLong(str[i]));
              for(CraneInspectReport c:craneList){
                  Map<String,String> map=new HashMap<String, String>();
                  String riskValue=getRiskValueByReportNumber(c.getReportNumber());
                  if(riskValue!=null){
                  map.put("reportNumber",c.getReportNumber());
                  map.put("riskvalue",riskValue);
                  updateRiskValueList.add(map);
                  }
              }
          }
          //更新
        for(Map<String,String> m:updateRiskValueList){
            craneInspectReportService.updateRiskValueByChooseReport(m.get("reportNumber"),m.get("riskvalue"));
        }
        return JsonResultUtils.getCodeAndMesByStringAsDefault(JsonResultUtils.Code.SUCCESS);
    }
    public String getRiskValueByReportNumber(String reportNumber){
        for(Map<String,String>mm:mList){
            if(mm.get("reportNumber").equals(reportNumber)){
                return mm.get("riskvalue");
            }
        }
        return null;
    }
    //获取所有的地址
    public List<Address> getAllAddress(){
         List<Address> list=addressService.getProvinceCity();
         return list;
    }
    //查出相关联的省市
    public List<Address> getProvinceCity(){
        List<Address> list=addressService.getProvinceCity();
        return list;
    }
    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    @POST
    @Path("/calculateAreaRisk")
    public String calculateAreaRisk(){
        //通过区查出有多少unitAddress,然后根据每家unitAddress求出区域风险平均值
        //查出所有的区
        addressList=getAllAddress();
        List<Map<String,Float>> areaList=new ArrayList<Map<String, Float>>();
        idList=getId();
            //根据省市
        for(Address address:idList){
            Map<String,String> addressRiskValue=craneInspectReportService.validateAddressRiskValueIsExistByAddressId(address.getId());
            if(addressRiskValue==null){
            }else{
                craneInspectReportService.deleteAreaRiskValue(Long.parseLong(String.valueOf(addressRiskValue.get("addressid"))));
            }
        }
        for(Address address:addressList){
            areaList=craneInspectReportService.getAreaInfoByCondition0(address.getProvince(),address.getCity(),"0","0","0",0f,0f);
            if(areaList!=null&&areaList.size()!=0){
            craneInspectReportService.batchInsertToAddressRiskValue(areaList);
            }
        }
        return JsonResultUtils.getCodeAndMesByStringAsDefault(JsonResultUtils.Code.SUCCESS);
    }
    public List<Address> getId(){
        return addressService.getId();
    }
    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    @POST
    @Path("/calculateCityRisk")
    public String calculateCityAreaRisk(){
        //通过区查出有多少unitAddress,然后根据每家unitAddress求出区域风险平均值
        //查出所有的区
        List<Map<String,Float>> cityList=new ArrayList<Map<String, Float>>();
        List<Address> addresses=getProvinceCity();
        for(Address address:addresses){
            Map<String,String> cityRiskValueMap=craneInspectReportService.validateCityRiskValueIsExistByProvinceAndCity(address.getProvince(),address.getCity());
            if(cityRiskValueMap==null){

            }else{
            craneInspectReportService.deleteCityRiskValue(cityRiskValueMap.get("province"), cityRiskValueMap.get("city"));
            }
            Map<String,Float> map=craneInspectReportService.getCityInfoByCondition0(address.getProvince(),address.getCity());
            cityList.add(map);
        }
        if(cityList!=null&&cityList.size()!=0){
            craneInspectReportService.batchInsertToCityRiskValue(cityList);
        }
        return JsonResultUtils.getCodeAndMesByStringAsDefault(JsonResultUtils.Code.SUCCESS);
    }
    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    @POST
    @Path("/deleteRiskData")
    public void deleteRiskData(){
        //删除计算值
        //drop craneInspectReportCollection
        craneInspectReportService.dropCraneInspectReportCollection();
        //删除
    }
    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    @POST
    @Path("/dumpDataToTempTable")
    public String dumpDataToTempTable(){
        //插入数据到三个临时表中
      /*  List<Map<String,String>> provinceRiskList=craneInspectReportService.selectProvinceRiskTemp();
        List<Map<String,String>> cityRiskList=craneInspectReportService.selectCityRiskTemp();
        List<Map<String,String>> areaRiskList=craneInspectReportService.selectAreaRiskTemp();*/
        int a=craneInspectReportService.dumpDataToProvinceRisk();
        if(a!=0){
            int b=craneInspectReportService.dumpDataToCityRisk();
            if(b!=0){
                int c=craneInspectReportService.dumpDataToAreaRisk();
                if(c!=0){
                    return JsonResultUtils.getCodeAndMesByStringAsDefault(JsonResultUtils.Code.SUCCESS);
                }
            }
        }
        return null;
    }
    @Produces(MediaType.APPLICATION_JSON+";charset=UTF-8")
    @POST
    @Path("/clearRiskTempTable")
    public String clearRiskTempTable(){
         //在复制数据的时候先清空三张临时表
        craneInspectReportService.deleteProvinceRiskTempTable();
        craneInspectReportService.deleteCityRiskTempTable();
        craneInspectReportService.deleteAreaRiskTempTable();
        return JsonResultUtils.getCodeAndMesByStringAsDefault(JsonResultUtils.Code.SUCCESS);
 }
    @Produces( MediaType.APPLICATION_JSON + ";charset=UTF-8")
    @Path("/updateUploadedReport")
    @POST
    public String updateUploadedReport(@FormParam("jsonString") String jsonString){
        Map<String,String> map= JsonMapper.buildNonDefaultMapper().fromJson(jsonString,Map.class);
        int result = craneInspectReportService.updateUpdatedReport(map);
        if(result>0){
            return JsonResultUtils.getCodeAndMesByStringAsDefault(JsonResultUtils.Code.SUCCESS);
        }else{
            return JsonResultUtils.getCodeAndMesByStringAsDefault(JsonResultUtils.Code.ERROR);
        }
    }
    @Produces( MediaType.APPLICATION_JSON+ ";charset=UTF-8")
    @Path("/deleteUploadedReport")
    @POST
    public String deleteUploadedReport(@FormParam("jsonString") String jsonString){
        Map<String,String> map = JsonMapper.buildNonDefaultMapper().fromJson(jsonString,Map.class);
        Long reportId=Long.parseLong(String.valueOf(map.get("id")));
        int deleted = craneInspectReportService.deleteByReportId(reportId);
        int result = craneInspectReportService.deleteUploadedReport(map);
        if(result>0&&deleted>=0){
            return JsonResultUtils.getCodeAndMesByStringAsDefault(JsonResultUtils.Code.SUCCESS);
        }else{
            return JsonResultUtils.getCodeAndMesByStringAsDefault(JsonResultUtils.Code.ERROR);
        }
    }
}
