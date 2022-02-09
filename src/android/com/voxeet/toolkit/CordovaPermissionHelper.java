package com.voxeet.toolkit;

import android.Manifest;

import com.voxeet.promise.Promise;
import com.voxeet.uxkit.common.permissions.PermissionController;
import com.voxeet.uxkit.common.permissions.PermissionResult;

import java.util.Arrays;
import java.util.List;

public class CordovaPermissionHelper {

    private final static List<String> PERMISSIONS = Arrays.asList(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA);

    public static Promise<Boolean> requestDefaultPermission() {
        return new Promise<>(solver -> {

            PermissionController.requestPermissions(PERMISSIONS).then(permissionResults -> {
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
