package cn.kurisu.reactnativedemo.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;
import android.widget.Toast;

import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMImage;
import com.tencent.imsdk.TIMImageElem;
import com.tencent.imsdk.TIMImageType;
import com.tencent.imsdk.TIMMessage;

import java.io.IOException;

import cn.kurisu.reactnativedemo.IMApplication;
import cn.kurisu.reactnativedemo.R;
import cn.kurisu.reactnativedemo.utils.FileUtil;

/**
 * 图片消息数据
 */
public class ImageMessage extends Message {

    private static final String TAG = "ImageMessage";
    private boolean isDownloading;

    public ImageMessage(TIMMessage message) {
        this.message = message;
    }

    public ImageMessage(String path) {
        this(path, false);
    }

    /**
     * 图片消息构造函数
     *
     * @param path  图片路径
     * @param isOri 是否原图发送
     */
    public ImageMessage(String path, boolean isOri) {
        message = new TIMMessage();
        TIMImageElem elem = new TIMImageElem();
        elem.setPath(path);
        elem.setLevel(isOri ? 0 : 1);
        message.addElement(elem);
    }


    /**
     * 获取消息摘要
     */
    @Override
    public String getSummary() {
        String str = getRevokeSummary();
        if (str != null) return str;
        return IMApplication.getContext().getString(R.string.summary_image);
    }

    /**
     * 保存消息或消息文件
     */
    @Override
    public void save() {
        final TIMImageElem e = (TIMImageElem) message.getElement(0);
        for (TIMImage image : e.getImageList()) {
            if (image.getType() == TIMImageType.Original) {
                final String uuid = image.getUuid();
                if (FileUtil.isCacheFileExist(uuid + ".jpg")) {
                    Toast.makeText(IMApplication.getContext(), IMApplication.getContext().getString(R.string.save_exist), Toast.LENGTH_SHORT).show();
                    return;
                }
                image.getImage(FileUtil.getCacheFilePath(uuid + ".jpg"), new TIMCallBack() {
                    @Override
                    public void onError(int i, String s) {
                        Log.e(TAG, "getFile failed. code: " + i + " errmsg: " + s);
                    }

                    @Override
                    public void onSuccess() {
                        Toast.makeText(IMApplication.getContext(), IMApplication.getContext().getString(R.string.save_succ), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    @Override
    public String getMsgType() {
        return "Image";
    }

    /**
     * 生成缩略图
     * 缩略图是将原图等比压缩，压缩后宽、高中较小的一个等于198像素
     * 详细信息参见文档
     */
    private Bitmap getThumb(String path) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        int reqWidth, reqHeight, width = options.outWidth, height = options.outHeight;
        if (width > height) {
            reqWidth = 198;
            reqHeight = (reqWidth * height) / width;
        } else {
            reqHeight = 198;
            reqWidth = (width * reqHeight) / height;
        }
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        try {
            options.inSampleSize = inSampleSize;
            options.inJustDecodeBounds = false;
            Matrix mat = new Matrix();
            Bitmap bitmap = BitmapFactory.decodeFile(path, options);
            ExifInterface ei = new ExifInterface(path);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    mat.postRotate(90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    mat.postRotate(180);
                    break;
            }
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mat, true);
        } catch (IOException e) {
            return null;
        }
    }


    private void navToImageview(final TIMImage image, final Context context) {
    }
}
