package com.voxeet.toolkit;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.voxeet.sdk.events.sdk.ConferenceStatusUpdatedEvent;
import com.voxeet.uxkit.common.activity.VoxeetCommonAppCompatActivityWrapper;
import com.voxeet.uxkit.common.activity.bundle.DefaultIncomingBundleChecker;
import com.voxeet.uxkit.common.activity.bundle.IncomingBundleChecker;
import com.voxeet.uxkit.service.VoxeetSystemService;

public class CordovaActivityWrapper extends VoxeetCommonAppCompatActivityWrapper<VoxeetSystemService> {
    public CordovaActivityWrapper(@NonNull AppCompatActivity parentActivity) {
        super(parentActivity);
    }

    @Override
    protected void onSdkServiceAvailable() {

    }

    @Override
    protected void onConferenceState(@NonNull ConferenceStatusUpdatedEvent conferenceStatusUpdatedEvent) {

    }

    @Override
    protected boolean canBeRegisteredToReceiveCalls() {
        return true;
    }

    @Override
    public IncomingBundleChecker createIncomingBundleChecker(@Nullable Intent intent) {
        if(null == intent) return new DefaultIncomingBundleChecker(new Intent(), null);
        return new DefaultIncomingBundleChecker(intent, null);
    }
}
