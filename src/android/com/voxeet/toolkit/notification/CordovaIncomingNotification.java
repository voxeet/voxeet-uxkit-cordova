package com.voxeet.toolkit.notification;

import android.content.Context;
import android.support.annotation.NonNull;

import com.voxeet.push.center.invitation.InvitationBundle;
import com.voxeet.toolkit.CordovaRootViewProvider;
import com.voxeet.toolkit.VoxeetApplication;
import com.voxeet.toolkit.incoming.IncomingFullScreen;
import com.voxeet.toolkit.incoming.IncomingNotification;

public class CordovaIncomingNotification extends IncomingNotification {

    @NonNull
    private final IncomingFullScreen incomingFullScreen;

    public CordovaIncomingNotification() {
        super();
        incomingFullScreen = new IncomingFullScreen(CordovaIncomingCallActivity.class);
    }

    @Override
    public String getIncomingAcceptedClass(@NonNull Context context) {
        //not returning super since this activity will be responsible for that
        return CordovaIncomingFromNotificationActivity.class.getName(); //canonical would have returned name
    }

    @Override
    public void onInvitation(@NonNull Context context, @NonNull InvitationBundle invitationBundle) {
        CordovaRootViewProvider provider = VoxeetApplication.ROOT_VIEW_PROVIDER;
        if (null != provider && null != provider.getCurrentActivity()) {
            incomingFullScreen.onInvitation(context, invitationBundle);
        } else super.onInvitation(context, invitationBundle);
    }

    @Override
    public void onInvitationCanceled(@NonNull Context context, @NonNull String conferenceId) {
        CordovaRootViewProvider provider = VoxeetApplication.ROOT_VIEW_PROVIDER;
        if (null != provider && null != provider.getCurrentActivity()) {
            incomingFullScreen.onInvitationCanceled(context, conferenceId);
        } else super.onInvitationCanceled(context, conferenceId);
    }
}
