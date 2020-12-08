package com.voxeet.toolkit.notification;

import android.content.Context;
import android.support.annotation.NonNull;

import com.voxeet.sdk.push.center.invitation.InvitationBundle;
import com.voxeet.toolkit.CordovaRootViewProvider;
import com.voxeet.toolkit.VoxeetApplication;
import com.voxeet.uxkit.incoming.IncomingFullScreen;
import com.voxeet.uxkit.incoming.IncomingNotification;

public class CordovaIncomingNotification extends IncomingNotification {

    public CordovaIncomingNotification() {
        super();
    }

    @Override
    public String getIncomingAcceptedClass(@NonNull Context context) {
        //not returning super since this activity will be responsible for that
        return CordovaIncomingFromNotificationActivity.class.getName(); //canonical would have returned name
    }
}
