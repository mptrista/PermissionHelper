package com.bout.androidapp.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;

import android.support.v4.app.Fragment;

/**
 * @author ltoshkin
 */

public class PermissionHelper {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({CAMERA, STORAGE})
    public @interface RequestType {
    }

    public static final int CAMERA = 1001;
    public static final int STORAGE = 1002;

    private WeakReference<Callback> callback;

    public interface Callback {
        void onPermissionGranted();

        void onRationaleNeeded();
    }

    private void requestPermission(Fragment fragment, String permission,
                                   @RequestType int requestCode) {
        fragment.requestPermissions(
                new String[]{permission},
                requestCode);
    }

    public static Intent getAppSettingsIntent(String packageName) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", packageName, null);
        intent.setData(uri);
        return intent;
    }

    public void onRequestPermissionResult(Fragment fra,
                                          @RequestType int requestCode,
                                          @NonNull String permission,
                                          @NonNull int[] grantResults) {
        if (!isPermissionGranted(requestCode, permission, grantResults)) {
            checkIfDontShowIsSet(fra, requestCode);
        } else {
            if (this.callback.get() != null) {
                this.callback.get().onPermissionGranted();
            }
        }
    }

    private boolean hasPermissions(Fragment fragment, @RequestType int requestCode) {
        String permission = getPermissionForCode(requestCode);
        if (hasPermission(fragment.getContext(), permission)) {
            return true;
        } else {
            requestPermission(fragment, permission, requestCode);
            return false;
        }
    }

    private boolean hasPermission(Context context, String permission) {
        int permissionCheck = ContextCompat.checkSelfPermission(context, permission);
        return permissionCheck == PackageManager.PERMISSION_GRANTED;
    }

    private boolean isPermissionGranted(int requestCode, String permission, int[] grantResults) {
        switch (requestCode) {
            case CAMERA:
                return TextUtils.equals(permission, Manifest.permission.CAMERA)
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED;
            case STORAGE:
                return TextUtils.equals(permission, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED;
            default:
                return false;
        }
    }

    public void executeWithPermCheck(final Fragment fragment, @RequestType int requestCode, Callback callback) {
        this.callback = new WeakReference<>(callback);
        if (hasPermissions(fragment, requestCode)) {
            if (this.callback.get() != null) {
                this.callback.get().onPermissionGranted();
            }
        }
    }

    private void checkIfDontShowIsSet(final Fragment fra, @RequestType int requestCode) {
        if (!fra.shouldShowRequestPermissionRationale(getPermissionForCode(requestCode))) {
            if (this.callback.get() != null) {
                this.callback.get().onRationaleNeeded();
            }
        }
    }

    @NonNull
    private String getPermissionForCode(@RequestType int requestCode) {
        String permission;
        switch (requestCode) {
            case STORAGE:
                permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
                break;
            case CAMERA:
                permission = Manifest.permission.CAMERA;
                break;
            default:
                permission = "";
                break;
        }
        return permission;
    }

}
