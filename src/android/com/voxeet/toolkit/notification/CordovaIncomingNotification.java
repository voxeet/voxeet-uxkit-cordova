package com.voxeet.toolkit.notification;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.voxeet.sdk.utils.AndroidManifest;
import com.voxeet.toolkit.incoming.IncomingNotification;

public class CordovaIncomingNotification extends IncomingNotification {

    public CordovaIncomingNotification() {
        super();
    }

    @Override
    public String getIncomingAcceptedClass(@NonNull Context context) {
        //not returning super since this activity will be responsible for that
        return CordovaIncomingFromNotificationActivity.class.getSimpleName();
    }


}
