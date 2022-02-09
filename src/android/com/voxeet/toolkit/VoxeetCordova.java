package com.voxeet.toolkit;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.voxeet.VoxeetSDK;
import com.voxeet.promise.Promise;
import com.voxeet.promise.solve.ErrorPromise;
import com.voxeet.promise.solve.ThenPromise;
import com.voxeet.promise.solve.ThenValue;
import com.voxeet.promise.solve.ThenVoid;
import com.voxeet.sdk.authent.token.TokenCallback;
import com.voxeet.sdk.events.error.PermissionRefusedEvent;
import com.voxeet.sdk.events.sdk.ConferenceStatusUpdatedEvent;
import com.voxeet.sdk.events.sdk.SocketStateChangeEvent;
import com.voxeet.sdk.events.v2.ParticipantAddedEvent;
import com.voxeet.sdk.json.ParticipantInfo;
import com.voxeet.sdk.json.internal.MetadataHolder;
import com.voxeet.sdk.json.internal.ParamsHolder;
import com.voxeet.sdk.models.Conference;
import com.voxeet.sdk.preferences.VoxeetPreferences;
import com.voxeet.sdk.push.center.NotificationCenter;
import com.voxeet.sdk.push.center.invitation.InvitationBundle;
import com.voxeet.sdk.push.center.management.EnforcedNotificationMode;
import com.voxeet.sdk.push.center.management.NotificationMode;
import com.voxeet.sdk.push.center.management.VersionFilter;
import com.voxeet.sdk.push.center.subscription.register.SubscribeInvitation;
import com.voxeet.sdk.services.AudioService;
import com.voxeet.sdk.services.ConferenceService;
import com.voxeet.sdk.services.SessionService;
import com.voxeet.sdk.services.TelemetryService;
import com.voxeet.sdk.services.builders.ConferenceCreateOptions;
import com.voxeet.sdk.services.builders.ConferenceJoinOptions;
import com.voxeet.sdk.services.builders.ConferenceListenOptions;
import com.voxeet.sdk.services.telemetry.SdkEnvironment;
import com.voxeet.sdk.utils.Validate;
import com.voxeet.toolkit.notification.CallUtils;
import com.voxeet.toolkit.notification.CordovaIncomingCallActivity;
import com.voxeet.toolkit.notification.CordovaIncomingNotification;
import com.voxeet.toolkit.notification.RNBundleChecker;
import com.voxeet.uxkit.common.activity.PermissionContractHolder;
import com.voxeet.uxkit.common.activity.bundle.DefaultIncomingBundleChecker;
import com.voxeet.uxkit.common.permissions.PermissionController;
import com.voxeet.uxkit.configuration.ActionBar;
import com.voxeet.uxkit.configuration.Configuration;
import com.voxeet.uxkit.configuration.Overlay;
import com.voxeet.uxkit.configuration.Users;
import com.voxeet.uxkit.controllers.VoxeetToolkit;
import com.voxeet.uxkit.implementation.overlays.OverlayState;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PermissionHelper;
import org.apache.cordova.PluginResult;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Voxeet implementation for Cordova
 */

public class VoxeetCordova extends CordovaPlugin {

    private static final String VERSION = "1.5.3-BETA6";
    private static final String ERROR_SDK_NOT_INITIALIZED = "ERROR_SDK_NOT_INITIALIZED";
    private static final String ERROR_SDK_NOT_LOGGED_IN = "ERROR_SDK_NOT_LOGGED_IN";
    private static final String SDK_ALREADY_CONFIGURED_ERROR = "The SDK is already configured";
    private static final String TAG = VoxeetCordova.class.getSimpleName();

    private static final Handler HANDLER = new Handler(Looper.getMainLooper());

    //static to let CordovaIncomingBundleChecker to check the state of it
    //it'll do the trick until the sdk is able to create and maintain a proper
    //conference configuration holder (something expected second half 2019)
    public static boolean startVideoOnJoin = false;

    private ParticipantInfo _current_user;
    private CallbackContext _log_in_callback;
    private ReentrantLock lock = new ReentrantLock();
    private ReentrantLock lockAwaitingToken = new ReentrantLock();
    private List<TokenCallback> mAwaitingTokenCallback;
    private CallbackContext refreshAccessTokenCallbackInstance;
    private CallbackContext onConferenceStatusUpdatedEventCallback;
    private CordovaWebView mWebView;
    private PermissionContractHolder permissionContractHolder;

    public VoxeetCordova() {
        super();
        mAwaitingTokenCallback = new ArrayList<>();

        Promise.setHandler(HANDLER);
    }

    public static void initNotificationCenter(@NonNull Context context) {
        //set Android Q as the minimum version no more supported by the full screen mode
        NotificationCenter.instance
                .register(NotificationMode.FULLSCREEN_INCOMING_CALL, new VersionFilter(VersionFilter.ALL, 29))
                //register notification only mode
                .register(NotificationMode.OVERHEAD_INCOMING_CALL, new CordovaIncomingNotification(context))
                //register full screen mode
                .register(NotificationMode.FULLSCREEN_INCOMING_CALL, new CordovaIncomingNotification(context))
                //activate fullscreen -> notification mode only
                .setEnforcedNotificationMode(EnforcedNotificationMode.MIXED_INCOMING_CALL);
    }

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);

        mWebView = webView;

        VoxeetSDK.instance().register(this);
        permissionContractHolder = new PermissionContractHolder(cordova.getActivity());
    }

    @Override
    public void onResume(boolean multitasking) {
        super.onResume(multitasking);

        VoxeetSDK.instance().register(this);

        PermissionController.register(permissionContractHolder.getRequestPermissions());

        if (VoxeetSDK.conference().isLive()) {
            setVolumeVoiceCall();
        }
        checkForAwaitingConference(null);
    }

    @Override
    public void onPause(boolean multitasking) {
        super.onPause(multitasking);

        ConferenceService service = VoxeetSDK.conference();
        if (null != AudioService.getSoundManager() && service.isLive()) {
            AudioService.getSoundManager().requestAudioFocus();
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException {
        super.onRequestPermissionResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PermissionRefusedEvent.RESULT_CAMERA: {
                ConferenceService service = VoxeetSDK.conference();
                if (service.isLive()) {
                    service.startVideo()
                            .then((result, solver) -> {

                            })
                            .error(Throwable::printStackTrace);
                }
                return;
            }
            default:
        }
    }

    private int index(@NonNull String[] permissions, @NonNull String permission) {
        int i = 0;
        for (String perm : permissions) {
            if (permission.equalsIgnoreCase(perm)) return i;
            i++;
        }
        return -1;
    }

    @Override
    public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        Log.d("VoxeetCordova", "execute: request " + action);
        if (action != null) {
            switch (action) {
                case "onAccessTokenOk":
                    onAccessTokenOk(args.getString(0), callbackContext);
                    break;
                case "onAccessTokenKo":
                    onAccessTokenKo(args.getString(0), callbackContext);
                    break;
                case "initializeToken":
                    initialize(args.getString(0), callbackContext);
                    break;
                case "initialize":
                    initialize(args.getString(0),
                            args.getString(1),
                            callbackContext);
                    break;
                case "refreshAccessTokenCallback":
                    refreshAccessTokenCallback(callbackContext);
                    break;
                case "connect":
                    try {
                        JSONObject userInfo = null;
                        if (!args.isNull(0)) userInfo = args.getJSONObject(0);
                        ParticipantInfo user = null;

                        if (null != userInfo) {
                            user = new ParticipantInfo(string(userInfo, "name"),
                                    string(userInfo, "externalId"),
                                    string(userInfo, "avatarUrl"));
                        }

                        openSession(user, callbackContext);
                    } catch (Exception e) {
                        e.printStackTrace();
                        callbackContext.error(e.getMessage());
                    }
                    break;
                case "disconnect":
                    closeSession(callbackContext);
                    break;
                case "isUserLoggedIn":
                    isUserLoggedIn(callbackContext);
                    break;
                case "isAudio3DEnabled":
                    isAudio3DEnabled(callbackContext);
                    break;
                case "isTelecomMode":
                    isTelecomMode(callbackContext);
                    break;
                case "checkForAwaitingConference":
                    checkForAwaitingConference(callbackContext);
                    break;
                case "create":
                    try {
                        JSONObject parameters = args.getJSONObject(0);

                        String confAlias = parameters.getString("alias");
                        JSONObject object = null;
                        if (!parameters.isNull("metadata")) {
                            object = parameters.getJSONObject("metadata");
                        }

                        MetadataHolder holder = new MetadataHolder();
                        if (null != object) {
                            Iterator<String> keys = object.keys();
                            while (keys.hasNext()) {
                                String key = keys.next();
                                holder.putValue(key, object.get(key));
                            }
                        }

                        if (!parameters.isNull("params")) {
                            object = parameters.getJSONObject("params");
                        }
                        ParamsHolder pholder = new ParamsHolder();
                        if (null != object) {
                            Iterator<String> keys = object.keys();
                            while (keys.hasNext()) {
                                String key = keys.next();
                                pholder.putValue(key, object.get(key));
                            }
                        }

                        create(confAlias, holder, pholder, callbackContext);
                    } catch (Exception e) {
                        e.printStackTrace();
                        callbackContext.error(e.getMessage());
                    }
                    break;
                case "broadcast":
                    try {
                        String confId = args.getString(0);
                        Log.d(TAG, "execute: broadcast");
                        broadcast(confId, callbackContext);
                    } catch (Exception e) {
                        e.printStackTrace();
                        callbackContext.error(e.getMessage());
                    }
                    break;
                case "join":
                    try {
                        boolean listener = false;
                        String confId = args.getString(0);
                        if (!args.isNull(1)) {
                            try {
                                JSONObject object = args.optJSONObject(1);
                                if (null != object) {
                                    JSONObject params_user = object.optJSONObject("user");
                                    if (null != params_user) {
                                        String userType = params_user.optString("type");
                                        listener = "listener".equals(userType);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        Log.d(TAG, "execute: listener := " + listener);
                        if (listener) {
                            listen(confId, callbackContext);
                        } else {
                            join(confId, callbackContext);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        callbackContext.error(e.getMessage());
                    }
                    break;
                case "invite":
                    try {
                        String conferenceId = null;
                        if (!args.isNull(0)) conferenceId = args.getString(0);
                        JSONArray array = null;
                        if (!args.isNull(1)) array = args.getJSONArray(1);

                        List<ParticipantInfo> participants = new ArrayList<>();
                        if (null != array) {
                            JSONObject object;
                            int index = 0;
                            while (index < array.length()) {
                                object = array.getJSONObject(index);

                                participants.add(new ParticipantInfo(object.getString("name"),
                                        object.getString("externalId"),
                                        object.getString("avatarUrl")));

                                index++;
                            }
                        }

                        invite(conferenceId, participants, callbackContext);
                    } catch (Exception e) {
                        e.printStackTrace();
                        callbackContext.error(e.getMessage());
                    }
                    break;
                case "setUIConfiguration":
                    try {
                        JSONObject object = args.getJSONObject(0);
                        setUiConfiguration(object);
                        callbackContext.success();
                    } catch (Exception e) {
                        e.printStackTrace();
                        callbackContext.error("Wrong configuration or issue with it");
                    }
                    break;
                case "startConference":
                    callbackContext.error("Not implemented anymore, see create->join->invite");
                    break;
                case "leave":
                case "stopConference":
                    stopConference(callbackContext);
                    break;
                //create -> objet key -> value (value->key->value)
                //join
                //leave
                case "startRecording":
                    startRecording(callbackContext);
                    break;
                case "stopRecording":
                    stopRecording(callbackContext);
                    break;
                case "minimize":
                    minimize(callbackContext);
                    break;
                case "maximize":
                    maximize(callbackContext);
                    break;
                case "setAudio3DEnabled":
                    setAudio3DEnabled(args.getBoolean(0));
                    callbackContext.success();
                    break;
                case "setTelecomMode":
                    setTelecomMode(args.getBoolean(0));
                    callbackContext.success();
                    break;
                case "appearMaximized":
                    appearMaximized(args.getBoolean(0));
                    callbackContext.success();
                    break;
                case "defaultVideo":
                    defaultVideo(args.getBoolean(0));
                    callbackContext.success();
                case "defaultBuiltInSpeaker":
                    defaultBuiltInSpeaker(args.getBoolean(0));
                    callbackContext.success();
                    break;
                case "screenAutoLock":
                    screenAutoLock(args.getBoolean(0));
                    callbackContext.success();
                    break;
                case "sendBroadcastMessage":
                    sendBroadcastMessage(args.getString(0), callbackContext);
                    break;
                case "onConferenceStatusUpdatedEvent":
                    onConferenceStatusUpdatedEventCallback = callbackContext;
                    break;
                default:
                    return false;
            }

            return true; //default return false - so true is ok
        }
        return false;
    }

    private void setUiConfiguration(JSONObject object) {
        JSONObject actionBar = jsonObject(object, "actionBar");
        JSONObject overlay = jsonObject(object, "overlay");
        JSONObject users = jsonObject(object, "users");

        Configuration configuration = VoxeetToolkit.instance().getConferenceToolkit().Configuration;
        ActionBar cactionBar = configuration.ActionBar;
        Overlay coverlay = configuration.Overlay;
        Users cusers = configuration.Users;
        if (null != actionBar) {
            if (actionBar.has("displayMute"))
                cactionBar.displayMute = bool(object, "displayMute");
            if (actionBar.has("displaySpeaker"))
                cactionBar.displaySpeaker = bool(object, "displaySpeaker");
            if (actionBar.has("displayCamera"))
                cactionBar.displayCamera = bool(object, "displayCamera");
            if (actionBar.has("displayScreenShare"))
                cactionBar.displayScreenShare = bool(object, "displayScreenShare");
            if (actionBar.has("displayLeave"))
                cactionBar.displayLeave = bool(object, "displayLeave");
        }
        if (null != overlay) {
            if (object.has("backgroundMaximizedColor"))
                coverlay.background_maximized_color = integer(object, "backgroundMaximizedColor");
            if (actionBar.has("backgroundMinimizedColor"))
                coverlay.background_minimized_color = integer(object, "backgroundMinimizedColor");
        }
        if (null != users) {
            if (object.has("speakingUserColor"))
                cusers.speaking_user_color = integer(object, "speakingUserColor");
            if (actionBar.has("selectedUserColor"))
                cusers.selected_user_color = integer(object, "selectedUserColor");
        }
    }

    private boolean bool(@Nullable JSONObject object, @NonNull String key) {
        if (null == object) return false;
        try {
            return object.optBoolean(key, false);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private int integer(@Nullable JSONObject object, @NonNull String key) {
        if (null == object) return 0;
        try {
            return object.optInt(key, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private String string(@Nullable JSONObject object, @NonNull String key) {
        if (null == object) return null;
        try {
            return object.optString(key, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    private JSONObject jsonObject(@Nullable JSONObject object, @NonNull String key) {
        if (null == object) return null;
        try {
            return object.optJSONObject(key);
        } catch (Exception e) {
            return null;
        }
    }

    @Nullable
    private void defaultVideo(boolean startVideo) {
        startVideoOnJoin = startVideo;
        VoxeetPreferences.setDefaultVideoOn(startVideo);
    }


    private void initialize(final String accessToken,
                            final CallbackContext callbackContext) {
        HANDLER.post(() -> {
            if (!VoxeetSDK.instance().isInitialized()) {
                VoxeetSDK.initialize(accessToken,
                        (required, callback) -> {
                            lock(lockAwaitingToken);
                            if (!mAwaitingTokenCallback.contains(callback)) {
                                mAwaitingTokenCallback.add(callback);
                            }
                            unlock(lockAwaitingToken);
                            postRefreshAccessToken();
                        });

                VoxeetSDK.instance().register(VoxeetCordova.this);
                internalInitialize(callbackContext, cordova.getActivity());
            } else {
                VoxeetSDK.instance().register(VoxeetCordova.this);
                callbackContext.success();
            }
        });
    }

    private void initialize(final String consumerKey,
                            final String consumerSecret,
                            final CallbackContext callbackContext) {
        HANDLER.post(() -> {
            if (!VoxeetSDK.instance().isInitialized()) {
                VoxeetSDK.initialize(consumerKey, consumerSecret);

                VoxeetSDK.instance().register(VoxeetCordova.this);
                internalInitialize(callbackContext, cordova.getActivity());
            } else {
                VoxeetSDK.instance().register(VoxeetCordova.this);
                callbackContext.success();
            }
        });
    }

    private static void internalInitialize(@Nullable final CallbackContext callbackContext, @NonNull Activity activity) {
        VoxeetSDK.conference().ConferenceConfigurations.TelecomWaitingForParticipantTimeout = -1; //no timeout by default in the cordova impl

        VoxeetCordova.initNotificationCenter(activity);

        try {
            VoxeetSDK.notification().subscribe(new SubscribeInvitation()).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

        TelemetryService.register(SdkEnvironment.CORDOVA, VERSION);

        Application application = (Application) activity.getApplicationContext();

        //set the 2 optional default configuration from previous saved state
        VoxeetCordova.startVideoOnJoin = VoxeetPreferences.isDefaultVideoOn();
        defaultBuiltInSpeaker(VoxeetPreferences.isDefaultBuiltinSpeakerOn());

        VoxeetToolkit
                .initialize(application, EventBus.getDefault())
                .enableOverlay(true);

        VoxeetToolkit.instance().getConferenceToolkit().setScreenShareEnabled(false).enable(true);

        if (null != callbackContext) callbackContext.success();
    }

    private void openSession(final ParticipantInfo userInfo,
                             final CallbackContext cb) {
        HANDLER.post(() -> {
            if (!VoxeetSDK.instance().isInitialized()) {
                cb.error(ERROR_SDK_NOT_INITIALIZED);
                return;
            }

            //if we are trying to connect the same user !
            if (isConnected() && isSameUser(userInfo)) {
                cb.success();
                return;
            }

            _log_in_callback = cb;
            if (_current_user == null) {
                _current_user = userInfo;
                logSelectedUser();
            } else {
                _current_user = userInfo;
                //we have an user
                VoxeetSDK.session().close()
                        .then((result, solver) -> logSelectedUser())
                        .error(error -> logSelectedUser());
            }
        });
    }

    /**
     * Call this method to log the current selected user
     */
    public void logSelectedUser() {
        SessionService service = VoxeetSDK.session();
        if (VoxeetSDK.instance().isInitialized()) {
            service.open(_current_user)
                    .then((result, solver) -> {
                        //TODO possibility here to add management for user to be socket managed here
                    })
                    .error(error -> {
                        //error
                    });
        }
    }

    @Nullable
    public ParticipantInfo getCurrentUser() {
        return _current_user;
    }

    private void closeSession(final CallbackContext cb) {
        HANDLER.post(() -> VoxeetSDK.session().close()
                .then((aBoolean, solver) -> {
                    _current_user = null;
                    cb.success();
                })
                .error(throwable -> {
                    _current_user = null;
                    cb.error("Error while logging out with the server: " + throwable.getMessage());
                }));
    }

    private void isUserLoggedIn(final CallbackContext cb) {
        HANDLER.post(() -> {
            boolean logged_in = VoxeetSDK.session().isSocketOpen();
            cb.sendPluginResult(new PluginResult(PluginResult.Status.OK, logged_in));
        });
    }

    private void isAudio3DEnabled(final CallbackContext cb) {
        HANDLER.post(() -> {
            boolean enabled = false;//TODO use spatial placement
            cb.sendPluginResult(new PluginResult(PluginResult.Status.OK, enabled));
        });
    }

    private void isTelecomMode(final CallbackContext cb) {
        HANDLER.post(() -> {
            boolean enabled = VoxeetSDK.conference().ConferenceConfigurations.telecomMode;

            cb.sendPluginResult(new PluginResult(PluginResult.Status.OK, enabled));
        });
    }

    private void checkForAwaitingConference(@Nullable final CallbackContext cb) {
        HANDLER.post(() -> {
            lock();
            if (!VoxeetSDK.instance().isInitialized()) {
                if (null != cb) cb.error(ERROR_SDK_NOT_INITIALIZED);
            } else {
                DefaultIncomingBundleChecker checker = CordovaIncomingCallActivity.CORDOVA_ROOT_BUNDLE;

                if (null != CordovaIncomingCallActivity.CORDOVA_AWAITING_BUNDLE_TO_BE_MANAGE_FOR_LAUNCH_ACCEPT) {
                    DefaultIncomingBundleChecker bundle = CordovaIncomingCallActivity.CORDOVA_AWAITING_BUNDLE_TO_BE_MANAGE_FOR_LAUNCH_ACCEPT;

                    InvitationBundle invitationBundle = CallUtils.createInvitationBundle(bundle);

                    NotificationCenter.instance.onInvitationReceived(cordova.getActivity(), invitationBundle);
                } else if (null != CordovaIncomingCallActivity.CORDOVA_AWAITING_BUNDLE_TO_BE_MANAGE_FOR_ACCEPT) {
                    CordovaIncomingCallActivity.CORDOVA_AWAITING_BUNDLE_TO_BE_MANAGE_FOR_ACCEPT.onAccept();
                } else if (null != CordovaIncomingCallActivity.CORDOVA_AWAITING_BUNDLE_TO_BE_MANAGE_FOR_DECLINE) {
                    CordovaIncomingCallActivity.CORDOVA_AWAITING_BUNDLE_TO_BE_MANAGE_FOR_DECLINE.onDecline();
                } else if (null != checker && checker.isBundleValid()) {
                    if (VoxeetSDK.session().isSocketOpen()) {
                        checker.onAccept();
                        CordovaIncomingCallActivity.CORDOVA_ROOT_BUNDLE = null;
                        if (null != cb) cb.success();
                    } else {
                        if (null != cb) cb.error(ERROR_SDK_NOT_LOGGED_IN);
                    }
                } else {
                    if (null != cb) cb.success();
                }

                cleanBundles();
            }
            unlock();
        });
    }

    private void invite(final String conferenceId,
                        final List<ParticipantInfo> participants,
                        final CallbackContext cb) {
        HANDLER.post(() -> VoxeetSDK.conference().invite(conferenceId, participants)
                .then((result, solver) -> cb.success())
                .error(throwable -> cb.error("Error while initializing the conference: " + throwable.getMessage())));
    }

    private void create(String conferenceAlias,
                        MetadataHolder metadataHolder,
                        ParamsHolder paramsHolder, final CallbackContext cb) {
        ConferenceCreateOptions conferenceCreateOptions = new ConferenceCreateOptions.Builder()
                .setConferenceAlias(conferenceAlias)
                .setMetadataHolder(metadataHolder)
                .setParamsHolder(paramsHolder)
                .build();

        VoxeetSDK.conference().create(conferenceCreateOptions)
                .then((result) -> {
                    JSONObject object = new JSONObject();
                    try {
                        object.put("conferenceId", result.getId());
                        object.put("conferenceAlias", result.getAlias());
                        object.put("isNew", result.isNew());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    cb.success(object);
                })
                .error(error -> cb.error("Error while creating the conference " + conferenceAlias + ": " + error.getMessage()));
    }

    private void broadcast(@NonNull String conferenceId, @NonNull final CallbackContext cb) {
        Context context = mWebView.getContext();
        Log.d(TAG, "broadcast: broadcasting conference");
        Conference conference = VoxeetSDK.conference().getConference(conferenceId);

        CordovaPermissionHelper.requestDefaultPermission().then((ThenPromise<Boolean, Conference>) ok -> {

            if (!ok) throw new IllegalStateException("no mic permission");
            VoxeetToolkit.instance().enable(VoxeetToolkit.instance().getConferenceToolkit());

            return VoxeetSDK.conference().broadcast(conference);
        }).then((result) -> {
            cleanBundles();

            if (startVideoOnJoin) {
                startVideo(null);
            }

            cb.success();
        }).error(error -> cb.error("Error while joining the conference " + conferenceId + ": " + error.getMessage()));
    }

    private void join(@NonNull String conferenceId, @NonNull final CallbackContext cb) {
        Log.d(TAG, "join: joining conference");
        Conference conference = VoxeetSDK.conference().getConference(conferenceId);
        ConferenceJoinOptions options = new ConferenceJoinOptions.Builder(conference).build();

        CordovaPermissionHelper.requestDefaultPermission().then((ThenPromise<Boolean, Conference>) ok -> {
            if (!ok) throw new IllegalStateException("no mic permission");
            VoxeetToolkit.instance().enable(VoxeetToolkit.instance().getConferenceToolkit());

            return VoxeetSDK.conference().join(options);
        }).then((result) -> {
            cleanBundles();

            if (startVideoOnJoin) {
                startVideo(null);
            }
            cb.success();
        }).error(error -> cb.error("Error while joining the conference " + conferenceId + ": " + error.getMessage()));
    }

    private void listen(@NonNull String conferenceId, @NonNull final CallbackContext cb) {
        Conference conference = VoxeetSDK.conference().getConference(conferenceId);
        ConferenceListenOptions options = new ConferenceListenOptions.Builder(conference)
                .build();

        VoxeetSDK.conference().listen(options)
                .then(result -> {
                    cleanBundles();
                    cb.success();
                })
                .error(error -> cb.error("Error while joining the conference " + conferenceId + ": " + error.getMessage()));
    }

    private void startVideo(final CallbackContext cb) {
        VoxeetSDK.conference().startVideo().then((result) -> {
            if (null != cb) cb.success();
        }).error(error -> {
            if (null != cb) cb.error("Error while starting video: " + error.getMessage());
        });
    }

    private void stopConference(final CallbackContext cb) {
        HANDLER.post(() -> VoxeetSDK.conference().leave()
                .then((ThenVoid<Boolean>) (bool) -> cb.success())
                .error(throwable -> cb.error("Error while leaving: " + throwable.getMessage())));
    }

    private void startRecording(final CallbackContext cb) {
        HANDLER.post(() -> VoxeetSDK.recording().start()
                .then((ThenVoid<Boolean>) (bool) -> cb.success())
                .error(throwable -> cb.error("Error while start recording: " + throwable.getMessage())));
    }

    private void stopRecording(final CallbackContext cb) {
        HANDLER.post(() -> VoxeetSDK.recording().stop()
                .then((ThenVoid<Boolean>) (bool) -> cb.success())
                .error(throwable -> cb.error("Error while stop recording: " + throwable.getMessage())));
    }

    private void minimize(final CallbackContext cb) {
        VoxeetToolkit.instance().getConferenceToolkit().minimize();
        if (cb != null) cb.success();
    }

    private void maximize(final CallbackContext cb) {
        VoxeetToolkit.instance().getConferenceToolkit().maximize();
        if (cb != null) cb.success();
    }

    private void sendBroadcastMessage(final String message, final CallbackContext cb) {
        String conferenceId = VoxeetSDK.conference().getConferenceId();
        if (null == conferenceId || TextUtils.isEmpty(conferenceId)) {
            cb.error("You're not in a conference");
            return;
        }

        HANDLER.post(() -> VoxeetSDK.command().send(conferenceId, message)
                .then((ThenVoid<Boolean>) (bool) -> cb.success())
                .error(throwable -> cb.error("Error while sending the message to the server: " + throwable.getMessage())));
    }

    private void setAudio3DEnabled(boolean enabled) {
        //TODO no-op
    }

    private void setTelecomMode(boolean telecomMode) {
        VoxeetSDK.conference().ConferenceConfigurations.telecomMode = telecomMode;
    }

    private void appearMaximized(final Boolean enabled) {
        if (null == VoxeetToolkit.instance()) {
            return;
        }

        HANDLER.post(() -> VoxeetToolkit.instance()
                .getConferenceToolkit()
                .setDefaultOverlayState(enabled ? OverlayState.EXPANDED
                        : OverlayState.MINIMIZED));
    }

    private static void defaultBuiltInSpeaker(final boolean enabled) {
        HANDLER.post(() -> {
            //comment lines introducing a switch in the mode
            //AudioRoute route = AudioRoute.ROUTE_PHONE;
            //if (enabled) route = AudioRoute.ROUTE_SPEAKER;

            VoxeetPreferences.setDefaultBuiltInSpeakerOn(enabled);

            //set the contextual information which will be used by the UXKit when he join event will be fired
            VoxeetToolkit.instance().getConferenceToolkit().Configuration.Contextual.default_speaker_on = enabled;
        });
    }

    private void screenAutoLock(Boolean enabled) {
        //TODO not available in the current sdk
    }

    private void postRefreshAccessToken() {
        Log.d("VoxeetCordova", "postRefreshAccessToken: sending call to javascript to refresh token");
        if (null != refreshAccessTokenCallbackInstance) {
            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK);
            pluginResult.setKeepCallback(true);
            refreshAccessTokenCallbackInstance.sendPluginResult(pluginResult);
        }
    }

    private void onAccessTokenOk(final String accessToken,
                                 final CallbackContext callbackContext) {
        lock(lockAwaitingToken);
        for (TokenCallback callback : mAwaitingTokenCallback) {
            try {
                callback.ok(accessToken);
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }
        unlock(lockAwaitingToken);
        callbackContext.success();
    }

    private void onAccessTokenKo(final String reason,
                                 final CallbackContext callbackContext) {
        try {
            throw new Exception("refreshToken failed with reason := " + reason);
        } catch (Exception e) {
            lock(lockAwaitingToken);
            for (TokenCallback callback : mAwaitingTokenCallback) {
                try {
                    callback.error(e);
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
            }
            unlock(lockAwaitingToken);
        }
        callbackContext.success();
    }

    private void refreshAccessTokenCallback(final CallbackContext callbackContext) {
        refreshAccessTokenCallbackInstance = callbackContext;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(SocketStateChangeEvent event) {
        switch (event.state) {
            case CONNECTED:
                if (null != _log_in_callback) {
                    _log_in_callback.success();
                    _log_in_callback = null;

                    VoxeetCordova.checkForIncomingConference();
                }
                break;
            case CLOSING:
            case CLOSED:
                if (null != _log_in_callback) {
                    _log_in_callback.error("Error while logging in");
                    _log_in_callback = null;

                    cancelIncomingConference();
                }
        }
    }

    private boolean isConnected() {
        SessionService service = VoxeetSDK.session();
        return service.isSocketOpen();
    }

    private boolean isSameUser(@NonNull ParticipantInfo userInfo) {
        ParticipantInfo currentUser = getCurrentUser();
        String externalId = userInfo.getExternalId();
        if (null == currentUser || null == externalId) return false;
        return externalId.equals(currentUser.getExternalId());
    }

    public static boolean checkForIncomingConference() {
        DefaultIncomingBundleChecker checker = CordovaIncomingCallActivity.CORDOVA_ROOT_BUNDLE;
        return VoxeetCordova.checkForIncomingConference(checker);
    }

    public static boolean checkForIncomingConference(@Nullable DefaultIncomingBundleChecker checker) {
        Log.d(TAG, "checkForIncomingConference: checker := " + checker);
        SessionService service = VoxeetSDK.session();
        if (null != checker && checker.isBundleValid()) {
            ParticipantInfo userInfo = VoxeetPreferences.getSavedUserInfo();

            Log.d(TAG, "checkForIncomingConference: socket opened := " + service.isOpen());
            if (service.isOpen()) {
                Log.d(TAG, "checkForIncomingConference: direct onAccept()");
                checker.onAccept();
                CordovaIncomingCallActivity.CORDOVA_ROOT_BUNDLE = null;
                return true;
            } else if (null != userInfo) {
                Log.d(TAG, "checkForIncomingConference: user infos saved := login");
                service.open(userInfo).then((result) -> {
                    Log.d(TAG, "onCall: session opened");
                    checker.onAccept();
                }).error(error -> {
                    Log.d(TAG, "onError: unable to join from bundle via push notification");
                    error.printStackTrace();
                });
                CordovaIncomingCallActivity.CORDOVA_ROOT_BUNDLE = null;
                return true;
            } else {
                Log.d(TAG, "checkForIncomingConference: no user infos saved");
                return false;
            }
        } else {
            Log.d(TAG, "checkForIncomingConference: invalid bundle or sdk not initialized");
        }
        return true;
    }

    private void cancelIncomingConference() {
        CordovaIncomingCallActivity.CORDOVA_ROOT_BUNDLE = null;
    }

    private void lock(ReentrantLock lock) {
        try {
            lock.lock();
        } catch (Exception e) {

        }
    }

    private void unlock(ReentrantLock lock) {
        try {
            if (lock.isLocked())
                lock.unlock();
        } catch (Exception e) {

        }
    }

    private void lock() {
        lock(lock);
    }

    private void unlock() {
        unlock(lock);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ParticipantAddedEvent event) {
        setVolumeVoiceCall();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ConferenceStatusUpdatedEvent event) {
        switch (event.state) {
            case CREATING:
            case CREATED:
            case JOINING:
                setVolumeVoiceCall();
                break;
            case JOINED:
                Log.d(TAG, "onEvent: ConferenceJoinedSuccessEvent :: removing the various bundles");
                setVolumeVoiceCall();
                cleanBundles();
                break;
            case ERROR:
                setVolumeMusic();
                cleanBundles();
                break;
            case LEAVING:
            case LEFT:
            default:
        }
        setVolumeMusic();

        if (onConferenceStatusUpdatedEventCallback != null) {
            try {
                JSONObject jObject = new JSONObject()
                        .put("status", event.state.toString())
                        .put("conferenceAlias", event.conferenceAlias);
                if (event.conference != null) {
                    jObject.put("conferenceId", event.conference.getId());
                }

                PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, jObject);
                pluginResult.setKeepCallback(true);
                onConferenceStatusUpdatedEventCallback.sendPluginResult(pluginResult);
            } catch (Exception ex) {
                Log.e(TAG, "onEvent: ConferenceJoinedSuccessEvent", ex);
            }
        }
    }

    private void setVolumeVoiceCall() {
        if (Build.MANUFACTURER.equals("samsung")) {
            Log.d(TAG, "setVolumeVoiceCall not to be used with samsung devices. Report issue if side effect");
        }

        cordova.getActivity().setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
        if (null != AudioService.getSoundManager()) {
            AudioService.getSoundManager().unsetMediaRoute().enable().requestAudioFocus();
        }
    }

    private void setVolumeMusic() {
        if (null != AudioService.getSoundManager()) {
            AudioService.getSoundManager().abandonAudioFocusRequest();
        }
    }

    private void cleanBundles() {
        //and finally clear them
        CordovaIncomingCallActivity.CORDOVA_ROOT_BUNDLE = null;
        CordovaIncomingCallActivity.CORDOVA_AWAITING_BUNDLE_TO_BE_MANAGE_FOR_ACCEPT = null;
        CordovaIncomingCallActivity.CORDOVA_AWAITING_BUNDLE_TO_BE_MANAGE_FOR_DECLINE = null;
        CordovaIncomingCallActivity.CORDOVA_AWAITING_BUNDLE_TO_BE_MANAGE_FOR_LAUNCH_ACCEPT = null;
    }

    private static boolean isEmpty(@Nullable String str) {
        return null == str || TextUtils.isEmpty(str) || "null".equalsIgnoreCase(str);
    }
}
