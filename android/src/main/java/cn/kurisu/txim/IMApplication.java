package cn.kurisu.txim;

import android.app.Application;
import android.content.Context;
import cn.kurisu.txim.utils.Foreground;

public class IMApplication {

    private static Context context;

    private static Class mainActivityClass;

    public static void setContext(final Context context, Class mainActivityClass) {
        Foreground.init((Application)context);
        IMApplication.context = context.getApplicationContext();
        IMApplication.mainActivityClass = mainActivityClass;
    }

    public static Context getContext() {
        return context;
    }

    public static Class getMainActivityClass() {
        return mainActivityClass;
    }
}

