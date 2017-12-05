package br.com.newidea.desafiotattooapp.utils;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;

import java.util.ArrayList;

import lombok.Data;

import static android.Manifest.permission.CAMERA;
import static br.com.newidea.desafiotattooapp.utils.AppConstants.ALL_PERMISSIONS_RESULT;

@Data
public class AppCameraPermissionsUtil {

    private Activity activityOwner;
    private ArrayList permissions;
    private ArrayList permissionsToRequest;
    private ArrayList permissionsRejected;

    // Storage Permissions
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public AppCameraPermissionsUtil(@NonNull final Activity activityOwner) {
        setActivityOwner(activityOwner);

        setPermissions(new ArrayList());
        permissions.add(CAMERA);
        setPermissionsToRequest(findUnAskedPermissions(permissions));
        setPermissionsRejected(new ArrayList());
    }

    public void setUpAppPermissions() {

        //get the permissions we have asked for before but are not granted..
        //we will store this in a global list to access later.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0)
                activityOwner.requestPermissions((String[]) permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (Object o : permissionsToRequest) {
                    String perms = (String) o;
                    if (hasPermission(perms)) {
                    } else {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (activityOwner.shouldShowRequestPermissionRationale((String) permissionsRejected.get(0))) {
                            MessageUtils.showMessageOKCancel(activityOwner, "Existem permissões necessárias para este app. Por favor, aceite !",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                activityOwner.requestPermissions((String[]) permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }
                }

                break;
        }

    }

    private ArrayList findUnAskedPermissions(ArrayList wanted) {
        ArrayList result = new ArrayList();

        for (Object o : wanted) {
            String perm = (String) o;
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }
        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (activityOwner.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }
}
