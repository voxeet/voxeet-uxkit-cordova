package com.voxeet.toolkit.notification;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.voxeet.sdk.push.center.invitation.InvitationBundle;
import com.voxeet.sdk.push.center.management.Constants;
import com.voxeet.sdk.utils.AndroidManifest;
import com.voxeet.uxkit.common.UXKitLogger;
import com.voxeet.uxkit.common.activity.bundle.DefaultIncomingBundleChecker;
import com.voxeet.uxkit.common.activity.bundle.IncomingBundleChecker;

public class CallUtils {
    public final static String BUNDLE_EXTRA_BUNDLE = "BUNDLE_EXTRA_BUNDLE";
    public final static String METADATA_CALL_ACCEPTED_OR_DECLINED = "voxeet_incoming_accepted_class";

    @Nullable
    public static Class createClassToCall(@NonNull Context context) {
        Class klass = null;

        String obtained = AndroidManifest.readMetadata(context, METADATA_CALL_ACCEPTED_OR_DECLINED, null);
        try {
            if (null != obtained) {
                klass = Class.forName(obtained);
                return klass;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            klass = Class.forName(context.getPackageName() + ".MainActivity");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return klass;
    }

    @SuppressLint("WrongConstant")
    public static void addFlags(Intent intent) {
        intent.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        );
    }

    @NonNull
    public static InvitationBundle createInvitationBundle(@NonNull IncomingBundleChecker bundleChecker) {
        return new InvitationBundle(createBundle(bundleChecker));
    }

    @NonNull
    public static Bundle createBundle(@NonNull IncomingBundleChecker bundleChecker) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.CONF_ID, bundleChecker.getConferenceId());
        bundle.putString(Constants.INVITER_NAME, bundleChecker.getUserName());
        bundle.putString(Constants.INVITER_ID, bundleChecker.getUserId());
        bundle.putString(Constants.INVITER_EXTERNAL_ID, bundleChecker.getExternalUserId());
        bundle.putString(Constants.INVITER_URL, bundleChecker.getAvatarUrl());
        return bundle;
    }

    @NonNull
    public static Intent createActivityCallAnswer(Activity caller,
                                                  @Nullable String conferenceId,
                                                  @Nullable String userName,
                                                  @Nullable String userId,
                                                  @Nullable String externalUserId,
                                                  @Nullable String avatarUrl,
                                                  Bundle extraBundle, boolean isAccepted) {
        Class to_call = CallUtils.createClassToCall(caller);

        //if call is disabled
        if (null == to_call) return null;

        Intent intent = new Intent(caller, to_call);

        //inject the extras from the current "loaded" activity
        Bundle extras = null;//here was the extras provider call
        if (null != extras) {
            intent.putExtras(extras);
        }

        intent.putExtra(BUNDLE_EXTRA_BUNDLE, extraBundle);

        intent.putExtra(Constants.CONF_ID, conferenceId)
                .putExtra(Constants.INVITER_NAME, userName)
                .putExtra(Constants.INVITER_ID, userId)
                .putExtra(Constants.INVITER_EXTERNAL_ID, externalUserId)
                .putExtra(Constants.INVITER_URL, avatarUrl);

        //deprecated
        intent.putExtra("join", isAccepted);
        intent.putExtra("callMode", 0x0001);

        CallUtils.addFlags(intent);

        return intent;
    }

    public static void dumpIntent(@NonNull DefaultIncomingBundleChecker bundle) {
        UXKitLogger.d(bundle.getClass().getSimpleName(), "dumpIntent: "
                + " userId:=" + bundle.getUserId()
                + " externaluserId:=" + bundle.getExternalUserId()
                + " conferenceId:=" + bundle.getConferenceId()
                + " userName:=" + bundle.getUserName()
                + " avatarUrl:=" + bundle.getAvatarUrl());
    }
}
