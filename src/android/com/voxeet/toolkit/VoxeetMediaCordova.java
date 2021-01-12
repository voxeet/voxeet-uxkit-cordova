package com.voxeet.toolkit;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.voxeet.VoxeetSDK;
import com.voxeet.promise.Promise;
import com.voxeet.promise.solve.ThenVoid;
import com.voxeet.sdk.services.ConferenceService;
import com.voxeet.sdk.services.MediaDeviceService;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONException;

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
                ConferenceService service = VoxeetSDK.conference();
                if (null == service) {
                    cb.error("NOT INITIALIZED");
                    return;
                }
                service.startVideo(front)
                        .then((ThenVoid<Boolean>) (result) -> cb.success())
                        .error(error -> cb.error("Error while starting video"));
            }
        });
    }

    private void stopVideo(final CallbackContext cb) {
        mHandler.post(() -> {
            ConferenceService service = VoxeetSDK.conference();
            if (null == service) {
                cb.error("NOT INITIALIZED");
                return;
            }
            service.stopVideo()
                    .then((ThenVoid<Boolean>) (result) -> cb.success())
                    .error(error -> cb.error("Error while stopping video"));
        });
    }

    private void switchCamera(final CallbackContext cb) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                MediaDeviceService service = VoxeetSDK.mediaDevice();
                if (null == service) {
                    cb.error("NOT INITIALIZED");
                    return;
                }
                service.switchCamera()
                        .then((ThenVoid<Boolean>) (result) -> cb.success())
                        .error(error -> cb.error("Error while switching camera"));
            }
        });
    }

    /*
    private void isDefaultFrontFacing(final CallbackContext cb) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                boolean enabled = false;
                if (null != VoxeetSdk.instance()) {
                    enabled = VoxeetSdk.instance().getMediaService().getCameraInformationProvider().isDefaultFrontFacing();
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
                if (null != VoxeetSdk.instance()) {
                    enabled = VoxeetSdk.instance().getMediaService().isFrontCamera();
                }

                cb.sendPluginResult(new PluginResult(PluginResult.Status.OK, enabled));
            }
        });
    }
    */
}
