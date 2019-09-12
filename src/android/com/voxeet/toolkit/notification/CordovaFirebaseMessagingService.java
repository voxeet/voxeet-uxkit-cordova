package com.voxeet.toolkit.notification;

import android.content.Intent;
import android.text.TextUtils;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.voxeet.push.firebase.VoxeetManageRemoteMessage;
import com.voxeet.sdk.core.preferences.VoxeetPreferences;
import com.voxeet.sdk.factories.VoxeetIntentFactory;
import com.voxeet.sdk.utils.AndroidManifest;

import java.util.Map;

public class CordovaFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = CordovaFirebaseMessagingService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();
        if (null != data && data.containsKey(VoxeetIntentFactory.NOTIF_TYPE)) {
            String notificationType = data.get(VoxeetIntentFactory.NOTIF_TYPE);
            if (null == notificationType || TextUtils.isEmpty(notificationType))
                notificationType = "";

            switch (notificationType) {
                case VoxeetIntentFactory.NOTIF_TYPE_INVITATION_RECEIVED:
                    String voxeet_default_incoming = AndroidManifest.readMetadata(getApplicationContext(), "voxeet_incoming_class", VoxeetPreferences.getDefaultActivity());

                    Intent intent = VoxeetIntentFactory.buildFrom(getApplicationContext(), voxeet_default_incoming, data);
                    if (intent != null)
                        getApplicationContext().startActivity(intent);
                    break;
                default:
                    boolean managed = VoxeetManageRemoteMessage.manageRemoteMessage(getApplicationContext(), remoteMessage);
            }
        }
    }
}
