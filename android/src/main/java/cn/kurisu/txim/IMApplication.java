package cn.kurisu.txim;

import android.app.Application;
import android.content.Context;

import cn.kurisu.txim.utils.Foreground;

public class IMApplication {
    private static volatile IMApplication imApplication;

    private Context context;
    private Class mainActivityClass;

    private IMApplication() {
    }

    public static IMApplication getInstance() {
        if (imApplication == null) {
            synchronized (IMApplication.class) {
                if (imApplication == null) {
                    imApplication = new IMApplication();
                }
            }
        }
        return imApplication;
    }

    public void setContext(final Context context, Class mainActivityClass) {
        Foreground.init((Application)context);
        imApplication.context = context;
        imApplication.mainActivityClass = mainActivityClass;
    }

    public Context getContext() {
        return this.context;
    }

    public Class getMainActivityClass() {
        return this.mainActivityClass;
    }
}

