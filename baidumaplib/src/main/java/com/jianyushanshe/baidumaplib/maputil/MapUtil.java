package com.jianyushanshe.baidumaplib.maputil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviParaOption;
import com.jianyushanshe.baidumaplib.maputil.ui.MapTranslateUtils;

import static com.jianyushanshe.baidumaplib.maputil.ui.MapTranslateUtils.GAODE_PACKNAME;
import static com.jianyushanshe.baidumaplib.maputil.ui.MapTranslateUtils.baidu2Gao;

import static com.jianyushanshe.baidumaplib.maputil.ui.MapTranslateUtils.isHuawei;
import static com.jianyushanshe.baidumaplib.maputil.ui.MapTranslateUtils.openNaviByIntent;
import static com.jianyushanshe.baidumaplib.maputil.ui.MapTranslateUtils.openNaviByWeb;


public class MapUtil {

    private static boolean isOpenedMap;//是否打开过地图

    /**
     * 百度导航
     *
     * @param startla
     * @param endla
     * @param startAdd
     * @param endAdd
     * @param context
     */
    public static void openBaiDuNavigation(LatLng startla, LatLng endla, String startAdd, String endAdd, final Activity context) {
        final NaviParaOption para = new NaviParaOption().startPoint(startla)
                .endPoint(endla).startName(startAdd)
                .endName(endAdd);
        //处理华为手机
        if (isHuawei()) {
            if (MapTranslateUtils.baiduIsInstalled(context)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //版本高于等于6.0
                    openNaviByIntent(context, para);
                    isOpenedMap = true;
                } else {
                    BaiduMapNavigation.openBaiduMapNavi(para, context);
                }
            } else {
                Toast.makeText(context, "当前未安装百度地图", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (MapTranslateUtils.baiduIsInstalled(context)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //版本高于等于6.0
                    if (isOpenedMap) {
                        BaiduMapNavigation.openBaiduMapNavi(para, context);
                    } else {
                        openNaviByIntent(context, para);
                        isOpenedMap = true;
                    }
                } else {
                    if (MapTranslateUtils.baiduIsInstalled(context)) { //打开百度地图
                        BaiduMapNavigation.openBaiduMapNavi(para, context);
                        return;
                    } else {
                        openNaviByIntent(context, para);
                        isOpenedMap = true;
                    }
                }
            } else {
                Toast.makeText(context, "当前未安装百度地图", Toast.LENGTH_SHORT).show();
            }
        }

    }


    /**
     * 打开高德导航
     *
     * @param startla
     * @param endla
     * @param startAdd
     * @param endAdd
     * @param context
     */
    public static void openGaoDeNavigation(LatLng startla, LatLng endla, String startAdd, String endAdd, final Activity context) {
        if (MapTranslateUtils.gaoDeIsInstalled(context)) { //打开高德地图
            startNativeGaode(context, String.valueOf(baidu2Gao(endla).latitude), String.valueOf(baidu2Gao(endla).longitude), endAdd);
        } else {
            Toast.makeText(context, "当前未安装高德地图", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 开始导航
     * 如果安装百度则用百度导航
     * 未安装百度安装了高德则用高德导航
     * 都未安装，用高德web导航
     *
     * @param startla
     * @param endla
     * @param startAdd
     * @param endAdd
     * @param context
     */
    public static void openNavigation(LatLng startla, LatLng endla, String startAdd, String endAdd, final Activity context) {
        final NaviParaOption para = new NaviParaOption().startPoint(startla)
                .endPoint(endla).startName(startAdd)
                .endName(endAdd);

        if (MapTranslateUtils.baiduIsInstalled(context)) {
            openNaviByIntent(context, para);
        } else if (MapTranslateUtils.gaoDeIsInstalled(context)) {
            startNativeGaode(context, String.valueOf(baidu2Gao(endla).latitude), String.valueOf(baidu2Gao(endla).longitude), endAdd);
        } else {
            openNaviByWeb(baidu2Gao(startla), baidu2Gao(endla), startAdd, endAdd, context);
        }


    }
//    public static void openNavigation(LatLng startla, LatLng endla, String startAdd, String endAdd, final Activity context) {
//        final NaviParaOption para = new NaviParaOption().startPoint(startla)
//                .endPoint(endla).startName(startAdd)
//                .endName(endAdd);
//        //处理华为手机
//        if (isHuawei()) {
//            if (MapTranslateUtils.baiduIsInstalled(context)) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //版本高于等于7.0
//                    openNaviByIntent(context, para);
//                    isOpenedMap = true;
//                } else {
//                    BaiduMapNavigation.openBaiduMapNavi(para, context);
//                }
//            } else {
//                if (MapTranslateUtils.gaoDeIsInstalled(context)) { //打开高德地图
//                    startNativeGaode(context, String.valueOf(baidu2Gao(endla).latitude), String.valueOf(baidu2Gao(endla).longitude), endAdd);
//                } else {
//                    openNaviByWeb(baidu2Gao(startla), baidu2Gao(endla), startAdd, endAdd, context);
//                    isOpenedMap = true;
//                }
//            }
//        } else {
//            if (MapTranslateUtils.baiduIsInstalled(context)) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //版本高于等于7.0
//                    if (isOpenedMap) {
//                        BaiduMapNavigation.openBaiduMapNavi(para, context);
//                    } else {
//                        openNaviByIntent(context, para);
//                        isOpenedMap = true;
//                    }
//                } else {
//                    if (MapTranslateUtils.baiduIsInstalled(context)) { //打开百度地图
//                        BaiduMapNavigation.openBaiduMapNavi(para, context);
//                        return;
//                    }
//
//                    if (MapTranslateUtils.gaoDeIsInstalled(context)) { //打开高德地图
//                        startNativeGaode(context, String.valueOf(baidu2Gao(endla).latitude), String.valueOf(baidu2Gao(endla).longitude), endAdd);
//                    }
//                }
//            } else {
//                if (MapTranslateUtils.gaoDeIsInstalled(context)) { //打开高德地图
//                    startNativeGaode(context, String.valueOf(baidu2Gao(endla).latitude), String.valueOf(baidu2Gao(endla).longitude), endAdd);
//                } else {
//                    openNaviByWeb(baidu2Gao(startla), baidu2Gao(endla), startAdd, endAdd, context);
//                    isOpenedMap = true;
//                }
//            }
//        }
//
//    }

    /**
     * 高德导航
     *
     * @param context
     * @param endLat
     * @param endLng
     * @param address
     */
    @SuppressLint("NewApi")
    private static void startNativeGaode(Context context, String endLat, String endLng, String address) {
        if (TextUtils.isEmpty(endLat) || TextUtils.isEmpty(endLng)) {
            return;
        }
        if (TextUtils.isEmpty(address)) {
            address = "目的地";
        }
        try {
            String uri = "androidamap://navi?sourceApplication=app"
                    .concat("&poiname=").concat(address)
                    .concat("&lat=")
                    .concat(endLat)
                    .concat("&lon=")
                    .concat(endLng)
                    .concat("&dev=1&style=2");
            Intent intent = new Intent("android.intent.action.VIEW",
                    android.net.Uri.parse(uri));
            intent.addCategory("android.intent.category.DEFAULT");
            intent.setPackage(GAODE_PACKNAME);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "地址解析错误", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 腾讯地图导航
     *
     * @param startla
     * @param endla
     * @param startAdd
     * @param endAdd
     * @param context
     */

    public static void openTxNavigation(LatLng startla, LatLng endla, String startAdd, String endAdd, final Activity context) {
        if (MapTranslateUtils.tengXunIsInstalled(context)) {
            double[] txNowLatLng = MapTranslateUtils.map_bd2hx(startla.latitude, startla.longitude);
            double[] txDesLatLng = MapTranslateUtils.map_bd2hx(endla.latitude, endla.longitude);
            String url = "http://apis.map.qq.com/uri/v1/routeplan?type=drive&from=" + startAdd + "&fromcoord=" + txNowLatLng[0] + "," + txNowLatLng[1] + "&to=" + endAdd + "&tocoord=" + txDesLatLng[0] + "," + txDesLatLng[1] + "&policy=1&referer=myapp";

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            context.startActivity(intent);
        } else {
            Toast.makeText(context, "腾讯地图未安装", Toast.LENGTH_SHORT).show();
        }

    }

}
