(function($){
    $.URL = {
        "common":{
          "rootPath":"http://localhost/"
        },
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
        "deviceData":{
            "getDeviceData":"rs/deviceData/getDeviceData"
        },
        "device":{
            "list":"rs/device/list",
            "add":"rs/device/add",
            "update":"rs/device/update",
            "delete":"rs/device/delete"
        },
        "deviceType":{
            "list":"rs/deviceType/list",
            "add":"rs/deviceType/add",
            "update":"rs/deviceType/update",
            "delete":"rs/deviceType/delete"
        },
        "websocket":{
            "register":"ws://localhost:8080/trackSystem/websocket/hello"
        }
    }
})(jQuery);