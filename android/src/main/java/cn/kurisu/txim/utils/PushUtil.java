package cn.kurisu.txim.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import cn.kurisu.txim.R;

/**
 * 在线消息通知展示
 */
public class PushUtil {

    private static final String TAG = PushUtil.class.getSimpleName();

    private static int pushNum = 0;

    private final int pushId = 1;

    private NotificationManager mNotificationManager;

    private static PushUtil instance = new PushUtil();

    public static PushUtil getInstance() {
        return instance;
    }


    public void PushNotify(String senderStr, String contentStr) {
        mNotificationManager = (NotificationManager) IMApplication.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        String chancelId;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            chancelId = createNotificationChannel(TAG, "后台通知");
        } else {
            chancelId="";
        }
        //系统消息，自己发的消息，程序在前台的时候不通知
        if (Foreground.get().isForeground()) {
            return;
        }
        Log.d(TAG, "recv msg " + contentStr);
        NotificationManager mNotificationManager = (NotificationManager) IMApplication.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(IMApplication.getContext(),  chancelId);
        Intent notificationIntent = new Intent(IMApplication.getContext(), IMApplication.getMainActivityClass());
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent = PendingIntent.getActivity(IMApplication.getContext(), 0, notificationIntent, 0);
        mBuilder.setContentTitle(senderStr)//设置通知栏标题
                .setContentText(contentStr)
                .setContentIntent(intent) //设置通知栏单击意图
                .setNumber(++pushNum) //设置通知集合的数量
                .setTicker(senderStr+":"+contentStr) //通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                .setDefaults(Notification.DEFAULT_ALL)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用 defaults 属性，可以组合
                .setSmallIcon(R.drawable.fw_ic_launcher);//设置通知小 ICON
        Notification notify = mBuilder.build();
        notify.flags |= Notification.FLAG_AUTO_CANCEL;
        mNotificationManager.notify(pushId, notify);
    }

    public static void resetPushNum() {
        pushNum = 0;
    }

    public void reset() {
        NotificationManager notificationManager = (NotificationManager) IMApplication.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(pushId);
    }

    @RequiresApi(Build.VERSION_CODES.O)
    public String createNotificationChannel(String chancelId,String channelName){
        NotificationChannel chan = new NotificationChannel(chancelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
        chan.setBypassDnd(true);//设置可以绕过请勿打扰模式
        chan.canBypassDnd();//可否绕过请勿打扰模式
        //锁屏显示通知
        chan.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
        chan.shouldShowLights();//是否会闪光
        chan.enableLights(true);//闪光
        //指定闪光时的灯光颜色，为了兼容低版本在上面builder上通过setLights方法设置了
        chan.setLightColor(Color.BLUE);

        chan.canShowBadge();//桌面launcher消息角标
        chan.enableVibration(true);//是否允许震动
        //震动模式，第一次100ms，第二次100ms，第三次200ms，为了兼容低版本在上面builder上设置了
        //channel.setVibrationPattern(new long[]{100,100,200});
        chan.getAudioAttributes();//获取系统通知响铃声音的配置
        chan.getGroup();//获取通知渠道组
        //绑定通知渠道
        mNotificationManager.createNotificationChannel(chan);
        return chancelId;
    }

}
