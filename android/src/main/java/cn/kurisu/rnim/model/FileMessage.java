package cn.kurisu.rnim.model;

import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMFileElem;
import com.tencent.imsdk.TIMMessage;

import cn.kurisu.rnim.IMApplication;
import cn.kurisu.rnim.R;
import cn.kurisu.rnim.utils.FileUtil;

/**
 * 文件消息
 */
public class FileMessage extends Message {


    public FileMessage(TIMMessage message) {
        this.message = message;
    }

    public FileMessage(String filePath) {
        message = new TIMMessage();
        TIMFileElem elem = new TIMFileElem();
        elem.setPath(filePath);
        elem.setFileName(filePath.substring(filePath.lastIndexOf("/") + 1));
        message.addElement(elem);
    }


    /**
     * 获取消息摘要
     */
    @Override
    public String getSummary() {
        String str = getRevokeSummary();
        return str == null ? "" : str;
    }

    /**
     * 保存消息或消息文件
     */
    @Override
    public void save() {
        if (message == null) return;
        final TIMFileElem e = (TIMFileElem) message.getElement(0);
        String[] str = e.getFileName().split("/");
        String filename = str[str.length - 1];
        if (FileUtil.isFileExist(filename, Environment.DIRECTORY_DOWNLOADS)) {
            Toast.makeText(IMApplication.getContext(), IMApplication.getContext().getString(R.string.save_exist), Toast.LENGTH_SHORT).show();
            return;
        }

        e.getToFile(FileUtil.getCacheFilePath(filename), new TIMCallBack() {
            @Override
            public void onError(int i, String s) {
                Log.e(TAG, "getFile failed. code: " + i + " errmsg: " + s);
            }

            @Override
            public void onSuccess() {

            }
        });

    }

    @Override
    public String getMsgType() {
        return "File";
    }
}
