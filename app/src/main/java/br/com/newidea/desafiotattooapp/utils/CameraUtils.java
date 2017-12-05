package br.com.newidea.desafiotattooapp.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.MediaStore;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Data;

/**
 * Created by fabio on 04/10/17.
 */

@Data
@Builder
public class CameraUtils {

    private Activity activityOwner;

    public Intent getPickImageChooserIntent() {

        // Determine Uri of camera image to save.
        final Uri outputFileUri = getCaptureImageOutputUri();

        final List allIntents = new ArrayList();
        PackageManager packageManager = activityOwner.getPackageManager();

        // collect all camera intents
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final List listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (Object o : listCam) {
            ResolveInfo res = (ResolveInfo) o;
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }

        // collect all gallery intents
        final Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        final List listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (Object o : listGallery) {

            ResolveInfo res = (ResolveInfo) o;
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }

        // the main intent is the last in the list (fucking android) so pickup the useless one
        Intent mainIntent = (Intent) allIntents.get(allIntents.size() - 1);
        for (Object o : allIntents) {
            Intent intent = (Intent) o;
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

        // Create a chooser from the main intent
        final Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");

        // Add all other intents
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }

    public Uri getPickImageResultUri(Intent data) {
        boolean isCamera = true;
        if (data != null) {
            final String action = data.getAction();
            isCamera = action != null && action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
        }

        return isCamera ? getCaptureImageOutputUri() : data.getData();
    }

    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = activityOwner.getExternalCacheDir();
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "profile.png"));
        }
        return outputFileUri;
    }
}
