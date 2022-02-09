package com.voxeet.toolkit.notification;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;
import com.voxeet.VoxeetSDK;
import com.voxeet.audio.utils.Constants;
import com.voxeet.sdk.events.sdk.ConferenceStatusUpdatedEvent;
import com.voxeet.sdk.json.ConferenceDestroyedPush;
import com.voxeet.sdk.media.audio.SoundManager;
import com.voxeet.sdk.preferences.VoxeetPreferences;
import com.voxeet.sdk.services.AudioService;
import com.voxeet.sdk.services.ConferenceService;
import com.voxeet.sdk.utils.AndroidManifest;
import com.voxeet.sdk.utils.AudioType;
import com.voxeet.toolkit.CordovaPermissionHelper;
import com.voxeet.toolkit.VoxeetCordova;
import com.voxeet.uxkit.R;
import com.voxeet.uxkit.common.activity.bundle.DefaultIncomingBundleChecker;
import com.voxeet.uxkit.views.internal.rounded.RoundedImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public class CordovaIncomingCallActivity extends AppCompatActivity {

    private final static String TAG = CordovaIncomingCallActivity.class.getSimpleName();
    private static final String DEFAULT_VOXEET_INCOMING_CALL_DURATION_KEY = "voxeet_incoming_call_duration";
    private static final int DEFAULT_VOXEET_INCOMING_CALL_DURATION_VALUE = 40 * 1000;
    public static DefaultIncomingBundleChecker CORDOVA_ROOT_BUNDLE = null;
    public static DefaultIncomingBundleChecker CORDOVA_AWAITING_BUNDLE_TO_BE_MANAGE_FOR_ACCEPT = null;
    public static RNBundleChecker CORDOVA_AWAITING_BUNDLE_TO_BE_MANAGE_FOR_DECLINE = null;
    public static DefaultIncomingBundleChecker CORDOVA_AWAITING_BUNDLE_TO_BE_MANAGE_FOR_LAUNCH_ACCEPT = null;

    protected TextView mUsername;
    protected TextView mStateTextView;
    protected TextView mDeclineTextView;
    protected TextView mAcceptTextView;
    protected RoundedImageView mAvatar;
    protected EventBus mEventBus;

    private DefaultIncomingBundleChecker mIncomingBundleChecker;
    private Handler mHandler;
    private boolean isResumed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isResumed = false;

        //we preInit the AudioService,
        AudioService.preInitSounds(getApplicationContext());

        mIncomingBundleChecker = new DefaultIncomingBundleChecker(getIntent(), null);

        //add few Flags to start the activity before its setContentView
        //note that if your device is using a keyguard (code or password)
        //when the call will be accepted, you still need to unlock it
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.voxeet_activity_incoming_call);

        mUsername = (TextView) findViewById(R.id.voxeet_incoming_username);
        mAvatar = (RoundedImageView) findViewById(R.id.voxeet_incoming_avatar_image);
        mStateTextView = (TextView) findViewById(R.id.voxeet_incoming_text);
        mAcceptTextView = (TextView) findViewById(R.id.voxeet_incoming_accept);
        mDeclineTextView = (TextView) findViewById(R.id.voxeet_incoming_decline);

        mDeclineTextView.setOnClickListener(view -> onDecline());

        mAcceptTextView.setOnClickListener(view -> onAccept());

        int timeout = AndroidManifest.readMetadataInt(this, DEFAULT_VOXEET_INCOMING_CALL_DURATION_KEY,
                DEFAULT_VOXEET_INCOMING_CALL_DURATION_VALUE);
        mHandler = new Handler();
        mHandler.postDelayed(() -> {
            try {
                if (null != mHandler) {
                    Log.d(TAG, "run: timedout... leaving screen. Timeout was := " + timeout);
                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, timeout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isResumed = true;

        SoundManager soundManager = AudioService.getSoundManager();
        if (null != soundManager) {
            soundManager.checkOutputRoute().playSoundType(AudioType.RING, Constants.STREAM_MUSIC);
        }

        try {
            EventBus.getDefault().register(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mIncomingBundleChecker.isBundleValid()) {
            mEventBus = VoxeetSDK.instance().getEventBus();
            try {
                if (!mEventBus.isRegistered(this))
                    mEventBus.register(this);
            } catch (Exception e) {
                e.printStackTrace();
            }

            mUsername.setText(mIncomingBundleChecker.getUserName());
            try {
                String avatarUrl = mIncomingBundleChecker.getAvatarUrl();
                if (!TextUtils.isEmpty(avatarUrl)) {
                    Picasso.get()
                            .load(avatarUrl)
                            .placeholder(R.drawable.default_avatar)
                            .error(R.drawable.default_avatar)
                            .into(mAvatar);
                } else {
                    Picasso.get()
                            .load(R.drawable.default_avatar)
                            .error(R.drawable.default_avatar)
                            .into(mAvatar);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "onResume: incoming call will quit, bundle invalid ...");
            CallUtils.dumpIntent(mIncomingBundleChecker);
            finish();
        }
    }

    @Override
    protected void onPause() {
        isResumed = false;

        try {
            EventBus.getDefault().unregister(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        SoundManager soundManager = AudioService.getSoundManager();
        if (null != soundManager) {
            soundManager.resetDefaultSoundType().stopSoundType(AudioType.RING);
        }

        try {
            if (mEventBus != null) {
                mEventBus.unregister(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onPause();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ConferenceDestroyedPush event) {
        if (mIncomingBundleChecker.isSameConference(event.conferenceId)) {
            Log.d(TAG, "onEvent: conference destroyed, leaving");
            finish();
        }
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Specific event used to manage the current "incoming" call feature
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ConferenceStatusUpdatedEvent event) {
        switch (event.state) {
            case JOINING:
            case JOINED:
                if (mIncomingBundleChecker.isSameConference(event.conference.getId())) {
                    Log.d(TAG, "onEvent: conference has been joined or is joining");
                    finish();
                }
                break;
            default:
        }
    }

    @Nullable
    protected String getConferenceId() {
        return mIncomingBundleChecker != null && mIncomingBundleChecker.isBundleValid() ? mIncomingBundleChecker.getConferenceId() : null;
    }

    protected void onDecline() {
        ConferenceService service = VoxeetSDK.conference();
        if (getConferenceId() != null && VoxeetSDK.instance().isInitialized()) {
            service.decline(getConferenceId())
                    .then((result, solver) -> {
                        Log.d(TAG, "onCall: declining... leaving incoming screen");
                        finish();
                    })
                    .error(error -> {
                        Log.e(TAG, "onCall: declining... leaving incoming screen", error);
                        finish();
                    });
        } else {
            CORDOVA_AWAITING_BUNDLE_TO_BE_MANAGE_FOR_DECLINE = new RNBundleChecker(getIntent());

            //finish();
            Intent intent = CORDOVA_AWAITING_BUNDLE_TO_BE_MANAGE_FOR_DECLINE.createActivityAccepted(this);
            //start the accepted call activity
            startActivity(intent);

            //and finishing this one - before the prejoined event
            Log.d(TAG, "onDecline: leaving right now the incoming screen");
            finish();
            overridePendingTransition(0, 0);
        }
    }

    /**
     * Accept a call following an user interaction. This call also check for the mic permission - and will ask the user accordingly.
     * The permission callback will then automatically consider mic granted as the accept action
     * (the only flow to have mic permission with this activity in the accept call button)
     */
    protected void onAccept() {
        CordovaPermissionHelper.requestDefaultPermission().then(result -> {
            onAcceptWithPermission();
        }).error(Throwable::printStackTrace);
    }

    protected void onAcceptWithPermission() {
        if (!mIncomingBundleChecker.isBundleValid()) return;
        if (canDirectlyUseJoin()) {
            VoxeetCordova.checkForIncomingConference(mIncomingBundleChecker);
        } else {
            CORDOVA_ROOT_BUNDLE = mIncomingBundleChecker;
        }

        Intent intent = mIncomingBundleChecker.createActivityAccepted(this);
        //start the accepted call activity
        startActivity(intent);

        //and finishing this one - before the prejoined event
        Log.d(TAG, "onAcceptWithPermission: permission accepted, leaving screen and starting main");
        finish();
        overridePendingTransition(0, 0);
    }

    private boolean canDirectlyUseJoin() {
        return VoxeetSDK.instance().isInitialized() && null != VoxeetPreferences.getSavedUserInfo();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
