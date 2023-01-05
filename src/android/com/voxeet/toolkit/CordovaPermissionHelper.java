package com.voxeet.toolkit;

import android.Manifest;
import android.os.Build;

import com.voxeet.promise.Promise;
import com.voxeet.uxkit.common.permissions.PermissionController;
import com.voxeet.uxkit.common.permissions.PermissionResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CordovaPermissionHelper {

    private final static String POST_NOTIFICATIONS = "android.permission.POST_NOTIFICATIONS";
    private final static List<String> PERMISSIONS = Arrays.asList(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA);

    public static Promise<Boolean> requestDefaultPermission() {
        return new Promise<>(solver -> {
            List<String> permissions = new ArrayList<>(PERMISSIONS);

            // Cordova itself is not yet bumping to tools 33 but we can use the version directly and the permission
            if (Build.VERSION.SDK_INT >= 33) {
                permissions.add(POST_NOTIFICATIONS);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                permissions.add(Manifest.permission.BLUETOOTH_CONNECT);
            }

            PermissionController.requestPermissions(permissions).then(permissionResults -> {
                for (PermissionResult res : permissionResults) {
                    if (res.isFor(Manifest.permission.RECORD_AUDIO) && !res.isGranted) {
                        solver.resolve(false);
                        return;
                    }
                }
                solver.resolve(true);
            }).error(solver::reject);
        });
    }
}
