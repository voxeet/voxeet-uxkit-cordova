package com.voxeet.toolkit.notification;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.voxeet.toolkit.R;
import com.voxeet.toolkit.views.internal.rounded.RoundedImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import eu.codlab.simplepromise.solve.ErrorPromise;
import eu.codlab.simplepromise.solve.PromiseExec;
import eu.codlab.simplepromise.solve.Solver;
import voxeet.com.sdk.audio.SoundManager;
import voxeet.com.sdk.core.VoxeetSdk;
import voxeet.com.sdk.core.services.AudioService;
import voxeet.com.sdk.events.success.ConferenceDestroyedPushEvent;
import voxeet.com.sdk.events.success.ConferenceEndedEvent;
import voxeet.com.sdk.events.success.ConferencePreJoinedEvent;
import voxeet.com.sdk.events.success.DeclineConferenceResultEvent;
import voxeet.com.sdk.utils.AndroidManifest;
import voxeet.com.sdk.utils.AudioType;

/**
 * Created by kevinleperf on 25/05/2018.
 */

public class CordovaIncomingCallActivity extends AppCompatActivity implements CordovaIncomingBundleChecker.IExtraBundleFillerListener {

    private final static String TAG = CordovaIncomingCallActivity.class.getSimpleName();
    private static final String DEFAULT_VOXEET_INCOMING_CALL_DURATION_KEY = "voxeet_incoming_call_duration";
    private static final int DEFAULT_VOXEET_INCOMING_CALL_DURATION_VALUE = 40 * 1000;
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
            soundManager.setInVoiceCallSoundType().playSoundType(AudioType.RING);
        }

        if (mIncomingBundleChecker.isBundleValid()) {
            if (null != VoxeetSdk.getInstance()) {
                mEventBus = VoxeetSdk.getInstance().getEventBus();
                if (null != mEventBus) mEventBus.register(this);
            }

            mUsername.setText(mIncomingBundleChecker.getUserName());
            Picasso.get()
                    .load(mIncomingBundleChecker.getAvatarUrl())
                    .into(mAvatar);
        } else {
            mIncomingBundleChecker.dumpIntent();
            finish();
        }
    }

    @Override
    protected void onPause() {
        isResumed = false;

        SoundManager soundManager = AudioService.getSoundManager();
        if (null != soundManager) {
            soundManager.resetDefaultSoundType().stopSoundType(AudioType.RING);
        }

        if (mEventBus != null) {
            mEventBus.unregister(this);
        }

        super.onPause();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ConferenceDestroyedPushEvent event) {
        if (mIncomingBundleChecker.isSameConference(event.getPush().getConferenceId())) {
            finish();
        }
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     * Specific event used to manage the current "incoming" call feature
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ConferenceEndedEvent event) {
        if (mIncomingBundleChecker.isSameConference(event.getEvent().getConferenceId())) {
            finish();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(DeclineConferenceResultEvent event) {
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ConferencePreJoinedEvent event) {
        if (mIncomingBundleChecker.isSameConference(event.getConferenceId())) {
            finish();
        }
    }

    @Nullable
    protected String getConferenceId() {
        return mIncomingBundleChecker != null && mIncomingBundleChecker.isBundleValid() ? mIncomingBundleChecker.getConferenceId() : null;
    }

    protected void onDecline() {
        if (getConferenceId() != null && null != VoxeetSdk.getInstance()) {
            VoxeetSdk.getInstance().getConferenceService().decline(getConferenceId())
                    .then(new PromiseExec<DeclineConferenceResultEvent, Object>() {
                        @Override
                        public void onCall(@Nullable DeclineConferenceResultEvent result, @NonNull Solver<Object> solver) {
                            //
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

    protected void onAccept() {
        if (mIncomingBundleChecker.isBundleValid()) {
            CORDOVA_ROOT_BUNDLE = mIncomingBundleChecker;

            Intent intent = mIncomingBundleChecker.createActivityAccepted(this);
            //start the accepted call activity
            startActivity(intent);

            //and finishing this one - before the prejoined event
            finish();
            overridePendingTransition(0, 0);
        }
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
