package com.voxeet.toolkit;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.voxeet.android.media.audio.AudioRoute;
import com.voxeet.toolkit.controllers.VoxeetToolkit;
import com.voxeet.toolkit.implementation.overlays.OverlayState;
import com.voxeet.toolkit.notification.CordovaIncomingBundleChecker;
import com.voxeet.toolkit.notification.CordovaIncomingCallActivity;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import eu.codlab.simplepromise.Promise;
import eu.codlab.simplepromise.solve.ErrorPromise;
import eu.codlab.simplepromise.solve.PromiseExec;
import eu.codlab.simplepromise.solve.Solver;
import voxeet.com.sdk.core.FirebaseController;
import voxeet.com.sdk.core.VoxeetSdk;
import voxeet.com.sdk.core.preferences.VoxeetPreferences;
import voxeet.com.sdk.events.success.ConferenceRefreshedEvent;
import voxeet.com.sdk.events.success.SocketConnectEvent;
import voxeet.com.sdk.events.success.SocketStateChangeEvent;
import voxeet.com.sdk.json.UserInfo;

/**
 * Voxeet implementation for Cordova
 */

public class VoxeetCordova extends CordovaPlugin {

    private static final String ERROR_SDK_NOT_INITIALIZED = "ERROR_SDK_NOT_INITIALIZED";
    private static final String ERROR_SDK_NOT_LOGGED_IN = "ERROR_SDK_NOT_LOGGED_IN";

    private final Handler mHandler;
    private UserInfo _current_user;
    private CallbackContext _log_in_callback;

    public VoxeetCordova() {
        super();
        mHandler = new Handler(Looper.getMainLooper());

        Promise.setHandler(mHandler);
    }

    @Override
    public void onResume(boolean multitasking) {
        super.onResume(multitasking);
    }

    @Override
    public void onPause(boolean multitasking) {
        super.onPause(multitasking);
    }

    @Override
    public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) throws JSONException {

        if (action != null) {
            switch (action) {
                case "initialize":
                    initialize(args.getString(0),
                            args.getString(1));
                    callbackContext.success();
                    break;
                case "openSession":
                    JSONObject userInfo = null;
                    if (!args.isNull(0)) userInfo = args.getJSONObject(0);
                    UserInfo user = null;

                    if (null != userInfo) {
                        user = new UserInfo(userInfo.getString("name"),
                                userInfo.getString("externalId"),
                                userInfo.getString("avatarUrl"));
                    }

                    openSession(user, callbackContext);
                    break;
                case "closeSession":
                    closeSession(callbackContext);
                    break;
                case "isUserLoggedIn":
                    isUserLoggedIn(callbackContext);
                    break;
                case "checkForAwaitingConference":
                    checkForAwaitingConference(callbackContext);
                    break;
                case "startConference":
                    try {
                        String confId = args.getString(0);
                        JSONArray array = null;
                        if (!args.isNull(1)) array = args.getJSONArray(1);

                        List<UserInfo> participants = new ArrayList<>();
                        if (null != array) {
                            JSONObject object;
                            int index = 0;
                            while (index < array.length()) {
                                object = array.getJSONObject(index);

                                participants.add(new UserInfo(object.getString("name"),
                                        object.getString("externalId"),
                                        object.getString("avatarUrl")));

                                index++;
                            }
                        }

                        startConference(confId, participants, callbackContext);
                    } catch (Exception e) {
                        e.printStackTrace();
                        callbackContext.error(e.getMessage());
                    }
                    break;
                case "stopConference":
                    stopConference(callbackContext);
                    break;
                case "appearMaximized":
                    appearMaximized(args.getBoolean(0));
                    callbackContext.success();
                    break;
                case "defaultBuildInSpeaker":
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
                default:
                    return false;
            }

            return true; //default return false - so true is ok
        }
        return false;
    }


    private void initialize(final String consumerKey,
                            final String consumerSecret) {

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Application application = (Application) cordova.getActivity().getApplicationContext();

                if (null == VoxeetSdk.getInstance()) {
                    VoxeetSdk.initialize(application,
                            consumerKey, consumerSecret, null);
                    VoxeetSdk.getInstance().getConferenceService().setTimeOut(30 * 1000); //30s
                }

                //also enable the push token upload and log
                FirebaseController.getInstance()
                        .log(true)
                        .enable(true)
                        .createNotificationChannel(application);

                //reset the incoming call activity, in case the SDK was no initialized, it would have
                //erased this method call
                VoxeetPreferences.setDefaultActivity(CordovaIncomingCallActivity.class.getCanonicalName());

                VoxeetToolkit
                        .initialize(application, EventBus.getDefault())
                        .enableOverlay(true);

                VoxeetSdk.getInstance().register(application, VoxeetCordova.this);
            }
        });
    }

    private void openSession(final UserInfo userInfo,
                             final CallbackContext cb) {

        mHandler.post(new Runnable() {
            @Override
            public void run() {

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
                    VoxeetSdk.getInstance()
                            .logout()
                            .then(new PromiseExec<Boolean, Object>() {
                                @Override
                                public void onCall(@Nullable Boolean result, @NonNull Solver<Object> solver) {
                                    logSelectedUser();
                                }
                            })
                            .error(new ErrorPromise() {
                                @Override
                                public void onError(Throwable error) {
                                    logSelectedUser();
                                }
                            });
                }
            }
        });
    }

    /**
     * Call this method to log the current selected user
     */
    public void logSelectedUser() {
        VoxeetSdk.getInstance().logUser(_current_user);
    }

    public UserInfo getCurrentUser() {
        return _current_user;
    }

    private void closeSession(final CallbackContext cb) {

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                VoxeetSdk.getInstance()
                        .logout()
                        .then(new PromiseExec<Boolean, Object>() {
                            @Override
                            public void onCall(@Nullable Boolean aBoolean, @NonNull Solver<Object> solver) {
                                _current_user = null;
                                cb.success();
                            }
                        })
                        .error(new ErrorPromise() {
                            @Override
                            public void onError(@NonNull Throwable throwable) {
                                _current_user = null;
                                cb.error("Error while logging out with the server");
                            }
                        });
            }
        });
    }

    private void isUserLoggedIn(final CallbackContext cb) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                boolean logged_in = false;
                if (null != VoxeetSdk.getInstance()) {
                    logged_in = VoxeetSdk.getInstance().isSocketOpen();
                }

                cb.sendPluginResult(new PluginResult(PluginResult.Status.OK, logged_in));
            }
        });
    }

    private void checkForAwaitingConference(final CallbackContext cb) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (null == VoxeetSdk.getInstance()) {
                    cb.error(ERROR_SDK_NOT_INITIALIZED);
                } else {
                    CordovaIncomingBundleChecker checker = CordovaIncomingCallActivity.CORDOVA_ROOT_BUNDLE;
                    if (null != checker && checker.isBundleValid()) {
                        if (VoxeetSdk.getInstance().isSocketOpen()) {
                            checker.onAccept();
                            CordovaIncomingCallActivity.CORDOVA_ROOT_BUNDLE = null;
                            cb.success();
                        } else {
                            cb.error(ERROR_SDK_NOT_LOGGED_IN);
                        }
                    } else {
                        cb.success();
                    }
                }
            }
        });
    }

    private void startConference(final String conferenceId,
                                 final List<UserInfo> participants,
                                 final CallbackContext cb) {

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                VoxeetToolkit.getInstance()
                        .getConferenceToolkit()
                        .join(conferenceId)
                        .then(new PromiseExec<Boolean, List<ConferenceRefreshedEvent>>() {
                            @Override
                            public void onCall(@Nullable Boolean aBoolean, @NonNull final Solver<List<ConferenceRefreshedEvent>> solver) {
                                if (null != participants && participants.size() > 0) {
                                    solver.resolve(VoxeetToolkit.getInstance()
                                            .getConferenceToolkit()
                                            .invite(participants));
                                } else {
                                    solver.resolve(new ArrayList<ConferenceRefreshedEvent>());
                                }
                            }
                        })
                        .then(new PromiseExec<List<ConferenceRefreshedEvent>, Object>() {
                            @Override
                            public void onCall(@Nullable List<ConferenceRefreshedEvent> conferenceRefreshedEvents, @NonNull Solver<Object> solver) {
                                cb.success();
                            }
                        })
                        .error(new ErrorPromise() {
                            @Override
                            public void onError(@NonNull Throwable throwable) {
                                cb.error("Error whilte initializing the conference");
                            }
                        });
            }
        });
    }

    private void stopConference(final CallbackContext cb) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                VoxeetSdk.getInstance()
                        .getConferenceService()
                        .leave()
                        .then(new PromiseExec<Boolean, Object>() {
                            @Override
                            public void onCall(@Nullable Boolean bool, @NonNull Solver<Object> solver) {
                                cb.success();
                            }
                        })
                        .error(new ErrorPromise() {
                            @Override
                            public void onError(@NonNull Throwable throwable) {
                                cb.error("Error while leaving");
                            }
                        });
            }
        });
    }

    private void sendBroadcastMessage(final String message, final CallbackContext cb) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                VoxeetSdk.getInstance()
                        .getConferenceService()
                        .sendBroadcastMessage(message)
                        .then(new PromiseExec<Boolean, Object>() {
                            @Override
                            public void onCall(@Nullable Boolean aBoolean, @NonNull Solver<Object> solver) {
                                cb.success();
                            }
                        })
                        .error(new ErrorPromise() {
                            @Override
                            public void onError(@NonNull Throwable throwable) {
                                cb.error("Error while sending the message to the server");
                            }
                        });
            }
        });
    }

    private void add(/* participant */) {
        //TODO not available in the current SDK
    }

    private void update(/* participant */) {
        //TODO not available in the current SDK
    }

    private void remove(/* participant */) {
        //TODO not available in the current SDK
    }

    private void appearMaximized(final Boolean enabled) {
        VoxeetToolkit.getInstance()
                .getConferenceToolkit()
                .setDefaultOverlayState(enabled ? OverlayState.EXPANDED
                        : OverlayState.MINIMIZED);
    }

    private void defaultBuiltInSpeaker(final boolean enabled) {
        AudioRoute route = AudioRoute.ROUTE_PHONE;
        if (enabled) route = AudioRoute.ROUTE_SPEAKER;

        VoxeetSdk.getInstance().getAudioService().setAudioRoute(route);
    }

    private void screenAutoLock(Boolean enabled) {
        //TODO not available in the current sdk
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(final SocketConnectEvent event) {
        if (null != _log_in_callback) {
            _log_in_callback.success();
            _log_in_callback = null;

            checkForIncomingConference();
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(SocketStateChangeEvent event) {
        switch (event.message()) {
            case "CLOSING":
            case "CLOSED":
                if (null != _log_in_callback) {
                    _log_in_callback.error("Error while logging in");
                    _log_in_callback = null;

                    cancelIncomingConference();
                }
        }
    }

    private boolean isConnected() {
        return VoxeetSdk.getInstance() != null
                && VoxeetSdk.getInstance().isSocketOpen();
    }

    private boolean isSameUser(@NonNull UserInfo userInfo) {
        return userInfo.getExternalId().equals(getCurrentUser());
    }

    private boolean checkForIncomingConference() {
        CordovaIncomingBundleChecker checker = CordovaIncomingCallActivity.CORDOVA_ROOT_BUNDLE;
        if (null != checker && checker.isBundleValid()) {
            if (VoxeetSdk.getInstance().isSocketOpen()) {
                checker.onAccept();
                CordovaIncomingCallActivity.CORDOVA_ROOT_BUNDLE = null;
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    private void cancelIncomingConference() {
        CordovaIncomingCallActivity.CORDOVA_ROOT_BUNDLE = null;
    }
}
