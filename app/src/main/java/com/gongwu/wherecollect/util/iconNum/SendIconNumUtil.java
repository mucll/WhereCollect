package com.gongwu.wherecollect.util.iconNum;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建时间： 2019/3/12 0012.
 * 创建人：  yanbin
 * 功能：
 */

public class SendIconNumUtil {

    private static IconBadgeNumManager setIconBadgeNumManager = new IconBadgeNumManager();
    private static NotificationManager nm, customNm;
    private static List<String> mData = new ArrayList<>();
    private static List<String> mCustomData = new ArrayList<>();

    /**
     * 设置角标（不带出现通知栏）
     *
     * @param i       角标数字（为0时清空角标）
     * @param context 上下文
     */
    public static void sendIconNumNotification(int i, Application context) {
        nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm == null) return;
        String notificationChannelId = null;

        Notification notification = null;
        try {
            notification = new NotificationCompat.Builder(context, notificationChannelId)
                    .setAutoCancel(true)
                    .build();
            notification = setIconBadgeNumManager.setIconBadgeNum(context, notification, i);
            nm.notify(0, notification);
//            nm.notify();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置角标并且带通知栏
     *
     * @param i             角标数字（为0时清空角标）
     * @param context       上下文
     * @param notifyID      通知id(类似于主键 如果通知id相同不管多少通知都只会显示最新一条)
     * @param pendingIntent 跳转事件
     * @param title         标题
     * @param channelId     通知栏 id
     * @param SmallIcon     图标
     * @param Ticker
     * @param ContentText   通知内容
     * @param channeName    通知渠道设置（区分通知类型默认为其他）
     */
    public static void sendIconNumNotification(int i, Application context, int notifyID, PendingIntent pendingIntent, String title, String channelId, int SmallIcon
            , String Ticker, String ContentText, String channeName) {
        nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm == null) return;
        Notification notification = null;
        String notificationChannelId = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = createNotificationChannel(channelId, channeName);
            nm.createNotificationChannel(notificationChannel);
            notificationChannelId = notificationChannel.getId();
            mData.add(notifyID + "");

        }
        notification = new NotificationCompat.Builder(context, notificationChannelId)
                .setSmallIcon(context.getApplicationInfo().icon)
                .setWhen(System.currentTimeMillis())
                .setTicker(Ticker)
                .setContentTitle(title)
                .setContentText(ContentText)
                .setSmallIcon(SmallIcon)
                .setDefaults(Notification.DEFAULT_ALL)
                .setLights(Color.YELLOW, 300, 0)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();
        try {
            notification = setIconBadgeNumManager.setIconBadgeNum(context, notification, i);
            nm.notify(notifyID, notification);
        } catch (Exception e) {
            if (e.toString().equals(""))
                Log.d("SendIconNumUtil", e.toString());
        }
    }

    /**
     * 设置角标并且带通知栏(不设置通知栏渠道 默认为渠道为：其他   其他类型的通道ID固定为otherTypes)
     *
     * @param i             角标数字（为0时清空角标）
     * @param context       上下文
     * @param notifyID      通知id
     * @param pendingIntent 跳转事件
     * @param title         标题
     *                      //     * @param channelId     通知栏 id
     * @param SmallIcon     图标
     * @param Ticker
     * @param ContentText   通知内容
     */
    public static void sendIconNumNotification(int i, Application context, int notifyID, PendingIntent pendingIntent, String title, int SmallIcon
            , String Ticker, String ContentText) {
        nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm == null) return;
        Notification notification = null;
        String notificationChannelId = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel =
                    createNotificationChannel("otherTypes", "");
            nm.createNotificationChannel(notificationChannel);
            notificationChannelId = notificationChannel.getId();
            mData.add(notifyID + "");

        }
        notification = new NotificationCompat.Builder(context, notificationChannelId)
                .setSmallIcon(context.getApplicationInfo().icon)
                .setWhen(System.currentTimeMillis())
                .setTicker(Ticker)
                .setContentTitle(title)
                .setContentText(ContentText)
                .setSmallIcon(SmallIcon)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();
        try {
            notification = setIconBadgeNumManager.setIconBadgeNum(context, notification, i);
            nm.notify(notifyID, notification);
        } catch (Exception e) {
            if (e.toString().equals(""))
                Log.d("SendIconNumUtil", e.toString());
        }
    }

    /**
     * 使用自定义通知栏
     *
     * @param i            角标显示数量
     * @param context      上下文
     * @param notifyID     通知id
     * @param notification 自定义通知栏
     * @param nm           通知栏管理器
     */
    public static void sendIconNumNotification(int i, Application context, int notifyID, Notification notification, NotificationManager nm) {

        try {
            notification = setIconBadgeNumManager.setIconBadgeNum(context, notification, i);
            nm.notify(notifyID, notification);
            customNm = nm;
            mCustomData.add(notifyID + "");
        } catch (Exception e) {
            Log.d("SendIconNumUtil", e.toString());
        }
    }


    //清除通知栏消息(默认的 更具传入消息id清除)
    public static void clearIconNumNotifucation(int notifyId) {
        nm.cancel(notifyId);
    }

    //清除自定义消息的
    public static void clearCustomIconNumNotifucation(int notifyId) {
        customNm.cancel(notifyId);
    }

    //清除所有的
    public static void clearAllIconNumNotifucation() {
        int notifyid, notifyAllId;
        if (mData.size() > 0 && mData != null) {
            for (int i = 0; i < mData.size(); i++) {
                notifyid = Integer.parseInt(mData.get(i));
                nm.cancel(notifyid);
            }
        }
        if (mCustomData.size() > 0 && mCustomData != null)
            for (int i = 0; i < mCustomData.size(); i++) {
                notifyAllId = Integer.parseInt(mCustomData.get(i));
                customNm.cancel(notifyAllId);
            }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private static NotificationChannel createNotificationChannel(String channelId, String channelName) {
        NotificationChannel channel = null;
        if (channelName.equals("")) {
            //第三个参数为通知等级   默认渠道为为两级有自定义渠道名的为三级（该等级用户可自动调控 没必要的话不需要调整）]
            channel = new NotificationChannel(channelId, "其他", NotificationManager.IMPORTANCE_LOW);
        } else {
            channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
        }


        Log.d("haha", channelName.equals("") ? "其他" : channelName);
        // channel.canBypassDnd();//是否绕过勿扰模式
        //  channel.setBypassDnd(true);
        channel.enableLights(true); //是否在桌面icon右上角展示小红点
        channel.setLightColor(Color.RED); //小红点颜色
        channel.setShowBadge(true); //是否在久按桌面图标时显示此渠道的通知
        return channel;
    }

    public static void init(final Application application) {
        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {
            }

            @Override
            public void onActivityStarted(Activity activity) {

                //不带通知栏的
                SendIconNumUtil.sendIconNumNotification(0, application);
                //测试修改
            }

            @Override
            public void onActivityResumed(Activity activity) {
            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }

//    @Override
//    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
//
//    }
//
//    @Override
//    public void onActivityStarted(Activity activity) {
//
//    }
//
//    @Override
//    public void onActivityResumed(Activity activity) {
//
//    }
//
//    @Override
//    public void onActivityPaused(Activity activity) {
//
//    }
//
//    @Override
//    public void onActivityStopped(Activity activity) {
//
//    }
//
//    @Override
//    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
//
//    }
//
//    @Override
//    public void onActivityDestroyed(Activity activity) {
//
//    }


}
