package cn.kurisu.reactnativedemo.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;

import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMSnapshot;
import com.tencent.imsdk.TIMVideo;
import com.tencent.imsdk.TIMVideoElem;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.kurisu.reactnativedemo.MainApplication;
import cn.kurisu.reactnativedemo.R;
import cn.kurisu.reactnativedemo.utils.FileUtil;
import cn.kurisu.reactnativedemo.utils.MediaUtil;

/**
 * 小视频消息数据
 */
public class VideoMessage extends Message {

    private static final String TAG = "VideoMessage";


    public VideoMessage(TIMMessage message) {
        this.message = message;
    }

    public VideoMessage(String fileName) {
        message = new TIMMessage();
        TIMVideoElem elem = new TIMVideoElem();
        elem.setVideoPath(FileUtil.getCacheFilePath(fileName));
        Bitmap thumb = ThumbnailUtils.createVideoThumbnail(FileUtil.getCacheFilePath(fileName), MediaStore.Images.Thumbnails.MINI_KIND);
        elem.setSnapshotPath(FileUtil.createFile(thumb, new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())));
        TIMSnapshot snapshot = new TIMSnapshot();
        snapshot.setType("PNG");
        snapshot.setHeight(thumb.getHeight());
        snapshot.setWidth(thumb.getWidth());
        TIMVideo video = new TIMVideo();
        video.setType("MP4");
        video.setDuaration(MediaUtil.getInstance().getDuration(FileUtil.getCacheFilePath(fileName)));
        elem.setSnapshot(snapshot);
        elem.setVideo(video);
        message.addElement(elem);
    }

    public VideoMessage(String filePath, String coverPath, long duration) {
        message = new TIMMessage();
        TIMVideoElem elem = new TIMVideoElem();
        elem.setVideoPath(filePath);
        elem.setSnapshotPath(coverPath);
        TIMSnapshot snapshot = new TIMSnapshot();
        File file = new File(coverPath);
        int height = 0, width = 0;
        if (file.exists()) {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(coverPath, options);
            height = options.outHeight;
            width = options.outWidth;
        }
        snapshot.setType("PNG");
        snapshot.setHeight(height);
        snapshot.setWidth(width);
        TIMVideo video = new TIMVideo();
        video.setType("MP4");
        video.setDuaration(duration);
        elem.setSnapshot(snapshot);
        elem.setVideo(video);
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
        return "Video";
    }
}
