package cn.kurisu.reactnativedemo.model;

import android.graphics.BitmapFactory;

import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.ext.ugc.TIMUGCCover;
import com.tencent.imsdk.ext.ugc.TIMUGCElem;
import com.tencent.imsdk.ext.ugc.TIMUGCVideo;

import java.io.File;

import cn.kurisu.reactnativedemo.MainApplication;
import cn.kurisu.reactnativedemo.R;

/**
 * 小视频消息
 */

public class UGCMessage extends Message {
    private static final String TAG = "UGCMessage";


    public UGCMessage(TIMMessage message) {
        this.message = message;
    }


    public UGCMessage(String filePath, String coverPath, long duration) {
        message = new TIMMessage();

        TIMUGCElem elem = new TIMUGCElem();
        TIMUGCCover cover = new TIMUGCCover();
        File file = new File(coverPath);
        int height = 0, width = 0;
        if (file.exists()) {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(coverPath, options);
            height = options.outHeight;
            width = options.outWidth;
        }
        cover.setHeight(height);
        cover.setWidth(width);
        cover.setType("PNG");
        TIMUGCVideo video = new TIMUGCVideo();
        video.setType("MP4");
        video.setDuration(duration);
        elem.setCover(cover);
        elem.setVideo(video);

        elem.setVideoPath(filePath);
        elem.setCoverPath(coverPath);
        message.addElement(elem);
    }


    /**
     * 获取消息摘要
     */
    @Override
    public String getSummary() {
        String str = getRevokeSummary();
        if (str != null) return str;
        return MainApplication.getContext().getString(R.string.summary_video);
    }

    /**
     * 保存消息或消息文件
     */
    @Override
    public void save() {

    }

    @Override
    public String getMsgType() {
        return "UGC";
    }

}
