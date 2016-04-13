package ru.test.ya.test6;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.io.IOException;

import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraActivity extends AppCompatActivity implements SurfaceHolder.Callback, View.OnClickListener, Camera.PictureCallback, Camera.PreviewCallback {

    private Camera camera;
    private SurfaceHolder surfaceHolder;
    private SurfaceView preview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        preview = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceHolder = preview.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        preview.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        camera = Camera.open();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            camera.setPreviewDisplay(holder);
            camera.setPreviewCallback(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Camera.Size previewSize = camera.getParameters().getPreviewSize();
        float aspect = (float) previewSize.width / previewSize.height;
        int previewSurfaceWidth = preview.getWidth();
        int previewSurfaceHeight = preview.getHeight();

        LayoutParams lp = preview.getLayoutParams();

        // здесь корректируем размер отображаемого preview, чтобы не было искажений

        if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
            // портретный вид
            camera.setDisplayOrientation(90);
            lp.height = previewSurfaceHeight;
            lp.width = (int) (previewSurfaceHeight / aspect);
            ;
        } else {
            // ландшафтный
            camera.setDisplayOrientation(0);
            lp.width = previewSurfaceWidth;
            lp.height = (int) (previewSurfaceWidth / aspect);
        }

        preview.setLayoutParams(lp);
        camera.startPreview();

        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        Bitmap bitmap = Bitmap.createBitmap(previewSurfaceWidth, previewSurfaceHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setAlpha(100);
        canvas.drawRect(0, 0, 30, previewSurfaceHeight, paint);
        canvas.drawRect(previewSurfaceWidth - 30, 0, previewSurfaceWidth, previewSurfaceHeight, paint);
        canvas.drawRect(30, 0, previewSurfaceWidth - 30, 30, paint);
        canvas.drawRect(30, previewSurfaceHeight - 100, previewSurfaceWidth - 30, previewSurfaceHeight, paint);
        paint.setColor(Color.WHITE);
        paint.setAlpha(200);
        canvas.drawCircle(previewSurfaceWidth / 2, previewSurfaceHeight - 50, 40, paint);
        imageView.setImageBitmap(bitmap);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    @Override
    public void onClick(View v) {
        if (v == preview) {
            camera.takePicture(null, null, null, this);
            //camera.autoFocus(this);
        }
    }

    @Override
    public void onPictureTaken(byte[] paramArrayOfByte, Camera paramCamera) {
        FileOutputStream outputStream;
        try {
            Date date = new Date();
            String dt = new SimpleDateFormat("dd_MM_yyyy_HH:mm:ss").format(date);
            outputStream = openFileOutput("Photo_" + dt, Context.MODE_PRIVATE);
            outputStream.write(paramArrayOfByte);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        startService(new Intent(this, ServiceDeletePhoto.class));
        finish();
    }

    @Override
    public void onPreviewFrame(byte[] paramArrayOfByte, Camera paramCamera) {
        // здесь можно обрабатывать изображение, показываемое в preview
    }
}
