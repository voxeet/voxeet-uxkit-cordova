package com.voxeet.toolkit;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.voxeet.sdk.core.VoxeetSdk;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONException;

import eu.codlab.simplepromise.Promise;
import eu.codlab.simplepromise.solve.ErrorPromise;
import eu.codlab.simplepromise.solve.PromiseExec;
import eu.codlab.simplepromise.solve.Solver;

/**
 * Voxeet's Media layer implementation for Cordova
 */

public class VoxeetMediaCordova extends CordovaPlugin {

    private static final String TAG = VoxeetMediaCordova.class.getSimpleName();

    private final Handler mHandler;

    public VoxeetMediaCordova() {
        super();
        mHandler = new Handler(Looper.getMainLooper());

        Promise.setHandler(mHandler);
    }

    @Override
    public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        Log.d("VoxeetMediaCordova", "execute: request " + action);

        if (action != null) {
            switch (action) {
                case "startVideo":
                    startVideo(args.getBoolean(0), callbackContext);
                    break;
                case "stopVideo":
                    stopVideo(callbackContext);
                    break;
                case "switchCamera":
                    switchCamera(callbackContext);
                    break;
                //case "isDefaultFrontFacing":
                //    isDefaultFrontFacing(callbackContext);
                //    break;
                //case "isFrontCamera":
                //    isFrontCamera(callbackContext);
                //    break;
                default:
                    return false;
            }

            return true; //default return false - so true is ok
        }
        return false;
    }

    private void startVideo(final boolean front, final CallbackContext cb) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                VoxeetSdk.getInstance().getConferenceService()
                        .startVideo(front)
                        .then(new PromiseExec<Boolean, Object>() {
                            @Override
                            public void onCall(@Nullable Boolean result, @NonNull Solver<Object> solver) {
                                cb.success();
                            }
                        })
                        .error(new ErrorPromise() {
                            @Override
                            public void onError(@NonNull Throwable error) {
                                cb.error("Error while starting video");
                            }
                        });
            }
        });
    }

    private void stopVideo(final CallbackContext cb) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                VoxeetSdk.getInstance().getConferenceService()
                        .stopVideo()
                        .then(new PromiseExec<Boolean, Object>() {
                            @Override
                            public void onCall(@Nullable Boolean result, @NonNull Solver<Object> solver) {
                                cb.success();
                            }
                        })
                        .error(new ErrorPromise() {
                            @Override
                            public void onError(@NonNull Throwable error) {
                                cb.error("Error while stopping video");
                            }
                        });
            }
        });
    }

    private void switchCamera(final CallbackContext cb) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                VoxeetSdk.getInstance().getMediaService().switchCamera()
                        .then(new PromiseExec<Boolean, Object>() {
                            @Override
                            public void onCall(@Nullable Boolean result, @NonNull Solver<Object> solver) {
                                cb.success();
                            }
                        })
                        .error(new ErrorPromise() {
                            @Override
                            public void onError(@NonNull Throwable error) {
                                cb.error("Error while switching camera");
                            }
                        });
            }
        });
    }

    /*
    private void isDefaultFrontFacing(final CallbackContext cb) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                boolean enabled = false;
                if (null != VoxeetSdk.getInstance()) {
                    enabled = VoxeetSdk.getInstance().getMediaService().getCameraInformationProvider().isDefaultFrontFacing();
                }

                cb.sendPluginResult(new PluginResult(PluginResult.Status.OK, enabled));
            }
        });
    }
    */

    /*
    private void isFrontCamera(final CallbackContext cb) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                boolean enabled = false;
                if (null != VoxeetSdk.getInstance()) {
                    enabled = VoxeetSdk.getInstance().getMediaService().isFrontCamera();
                }

                cb.sendPluginResult(new PluginResult(PluginResult.Status.OK, enabled));
            }
        });
    }
    */
}
