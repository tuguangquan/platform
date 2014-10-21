(function($){
    $.URL = {
        "power":{
            "add":"rs/power/add",
            "update":"rs/power/update",
            "delete":"rs/power/delete",
            "list":"rs/power/list"
        },
        "user":{
            "add":"rs/user/add",
            "update":"rs/user/update",
            "delete":"rs/user/delete",
            "list":"rs/user/list",
            "getId":"rs/user/getIdByName" ,
            "currentUserId": "rs/user/currentUserId",
            "currentUserInfo":"rs/user/currentUser"
        },
        "authority":{
             "add":"rs/authority/add",
             "update":"rs/authority/update",
             "delete":"rs/authority/delete",
            "list":"rs/authority/list"
        },
        "userAuthority":{
              "add":"rs/userAuthority/add"
        },
        "device":{
            "add":"rs/device/add"
        },
        "customer":{
            "add":"rs/customer/add",
            "update":"rs/customer/update",
            "delete":"rs/customer/delete",
            "list":"rs/customer/list"
        },
        "installation":{
            "list":"rs/installation/list",
            "add":"rs/installation/add",
            "update":"rs/installation/update",
            "delete":"rs/installation/delete"
        },
        "department":{
            "add":"rs/department/add",
            "list":"rs/department/list",
            "update":"rs/department/update",
            "delete":"rs/department/delete"
        },
        "car_Driver":{
            "add":"rs/car_Driver/add",
            "list":"rs/car_Driver/list",
            "update":"rs/car_Driver/update",
            "delete":"rs/car_Driver/delete"
        },
        "device":{
            "add":"rs/device/add",
            "update":"rs/device/update",
            "delete":"rs/device/delete",
            "list":"rs/device/list",
            "getIdByNumber":"rs/device/getIdByNumber"
        },
        "deviceType":{
            "list":"rs/deviceType/list"
        },
        "stock_in_sheet":{
            "add":"rs/stock_in_sheet/add"
        },
        "stock_out_sheet":{
            "add" :"rs/stock_out_sheet/add" ,
            "list":"rs/stock_out_sheet/list" ,
            "delete": "rs/stock_out_sheet/delete" ,
            "update":"rs/stock_out_sheet/update"
        } ,
        "selfInspect":{
            "add":"rs/selfInspect/add" ,
            "update":"rs/selfInspect/update",
            "delete":"rs/selfInspect/delete" ,
            "list":"rs/selfInspect/list"
        },
        "remove":{
            "add":"rs/remove/add"  ,
            "update":"rs/remove/update",
            "delete":"rs/remove/delete" ,
            "list":"rs/remove/list"
        },
        "prebury":{
            "add":"rs/prebury/add"  ,
            "update":"rs/prebury/update",
            "delete":"rs/prebury/delete" ,
            "list":"rs/prebury/list"
        }
    }
})(jQuery);