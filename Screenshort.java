

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.ps.uc.Area.AreaActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Screenshort {
    /*todo in Manifest*/
     <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />

    android:requestLegacyExternalStorage="true"
    android:hardwareAccelerated="true"
    android:usesCleartextTraffic="true"
    tools:targetApi="m"


    <provider
    android:name="androidx.core.content.FileProvider"
    android:authorities="com.ps.uc.fileprovider"
    android:exported="false"
    android:grantUriPermissions="true">
            <meta-data
    android:name="android.support.FILE_PROVIDER_PATHS"
    android:resource="@xml/file_paths" />
        </provider>


    /*todo in file_paths*/
    <resources>
    <paths xmlns:android="http://schemas.android.com/apk/res/android">
        <cache-path name="shared_images" path="images/"/>
    </paths>
</resources>

    /*todo in activity*/

    public static int REQUEST_PERMISSIONS = 1;
    boolean boolean_permission;

    private void fn_permission() {
        if ((ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {

            if ((ActivityCompat.shouldShowRequestPermissionRationale(AreaActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE))) {
            } else {
                ActivityCompat.requestPermissions(AreaActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);

            }

            if ((ActivityCompat.shouldShowRequestPermissionRationale(AreaActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE))) {
            } else {
                ActivityCompat.requestPermissions(AreaActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);

            }
        } else {
            boolean_permission = true;


        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                boolean_permission = true;


            } else {
                Toast.makeText(getApplicationContext(), "Please allow the permission", Toast.LENGTH_LONG).show();

            }
        }
    }

    onclick.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
//                takeScreenshot();
            if (boolean_permission) {
                Bitmap bitmap1 = loadBitmapFromView(binding.linear, binding.linear.getWidth(), binding.linear.getHeight());
//                    saveBitmap(bitmap1);
//                    saveImage(bitmap1);
                shareImageUri(saveImage(bitmap1));
            }
        }
    });

    public static Bitmap loadBitmapFromView(View v, int width, int height) {
        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(b);
        v.draw(c);

        return b;
    }

    private Uri saveImage(Bitmap image) {
        Log.d(TAG, "saveImage: " + image);
        //TODO - Should be processed in another thread
        File imagesFolder = new File(getCacheDir(), "images");
        Uri uri = null;
        try {
            imagesFolder.mkdirs();
            File file = new File(imagesFolder, "shared_image.png");

            FileOutputStream stream = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.PNG, 90, stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(this, "com.ps.uc.fileprovider", file);
            Log.d(TAG, "saveImage: " + uri);
        } catch (IOException e) {
            Log.d(TAG, "IOException while trying to write file for sharing: " + e.getMessage());
        }
        return uri;
    }

    private void shareImageUri(Uri uri) {
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        Log.d(TAG, "shareImageUri: " + uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("image/png");
        String shareMessage = "Area Conversion\nDownload app: \n" + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n"+"Thanks\nUnit Convertor Team";
        intent.putExtra(Intent.EXTRA_TEXT, shareMessage);
        startActivity(intent);
    }

}
