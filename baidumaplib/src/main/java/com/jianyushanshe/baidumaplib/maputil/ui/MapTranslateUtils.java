package com.jianyushanshe.baidumaplib.maputil.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviParaOption;

import java.io.File;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Author:wangjianming
 * Time:2019/5/30
 * Description:map检测工具
 */
public class MapTranslateUtils {

    public static String BAIDU_PACKNAME = "com.baidu.BaiduMap";//百度地图包名
    public static String GAODE_PACKNAME = "com.autonavi.minimap";//高德地图包名
    public static String TENGXUN_PACKNAME = "com.tencent.map";//腾讯地图包名


    public static boolean isBaduInstalled; //百度地图是否已安装
    public static boolean isGaodeInstalled; //高德地图是否已安装
    public static boolean isTengxunInstalled; //腾讯地图是否已安装

    static {
        checkMapInastlled();
    }


    private static void checkMapInastlled() {
        isBaduInstalled = isInstallByread(BAIDU_PACKNAME);
        isGaodeInstalled = isInstallByread(GAODE_PACKNAME);
        isTengxunInstalled = isInstallByread(TENGXUN_PACKNAME);
    }

    /**
     * 判断是否安装目标应用
     *
     * @param packageName 目标应用安装后的包名
     * @return 是否已安装目标应用
     */
    public static boolean isInstallByread(String packageName) {
        return new File("/data/data/".concat(packageName)).exists();
    }

    /**
     * 方法描述：判断是否为华为手机
     * <p>
     *
     * @param
     * @return
     */
    public static boolean isHuawei() {
        String bland = android.os.Build.MANUFACTURER;
        return !TextUtils.isEmpty(bland) && (bland.contains("HUAWEI") || bland.contains("华为") || bland.contains("huawei"));
    }

    /**
     * 方法描述： 是否安装了浏览器
     * <p>
     *
     * @param
     * @return
     */
    @SuppressWarnings("WrongConstant")
    public static boolean hasBrowser(Context context) {
        PackageManager pm = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse("http://"));

        List<ResolveInfo> list = pm.queryIntentActivities(intent,
                PackageManager.GET_INTENT_FILTERS);
        final int size = (list == null) ? 0 : list.size();
        return size > 0;
    }


    /**
     * 方法描述：打开浏览器
     * <p>
     *
     * @param
     * @return
     */

    public static void openNaviByWeb(Activity context, NaviParaOption para) {
        if (hasBrowser(context)) {
            BaiduMapNavigation.setSupportWebNavi(true);
            BaiduMapNavigation.openBaiduMapNavi(para, context); //打开网页导航
        } else {
            Toast.makeText(context, "抱歉，未检测到浏览器无法开启导航。", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 方法描述：打开浏览器
     *
     * @param
     * @return
     */
    public static void openNaviByIntent(Activity context, NaviParaOption para) {
        //调起百度地图客户端
        Intent intent;
        try {
            //结构说明地址 ：http://developer.baidu.com/map/uri-introandroid.htm
            String url = addString("intent://map/direction?origin=latlng:",
                    String.valueOf(para.getStartPoint().latitude), ",",
                    String.valueOf(para.getStartPoint().longitude), "|name:", para.getStartName(),
                    "&destination=latlng:",
                    String.valueOf(para.getEndPoint().latitude), ",",
                    String.valueOf(para.getEndPoint().longitude), "|name:", para.getEndName(),
                    "&mode=driving#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end) ");
//                            intent = Intent.getIntent("intent://map/direction?origin=latlng:34.264642646862,108.95108518068|name:我家&longitude:34.264642646862,108.95108518068&mode=driving®ion=西安&referer=Autohome|GasStation#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end");
            Log.e("map", url);
            intent = Intent.getIntent(url);
            context.startActivity(intent); //启动调用
//                Log.e("GasStation", "百度地图客户端已经安装");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "未知错误", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 方法描述：拼接字符串
     * <p>
     *
     * @param strs 字符串的可变集合
     * @return
     */
    private static String addString(String... strs) {
        String lastString = "";
        if (strs != null) {
            if (strs.length == 1) {
                return strs[0];
            } else {
                for (String str : strs) {
                    lastString = lastString.concat(str);
                }
            }
        }
        return lastString;
    }


    /**
     * 方法描述：将百度定位转为高德定位
     * <p>
     *
     * @param latLng 百度经纬度
     * @return
     */
    public static LatLng baidu2Gao(LatLng latLng) {

        double x_pi = 3.14159265358979324 * 3000.0 / 180.0;
        double x = latLng.longitude - 0.0065, y = latLng.latitude - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_pi);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi);
        LatLng newLatLng = new LatLng(z * Math.sin(theta) + 0.0030, z * Math.cos(theta) - 0.0049);
        return newLatLng;
    }

    public static LatLng convertBaiduToGPS(LatLng sourceLatLng) {
        // 将GPS设备采集的原始GPS坐标转换成百度坐标
        com.baidu.mapapi.utils.CoordinateConverter converter = new com.baidu.mapapi.utils.CoordinateConverter();
        converter.from(com.baidu.mapapi.utils.CoordinateConverter.CoordType.GPS);
        // sourceLatLng待转换坐标
        converter.coord(sourceLatLng);
        LatLng desLatLng = converter.convert();
        double latitude = 2 * sourceLatLng.latitude - desLatLng.latitude;
        double longitude = 2 * sourceLatLng.longitude - desLatLng.longitude;
        BigDecimal bdLatitude = new BigDecimal(latitude);
        bdLatitude = bdLatitude.setScale(6, BigDecimal.ROUND_HALF_UP);
        BigDecimal bdLongitude = new BigDecimal(longitude);
        bdLongitude = bdLongitude.setScale(6, BigDecimal.ROUND_HALF_UP);
        return new LatLng(bdLatitude.doubleValue(), bdLongitude.doubleValue());
    }

    /**
     * 坐标转换，百度地图坐标转换成腾讯地图坐标
     *
     * @param lat 百度坐标纬度
     * @param lon 百度坐标经度
     * @return 返回结果：纬度,经度
     */
    private static String baidu2Tx(double lat, double lon) {
        double tx_lat;
        double tx_lon;
        double x_pi = 3.14159265358979324;
        double x = lon - 0.0065, y = lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_pi);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi);
        tx_lon = z * Math.cos(theta);
        tx_lat = z * Math.sin(theta);
        return tx_lat + "," + tx_lon;
    }

    /**
     * 坐标转换，百度地图坐标转换成腾讯地图坐标 * @param lat 百度坐标纬度 * @param lon 百度坐标经度 * @return 返回结果：纬度,经度
     */
    public static double[] map_bd2hx(double lat, double lon) {
        double tx_lat;
        double tx_lon;
        double x_pi = 3.14159265358979324;
        double x = lon - 0.0065, y = lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_pi);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi);
        tx_lon = z * Math.cos(theta);
        tx_lat = z * Math.sin(theta);
        double[] doubles = new double[]{tx_lat, tx_lon};
        return doubles;
    }


}
