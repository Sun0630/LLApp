package com.umeng.soexample.net;

import android.text.TextUtils;

import com.android.volley.Request;
import com.umeng.soexample.App;
import com.umeng.soexample.net.entity.Response;
import com.umeng.soexample.net.entity.ResponseList;
import com.umeng.soexample.net.entity.ResponseVo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NetApi implements Api {
    /**
     * 附加参数   暂时添加测试
     *
     * @param request 请求对象
     * @param map  请求参数集合
     * @param <T>     类型
     */
  /*  private static <T> void attachParams1(BaseRequest<T> request, Map<String, String> map) {
       if(map!=null){
//           String json = (String) StringUtils.toJson(map).get("Json");
           request.addParams(map);
       }
    }*/

    /**
     * 附加参数
     *
     * @param request 请求对象
     * @param params  请求参数
     * @param <T>     类型
     */
    private static <T> void attachParams(BaseRequest<T> request, String[]... params) {
        if (params != null && params.length > 0) {
            for (String[] param : params) {
                if (param[0] != null && param[1] != null) {
                    request.addParam(param[0], param[1]);
                }
            }
        }
    }

    /**
     * 转换成参数对象
     *
     * @param object 参数来源
     * @return 转换后参数
     */
    private static String[][] parseToParams(Object object) {
        if (object == null) {
            return null;
        }
        List<String[]> listParams = new ArrayList<>();
        Class<?> cls = object.getClass();
        while (cls != null && !cls.isAssignableFrom(Object.class)) {
            for (Field field : object.getClass().getDeclaredFields()) {
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                try {
                    Object objectValue = field.get(object);
                    if (objectValue == null) {
                        continue;
                    }
                    String value;
                    if (objectValue instanceof Date) {
                        value = String.valueOf(((Date) objectValue).getTime());
                    } else {
                        value = String.valueOf(objectValue);
                    }
                    String[] param = new String[]{field.getName(), value};
                    listParams.add(param);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            cls = cls.getSuperclass();
        }
        return listParams.toArray(new String[listParams.size()][2]);
    }

   /* public static <T> void executeBeanRequest1(String url, HashMap<String,Object>map,ResponseListener<ResponseVo<T>> listener) {
        BeanRequest<T> request = new BeanRequest<>(url, listener);
        attachParams1(request, map);
        App.getInstance().getRequestQueue().add(request);
    }*/
    /**
     * 执行请求
     *
     * @param url      地址
     * @param listener 回调
     * @param params   参数
     */
    public static <T> void executeListRequest(String url, ResponseListener<ResponseList<T>> listener, String[]... params) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        ListRequest<T> request = new ListRequest<>(url, listener);
        attachParams(request, params);
        App.getInstance().getRequestQueue().add(request);
    }

    public static <T> void executeListRequestGet(String url, ResponseListener<ResponseList<T>> listener) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        ListRequest<T> request = new ListRequest<>(Request.Method.GET, url, listener);
//        attachParams(request, params);
        App.getInstance().getRequestQueue().add(request);
    }

    /**
     * 执行请求
     *
     * @param url      地址
     * @param listener 回调
     * @param params   参数
     */
    public static <T> void executeBeanRequest(String url, ResponseListener<ResponseVo<T>> listener, String[]... params) {
        BeanRequest<T> request = new BeanRequest<>(url, listener);
        attachParams(request, params);
        App.getInstance().getRequestQueue().add(request);
    }

    /**
     * 执行请求
     *
     * @param url      地址
     * @param listener 回调
     * @param params   参数
     */
    public static void executeSimpleRequest(String url, ResponseListener<Response> listener, String[]... params) {
        SimpleRequest request = new SimpleRequest(url, listener);
        attachParams(request, params);
        App.getInstance().getRequestQueue().add(request);
    }

//    /**
//     * 执行请求
//     *
//     * @param url      地址
//     * @param listener 回调
//     * @param params   参数
//     */
//    public static void executeFileRequest(String url, FileListener listener, String[]... params) {
//        FileRequest request = new FileRequest(url, listener);
//        attachParams(request, params);
//        AndroidApp.getInstance().getRequestQueue().add(request);
//    }


    private static String getUrl(String path) {
        return path;
    }

    public static class Media {

        /**
         * 获取广告
         */
        public static <T> void getAdImage(String baoming, String app_platform, String ad_type, ResponseListener<ResponseList<T>> paramResponseListener) {
            executeListRequest("http://admin.ad.e-toys.cn/" + "api/ad/getad",
                               paramResponseListener, new String[][]{{"baoming", baoming},
                                                                     {"app_platform", app_platform}, {"ad_type", String.valueOf(ad_type)}});
        }

        //商品及广告轮播   接口
        public static <T> void getGoodsList(ResponseListener<ResponseList<T>> paramResponseListener) {
            NetApi.executeListRequestGet("http://www.e-toys.cn/mobile/apiIndex", paramResponseListener);
        }

        //判断该设备是否被绑定过
        public static void checkBind(String short_id, ResponseListener<Response> paramResponseListener) {
            NetApi.executeSimpleRequest(NetApi.getUrl("api/user/checkDevice"), paramResponseListener, new String[][]{{"short_id", short_id}});
        }

        //验证手机号码
        public static void checkMobile(String mobile, ResponseListener<Response> paramResponseListener) {
            NetApi.executeSimpleRequest(NetApi.getUrl("api/user/checkMobile"), paramResponseListener, new String[][]{{"mobile", mobile}});
        }

        //验证码验证
        public static void verifyCode(String mobile, String code, String type, ResponseListener<Response> paramResponseListener) {
            NetApi.executeSimpleRequest(NetApi.getUrl("api/user/verifyCode"), paramResponseListener, new String[][]{{"mobile", mobile}, {"code", code}, {"type", type}});
        }

        //资源删除
        public static void deleteMedia(String token, String media_id, ResponseListener<Response> paramResponseListener) {
            NetApi.executeSimpleRequest(NetApi.getUrl("api/device/deleteMedia"), paramResponseListener, new String[][]{{"token", token}, {"media_id", media_id}});
        }

        //资源共享
        public static void shareMedia(String token, String media_id, String enable, ResponseListener<Response> paramResponseListener) {
            NetApi.executeSimpleRequest(NetApi.getUrl("api/device/shareMedia"), paramResponseListener, new String[][]{{"token", token}, {"media_id", media_id}, {"enable", enable}});
        }

        //设备消息列表
        public static <T> void messages(String token, String client_id, String time, int page, ResponseListener<ResponseList<T>> paramResponseListener) {
            NetApi.executeListRequest(NetApi.getUrl("api/device/messages"), paramResponseListener, new String[][]{{"token", token}, {"client_id", client_id}, {"time", time}, {"page", String.valueOf(page)}});
        }

        //修改密码

        //用户连上设备
        public static void connect(String token, String client_id, ResponseListener<Response> paramResponseListener) {
            NetApi.executeSimpleRequest(NetApi.getUrl("api/device/connect"), paramResponseListener, new String[][]{{"token", token}, {"client_id", client_id}});
        }

        //用户断开设备
        public static <T> void disconnect(String token, String client_id, ResponseListener<Response> paramResponseListener) {
            NetApi.executeSimpleRequest(NetApi.getUrl("api/device/disconnect"), paramResponseListener, new String[][]{{"token", token}, {"client_id", client_id}});
        }

        //获取设备相册
        public static <T> void getDevicePhotos(String token, String client_id, String time, int page, ResponseListener<ResponseList<T>> paramResponseListener) {
            NetApi.executeListRequest(NetApi.getUrl("api/device/gallery"), paramResponseListener, new String[][]{{"token", token}, {"client_id", client_id}, {"time", time}, {"page", String.valueOf(page)}});
        }
        //初始化设备

        //重置密码
        public static void resetPassword(String mobile, String code, String pwd, ResponseListener<Response> paramResponseListener) {
            NetApi.executeSimpleRequest(NetApi.getUrl("api/user/resetPassword"), paramResponseListener, new String[][]{{"mobile", mobile}, {"code", code}, {"pwd", pwd}});
        }

        //删除成员
        public static void deleteMember(String token, String member_id, ResponseListener<Response> paramResponseListener) {
            NetApi.executeSimpleRequest(NetApi.getUrl("api/user/deleteMember"), paramResponseListener, new String[][]{{"token", token}, {"member_id", member_id}});
        }

        //我的设备列表（指我所能控制的所有设备的列表）
        public static <T> void MyDevices(String token, ResponseListener<ResponseList<T>> paramResponseListener) {
            NetApi.executeListRequest(NetApi.getUrl("api/user/devices"), paramResponseListener, new String[][]{{"token", token}});
        }

        //设备成员列表 （指能控制当前设备的所有成员列表）
        public static <T> void deviceMembers(String token, String client_id, ResponseListener<ResponseList<T>> paramResponseListener) {
            NetApi.executeListRequest(NetApi.getUrl("api/user/deviceMembers"), paramResponseListener, new String[][]{{"token", token}, {"client_id", client_id}});
        }

        //添加设备成员
        public static void addMember(String token, String client_id, String mobile, String name, String control, String photo, String video, ResponseListener<Response> paramResponseListener) {
            NetApi.executeSimpleRequest(NetApi.getUrl("api/user/addMember"), paramResponseListener,
                                        new String[][]{{"token", token}, {"client_id", client_id}, {"mobile", mobile}, {"name", name}, {"control", control}, {"photo", photo}, {"video", video}});
        }

        //修改设备成员
        public static void modifyMember(String token, String member_id, String name, String control, String photo, String video, ResponseListener<Response> paramResponseListener) {
            NetApi.executeSimpleRequest(NetApi.getUrl("api/user/modifyMember"), paramResponseListener,
                                        new String[][]{{"token", token}, {"member_id", member_id}, {"name", name}, {"control", control}, {"photo", photo}, {"video", video}});
        }

        //修改密码
        public static void changePassword(String token, String old_pwd, String pwd, ResponseListener<Response> paramResponseListener) {
            NetApi.executeSimpleRequest(NetApi.getUrl("api/user/changePassword"), paramResponseListener, new String[][]{{"token", token}, {"old_pwd", old_pwd}, {"pwd", pwd}});
        }

        //绑定设备
        public static void bindDevice(String token, String short_id, String device_name, String name, ResponseListener<Response> paramResponseListener) {
            NetApi.executeSimpleRequest(NetApi.getUrl("api/user/bindDevice"), paramResponseListener, new String[][]{{"token", token}, {"short_id", short_id}, {"device_name", device_name}, {"name", name}});
        }

        //解除绑定设备
        public static void unBindDevice(String token, String client_id, ResponseListener<Response> paramResponseListener) {
            NetApi.executeSimpleRequest(NetApi.getUrl("api/user/unBind"), paramResponseListener, new String[][]{{"token", token}, {"client_id", client_id}});
        }

        //修改个人资料
        public static void modifyInfo(String token, String info, ResponseListener<Response> paramResponseListener) {
            NetApi.executeSimpleRequest(NetApi.getUrl("api/user/saveInfo"), paramResponseListener, new String[][]{{"token", token}, {"info", info}});
        }

//        //修改个人资料(头像)
//        public static void modifyInfo(String token, String info, FileListener paramFileListener) {
//            NetApi.executeFileRequest(NetApi.getUrl("api/user/saveInfo"), paramFileListener, new String[][]{{"token", token}, {"info", info}});
//        }
//
//        //修改设备名称
//        public static void modifyDeviceName(String token, String client_id, String device_name, ResponseListener<Response> paramResponseListener) {
//            NetApi.executeSimpleRequest(NetApi.getUrl("api/user/modifyDevice"), paramResponseListener, new String[][]{{"token", token}, {"client_id", client_id}, {"device_name", device_name}});
//        }
//
//        //修改设备信息（头像）
//        public static void modifyDevice(String token, String client_id, String device_name, String device_header, FileListener paramFileListener) {
//            NetApi.executeFileRequest(NetApi.getUrl("api/user/modifyDevice"), paramFileListener, new String[][]{{"token", token}, {"client_id", client_id}, {"device_name", device_name}, {"device_header", device_header}});
//        }

        //注册
        public static <T> void regist(String mobile, String code, String pwd, ResponseListener<ResponseVo<T>> paramResponseListener) {
            NetApi.executeBeanRequest(NetApi.getUrl("api/user/register"), paramResponseListener, new String[][]{{"mobile", mobile}, {"code", code}, {"pwd", pwd}});
        }

        //登陆
        public static <T> void Login(String mobile, String password, ResponseListener<ResponseVo<T>> paramResponseListener) {
            NetApi.executeBeanRequest(NetApi.getUrl("api/user/loginByMobile"), paramResponseListener, new String[][]{{"mobile", mobile}, {"password", password}});
        }

        //发送验证码
        public static <T> void sendCode(String mobile, String type, ResponseListener<ResponseVo<T>> paramResponseListener) {
            NetApi.executeBeanRequest(NetApi.getUrl("api/user/sendCode"), paramResponseListener, new String[][]{{"mobile", mobile}, {"type", type}});
        }
       /* public static <T> void allPost(String method, HashMap<String, Object> map,ResponseListener<ResponseVo<T>>paramResponseListener){
            NetApi.executeBeanRequest1("http://media.e-toys.cn/api/media/"+method,map, paramResponseListener);
        }*/

        public static <T> void getBean(String name, String psw, ResponseListener<ResponseVo<T>> paramResponseListener) {
            NetApi.executeBeanRequest("http://58.220.16.170/passport/login", paramResponseListener, new String[][]{{"username", name}, {"password", psw}});
        }

        public static void doAction(int paramInt, long paramLong, ResponseListener<Response> paramResponseListener) {
            NetApi.executeSimpleRequest(NetApi.getUrl("api/media/doAction"), paramResponseListener, new String[][]{{"action", String.valueOf(paramInt)}, {"device_id", String.valueOf(paramLong)}});
        }

        public static <T> void getDevices(ResponseListener<ResponseList<T>> paramResponseListener) {
            NetApi.executeListRequest(NetApi.getUrl("api/media/devices"), paramResponseListener);
        }

        public static <T> void getSongs(ResponseListener<ResponseList<T>> paramResponseListener) {
            NetApi.executeListRequest(NetApi.getUrl("api/media/songs"), paramResponseListener);
        }

        public static void nextSong(long paramLong, ResponseListener<Response> paramResponseListener) {
            NetApi.executeSimpleRequest(NetApi.getUrl("api/media/nextSong"), paramResponseListener, new String[][]{{"device_id", String.valueOf(paramLong)}});
        }

        public static void playSong(long paramLong1, long paramLong2, ResponseListener<Response> paramResponseListener) {
            NetApi.executeSimpleRequest(NetApi.getUrl("api/media/playSong"), paramResponseListener, new String[][]{{"song_id", String.valueOf(paramLong1)}, {"device_id", String.valueOf(paramLong2)}});
        }

//        public static void playVoice(long paramLong, FileListener paramFileListener) {
//            NetApi.executeFileRequest(NetApi.getUrl("api/media/playVoice"), paramFileListener, new String[][]{{"device_id", String.valueOf(paramLong)}});
//        }
//
//        public static void showImage(long paramLong, FileListener paramFileListener) {
//            NetApi.executeFileRequest(NetApi.getUrl("api/media/showImage"), paramFileListener, new String[][]{{"device_id", String.valueOf(paramLong)}});
//        }
//
//        public static void stopSong(long paramLong, ResponseListener<Response> paramResponseListener) {
//            NetApi.executeSimpleRequest(NetApi.getUrl("api/media/stopSong"), paramResponseListener, new String[][]{{"device_id", String.valueOf(paramLong)}});
//        }
//
//        public static void upImage(String portrait, String token, FileListener paramFileListener) {
//            NetApi.executeFileRequest(NetApi.getUrl("api/media/showImage"), paramFileListener, new String[][]{{"portrait", portrait}, {"token", token}});
//        }

//	    /**
//         * 上传多媒体文件，目前只支持声音或图片
//         * @param clientId 设备ID
//         * @param token 登录令牌
//         * @param type 类型（picture,voice）
//         * @param content 消息内容（传递什么内容，message_content 就为此内容）
//         * @param fileName 文件名称
//         * @param fileListener 文件监听器
//	     */
//        public static void uploadMedia(String clientId, String token, String type, String content, String fileName, FileListener fileListener) {
//            NetApi.executeFileRequest(NetApi.getUrl("api/device/uploadMedia"), fileListener, new String[][]{{"client_id", clientId}, {"token", token}, {"type", type}, {"content", content}, {"file_name", fileName}});
//        }
    }
}