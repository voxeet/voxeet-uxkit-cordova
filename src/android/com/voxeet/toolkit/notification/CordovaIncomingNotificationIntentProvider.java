package com.voxeet.toolkit.notification;

import android.content.BroadcastReceiver;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.voxeet.uxkit.common.UXKitLogger;
import com.voxeet.uxkit.common.logging.ShortLogger;
import com.voxeet.uxkit.incoming.AbstractIncomingNotificationIntentProvider;
import com.voxeet.uxkit.incoming.implementation.AcceptedNotificationBroadcastReceiver;
import com.voxeet.uxkit.incoming.implementation.DefaultIncomingNotificationIntentProvider;
import com.voxeet.uxkit.incoming.manifest.DismissNotificationBroadcastReceiver;

public class CordovaIncomingNotificationIntentProvider extends AbstractIncomingNotificationIntentProvider {

    private final static ShortLogger Log = UXKitLogger.createLogger(DefaultIncomingNotificationIntentProvider.class);

    public CordovaIncomingNotificationIntentProvider(@NonNull Context context) {
        super(context, Log);
    }

    @NonNull
    @Override
    protected Class<? extends BroadcastReceiver> getAcceptedBroadcastReceiverClass() {
        return AcceptedNotificationBroadcastReceiver.class;
    }

    @NonNull
    @Override
    protected Class<? extends BroadcastReceiver> getDismissedBroadcastReceiverClass() {
        return DismissNotificationBroadcastReceiver.class;
    }

    @Nullable
    @Override
    protected Class<? extends AppCompatActivity> getIncomingCallActivityClass() {
        return CordovaIncomingCallActivity.class;
    }
}
