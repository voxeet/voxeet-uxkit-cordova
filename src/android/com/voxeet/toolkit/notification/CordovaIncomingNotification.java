package com.voxeet.toolkit.notification;

import android.content.Context;

import androidx.annotation.NonNull;

import com.voxeet.sdk.push.center.invitation.IIncomingInvitationListener;
import com.voxeet.sdk.push.center.invitation.InvitationBundle;
import com.voxeet.uxkit.common.UXKitLogger;
import com.voxeet.uxkit.common.logging.ShortLogger;
import com.voxeet.uxkit.incoming.implementation.DefaultIncomingNotificationIntentProvider;
import com.voxeet.uxkit.incoming.implementation.DefaultIncomingNotificationService;
import com.voxeet.uxkit.incoming.utils.IncomingNotificationServiceHelper;

//PROBLEM IS HERE
public class CordovaIncomingNotification implements IIncomingInvitationListener {

    private static final ShortLogger Log = UXKitLogger.createLogger(com.voxeet.uxkit.incoming.implementation.DefaultIncomingNotification.class.getSimpleName());


    public CordovaIncomingNotification(@NonNull Context context) {
        new DefaultIncomingNotificationIntentProvider(context).createNotificationChannel();
        Log.d("created");
    }

    @Override
    public void onInvitation(@NonNull Context context, @NonNull InvitationBundle invitationBundle) {
        Log.d("onInvitation " + invitationBundle);

        IncomingNotificationServiceHelper.start(DefaultIncomingNotificationService.class,
                context, invitationBundle, new DefaultIncomingNotificationIntentProvider(context));
    }

    @Override
    public void onInvitationCanceled(@NonNull Context context, @NonNull String conferenceId) {
        Log.d("onInvitationCanceled " + conferenceId);

        IncomingNotificationServiceHelper.stop(DefaultIncomingNotificationService.class, context, conferenceId, null);
    }
}