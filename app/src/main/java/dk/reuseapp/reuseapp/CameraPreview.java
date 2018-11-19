package dk.reuseapp.reuseapp;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

/**
 * This is the camera preview View. It implements all the methods necessary for
 * drawing the camera preview. It also controls the camera object. It's based on the official
 * "Building a camera app" guide and uses the android.hardware.Camera to work with API 19.
 * /TODO: Go over the complex methods and make sure there are no redundant calls, also fix orientation.
 *
 * @author Torkil Helgeland
 */
public class CameraPreview extends ViewGroup implements SurfaceHolder.Callback {
    private SurfaceHolder holder;
    private SurfaceView surfaceView;
    private Camera camera;
    private List<Camera.Size> supportedPreviewSizes;
    private Camera.Size previewSize;
    private Context context;

    public CameraPreview(Context context) {
        super(context);
        this.context = context;

        surfaceView = new SurfaceView(context);
        addView(surfaceView);

        holder = surfaceView.getHolder();
        holder.addCallback(this);
    }

    public void startCamera(Camera camera) {
        if (this.camera == camera) {
            return;
        }
        stopPreview();

        this.camera = camera;

        if (camera != null) {
            supportedPreviewSizes = camera.getParameters().getSupportedPreviewSizes();
            requestLayout();

            try {
                camera.setPreviewDisplay(holder);
            }
            catch (IOException ioe) {
                Toast.makeText(this.getContext(), "yikes", Toast.LENGTH_LONG).show();
                Log.d("ReuseApp", "Something went wrong setting display when starting camera");
            }
            camera.startPreview();
        }
    }

    public void takePicture(Camera.PictureCallback pictureCallback) {
        camera.takePicture(null, null, pictureCallback);
    }

    public void stopPreview() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    @Override
    public void onMeasure(int w, int h) {
        int width = MeasureSpec.getSize(w);
        int heigth = MeasureSpec.getSize(h);

        setMeasuredDimension(width, heigth);

        if (supportedPreviewSizes != null) {
            previewSize = supportedPreviewSizes.get(0);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            if (camera != null) {
                camera.setPreviewDisplay(holder);
            }
        }
        catch (IOException ioe) {
            Log.d("ReuseApp", "Something went wrong setting display on holder");
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        camera.stopPreview();
        Camera.Parameters parameters = camera.getParameters();
        parameters.setPreviewSize(previewSize.width, previewSize.height);
        try {
            camera.setPreviewDisplay(holder);
        }
        catch (IOException ioe) {
            Log.d("ReuseApp", "Something went wrong setting display holder");
        }
        requestLayout();
        camera.setParameters(parameters);

        setCameraDisplayOrientation(camera);

        camera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (camera != null) {
            camera.stopPreview();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed && getChildCount()>0) {
            View child = getChildAt(0);
            int width = r-l;
            int heigth = b-t;
            int previewWidth = width;
            int previewHeigth = heigth;
            if (previewSize != null) {
                previewWidth = previewSize.width;
                previewHeigth = previewSize.height;
            }

            if (width * previewHeigth < heigth * previewWidth) {
                int scaledWidth = previewWidth * heigth /previewHeigth;
                child.layout((width-scaledWidth)/2,0,(width + scaledWidth)/2,heigth);
            }
            else {
                int scaledHeigth = previewHeigth * width / previewWidth;
                child.layout(0, (heigth-scaledHeigth)/2,width,(heigth+scaledHeigth)/2);
            }
        }
    }

    //TODO: This method doesn't work properly.
    public void setCameraDisplayOrientation(android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo camInfo =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(0, camInfo);


        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int rotation = display.getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int orientation;
        if (camInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            orientation = (camInfo.orientation + degrees) % 360;
            orientation = (360 - orientation) % 360;
        } else {
            orientation = (camInfo.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(orientation);
    }
}