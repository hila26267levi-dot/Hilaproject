package com.hila.myapplication.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.ImageButton;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class ImageHelper {

    public static final int PICK_IMAGE_REQUEST = 1001;
    public static final int CAPTURE_IMAGE_REQUEST = 1002;

    // פתח גלריה
    public static void openGallery(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // פתח מצלמה
    public static void openCamera(Activity activity) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        activity.startActivityForResult(intent, CAPTURE_IMAGE_REQUEST);
    }

    // המר Bitmap ל-Base64
    public static String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
    }

    // המר Base64 ל-Bitmap
    public static Bitmap base64ToBitmap(String base64) {
        byte[] bytes = Base64.decode(base64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    // טפל בתוצאה מגלריה/מצלמה והחזר Base64
    public static String handleActivityResult(Activity activity, int requestCode,
                                              int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK || data == null) return null;

        try {
            if (requestCode == PICK_IMAGE_REQUEST) {
                Uri imageUri = data.getData();
                InputStream inputStream = activity.getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmapToBase64(bitmap);

            } else if (requestCode == CAPTURE_IMAGE_REQUEST) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                return bitmapToBase64(bitmap);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
