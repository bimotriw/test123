package com.oustme.oustsdk.compression.ffmpeg;

import com.oustme.oustsdk.tools.OustSdkTools;

import java.util.LinkedHashSet;
import java.util.Set;


/**
 * Created by TangChao on 2017/9/14.
 */

public class FFMPEGCmdExecutorFactory {

    private static Set<Class<? extends FFMPEGCmdExecutor>> executorClasss = new LinkedHashSet<>();

    public static FFMPEGCmdExecutor create(){
        for (Class<? extends FFMPEGCmdExecutor> z : executorClasss) {
            try {
                return z.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            OustSdkTools.sendSentryException(e);
            }
        }
        return new WMExecutor();
    }


    public static void registerFFMPEGExecutor(Class<? extends FFMPEGCmdExecutor> clazz) {
        executorClasss.add(clazz);
    }
}
