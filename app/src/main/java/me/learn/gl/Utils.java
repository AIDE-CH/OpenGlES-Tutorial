package me.learn.gl;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Utils {
    private final static String TAG = Utils.class.getSimpleName();

    public final static int BytesPerFloat = 4;
    public final static int BytesPerShort = 2;
    public final static int FloatsPerPosition = 3;
    public final static int FloatsPerColor = 3;
    public final static int FloatsPerTexture = 2;

    public static Bitmap readBitmapResource(Context ctx, int resourceId){
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        return BitmapFactory.decodeResource(ctx.getResources(), resourceId, options);
    }

    public static Bitmap getBitmapFromAsset(Context context, String filePath) {
        AssetManager assetManager = context.getAssets();

        InputStream istr;
        Bitmap bitmap = null;
        try {
            istr = assetManager.open(filePath);
            bitmap = BitmapFactory.decodeStream(istr);
        } catch (IOException e) {
            // handle exception
        }

        return bitmap;
    }

    public static String readAssetFile(Context ctx, String fileName){
        BufferedReader reader = null;
        try {
            reader = new BufferedReader( new InputStreamReader(ctx.getAssets().open(fileName)) );
            StringBuilder sb = new StringBuilder();
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                sb.append(mLine);
                sb.append("\n");
            }
            return  sb.toString();
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                }
            }
        }
        return null;
    }

}
