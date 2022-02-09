package com.voxeet.toolkit.notification;

import androidx.annotation.NonNull;

import com.voxeet.uxkit.incoming.AbstractIncomingNotificationService;
import com.voxeet.uxkit.incoming.implementation.DefaultIncomingNotificationIntentProvider;

public class DefaultIncomingNotificationService extends AbstractIncomingNotificationService<CordovaIncomingNotificationIntentProvider> {
    @NonNull
    @Override
    protected CordovaIncomingNotificationIntentProvider createIncomingNotificationIntentProvider() {
        return new CordovaIncomingNotificationIntentProvider(this);
    }
}
