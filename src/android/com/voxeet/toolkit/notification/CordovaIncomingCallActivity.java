package com.voxeet.toolkit.notification;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.voxeet.audio.utils.Constants;
import com.voxeet.promise.solve.ErrorPromise;
import com.voxeet.promise.solve.PromiseExec;
import com.voxeet.promise.solve.Solver;
import com.voxeet.sdk.VoxeetSdk;
import com.voxeet.sdk.events.sdk.ConferenceStatusUpdatedEvent;
import com.voxeet.sdk.json.ConferenceDestroyedPush;
import com.voxeet.sdk.media.audio.SoundManager;
import com.voxeet.sdk.preferences.VoxeetPreferences;
import com.voxeet.sdk.services.AudioService;
import com.voxeet.sdk.services.ConferenceService;
import com.voxeet.sdk.utils.AndroidManifest;
import com.voxeet.sdk.utils.AudioType;
import com.voxeet.toolkit.R;
import com.voxeet.toolkit.VoxeetCordova;
import com.voxeet.toolkit.views.internal.rounded.RoundedImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class CordovaIncomingCallActivity extends AppCompatActivity implements CordovaIncomingBundleChecker.IExtraBundleFillerListener {

    private final static String TAG = CordovaIncomingCallActivity.class.getSimpleName();
    private static final String DEFAULT_VOXEET_INCOMING_CALL_DURATION_KEY = "voxeet_incoming_call_duration";
    private static final int DEFAULT_VOXEET_INCOMING_CALL_DURATION_VALUE = 40 * 1000;
    static final int RECORD_AUDIO_RESULT = 0x10;
    public static CordovaIncomingBundleChecker CORDOVA_ROOT_BUNDLE = null;
    public static RNBundleChecker CORDOVA_AWAITING_BUNDLE_TO_BE_MANAGE_FOR_ACCEPT = null;
    public static RNBundleChecker CORDOVA_AWAITING_BUNDLE_TO_BE_MANAGE_FOR_DECLINE = null;
    public static RNBundleChecker CORDOVA_AWAITING_BUNDLE_TO_BE_MANAGE_FOR_LAUNCH_ACCEPT = null;

    protected TextView mUsername;
    protected TextView mStateTextView;
    protected TextView mDeclineTextView;
    protected TextView mAcceptTextView;
    protected RoundedImageView mAvatar;
    protected EventBus mEventBus;

    private CordovaIncomingBundleChecker mIncomingBundleChecker;
    private Handler mHandler;
    private boolean isResumed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isResumed = false;

        VoxeetCordova.tryInitialize(this, this);

        //we preInit the AudioService,
        AudioService.preInitSounds(getApplicationContext());

        mIncomingBundleChecker = new CordovaIncomingBundleChecker(this, getIntent(), this);

        //add few Flags to start the activity before its setContentView
        //note that if your device is using a keyguard (code or password)
        //when the call will be accepted, you still need to unlock it
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.voxeet_activity_incoming_call);

        mUsername = (TextView) findViewById(R.id.voxeet_incoming_username);
        mAvatar = (RoundedImageView) findViewById(R.id.voxeet_incoming_avatar_image);
        mStateTextView = (TextView) findViewById(R.id.voxeet_incoming_text);
        mAcceptTextView = (TextView) findViewById(R.id.voxeet_incoming_accept);
        mDeclineTextView = (TextView) findViewById(R.id.voxeet_incoming_decline);

        mDeclineTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDecline();
            }
        });

        mAcceptTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAccept();
            }
        });

        mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    if (null != mHandler)
                        finish();
                } catch (Exception e) {

                }
            }
        }, AndroidManifest.readMetadataInt(this, DEFAULT_VOXEET_INCOMING_CALL_DURATION_KEY,
                DEFAULT_VOXEET_INCOMING_CALL_DURATION_VALUE));
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
            if (null != VoxeetSdk.instance()) {
                mEventBus = VoxeetSdk.instance().getEventBus();
                try {
                    if (null != mEventBus && !mEventBus.isRegistered(this))
                        mEventBus.register(this);
                } catch (Exception e) {

                }
            }

            mUsername.setText(mIncomingBundleChecker.getUserName());
            Picasso.get()
                    .load(mIncomingBundleChecker.getAvatarUrl())
                    .placeholder(R.drawable.default_avatar)
                    .error(R.drawable.default_avatar)
                    .into(mAvatar);
        } else {
            Log.d(TAG, "onResume: incoming call will quit, bundle invalid ...");
            mIncomingBundleChecker.dumpIntent();
            finish();
        }
    }

    @Override
    protected void onPause() {
        isResumed = false;

        try {
            EventBus.getDefault().unregister(this);
        } catch (Exception e) {

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

        }

        super.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case RECORD_AUDIO_RESULT: {
                for (int i = 0; i < permissions.length; i++) {
                    String permission = permissions[i];
                    int grantResult = grantResults[i];

                    if (Manifest.permission.RECORD_AUDIO.equals(permission) && grantResult == PackageManager.PERMISSION_GRANTED) {
                        onAcceptWithPermission();
                    } else {
                        //possible message to show? display?
                    }
                }
                return;
            }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ConferenceDestroyedPush event) {
        if (mIncomingBundleChecker.isSameConference(event.conferenceId)) {
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
        ConferenceService service = VoxeetSdk.conference();
        if (getConferenceId() != null && null != service) {
            service.decline(getConferenceId())
                    .then(new PromiseExec<Boolean, Object>() {
                        @Override
                        public void onCall(@Nullable Boolean result, @NonNull Solver<Object> solver) {
                            finish();
                        }
                    })
                    .error(new ErrorPromise() {
                        @Override
                        public void onError(Throwable error) {
                            finish();
                        }
                    });
        } else {
            CORDOVA_AWAITING_BUNDLE_TO_BE_MANAGE_FOR_DECLINE = new RNBundleChecker(getIntent(), null);

            //finish();
            Intent intent = CORDOVA_AWAITING_BUNDLE_TO_BE_MANAGE_FOR_DECLINE.createActivityDeclined(this);
            //start the accepted call activity
            startActivity(intent);

            //and finishing this one - before the prejoined event
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_AUDIO_RESULT);
        } else {
            onAcceptWithPermission();
        }
    }

    protected void onAcceptWithPermission() {
        if (mIncomingBundleChecker.isBundleValid()) {
            if (canDirectlyUseJoin()) {
                VoxeetCordova.checkForIncomingConference(mIncomingBundleChecker);
            } else {
                CORDOVA_ROOT_BUNDLE = mIncomingBundleChecker;
            }

            Intent intent = mIncomingBundleChecker.createActivityAccepted(this);
            //start the accepted call activity
            startActivity(intent);

            //and finishing this one - before the prejoined event
            finish();
            overridePendingTransition(0, 0);
        }
    }

    private boolean canDirectlyUseJoin() {
        return null != VoxeetSdk.session() && null != VoxeetPreferences.getSavedUserInfo();
    }

    /**
     * Give the possibility to add custom extra infos before starting a conference
     *
     * @return a nullable extra bundle (will not be the bundle sent but a value with a key)
     */
    @Nullable
    @Override
    public Bundle createExtraBundle() {
        //override to return a custom intent to add in the possible notification
        //note that everything which could have been backed up from the previous activity
        //will be injected after the creation - usefull if the app is mainly based on
        //passed intents
        return null;
    }

    /**
     * Get the instance of the bundle checker corresponding to this activity
     *
     * @return an instance or null corresponding to the current bundle checker
     */
    @Nullable
    protected CordovaIncomingBundleChecker getBundleChecker() {
        return mIncomingBundleChecker;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
