package views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;
import android.view.TextureView;

import androidx.annotation.NonNull;

//TextureView that is used to render the camera preview
public class CameraPreviewPicker extends TextureView implements TextureView.SurfaceTextureListener, Camera.PreviewCallback {
    //Logging tag
    private static final String TAG = CameraPreviewPicker.class.getCanonicalName();

    //Size of Pointer in Pixels
    private static final int POINTER = 5;

    //Camera used for getting the preview frame
    protected Camera mCamera;

    //The Camera Size of the preview
    protected Camera.Size mPrevSize;

    //Array of 3 ints that represent the colours being selected
    protected int[] mColoursSelected;

    //An OnColourSelectedListener that'll be called when a colour is selected
    protected OnColourSelectedListener mOnColourSelectedListener;

    public CameraPreviewPicker(Context context, Camera camera) {
        super(context);
        mCamera = camera;
        mCamera.getParameters().getPreviewFormat();

        //installing a SurfaceHolder.Callback to get notified when the underlying surface is created/destroyed
        this.setSurfaceTextureListener(this);

        mPrevSize = mCamera.getParameters().getPreviewSize();
        mColoursSelected = new int[3];
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        //Surface created, tell the camera where to draw preview
        try {
            mCamera.setPreviewTexture(surface);
            mCamera.setPreviewCallback(this);
            mCamera.startPreview();
        } catch (Exception e) {
            Log.d(TAG, "Error starting camera: " + e.getMessage());
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {

    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (mOnColourSelectedListener != null) {
            final int x = mPrevSize.width / 2;
            final int y = mPrevSize.height / 2;

            //Resetting selected colour
            mColoursSelected[0] = 0;
            mColoursSelected[1] = 0;
            mColoursSelected[2] = 0;

            //Computing avg selected colour
            for (int i = 0; i < POINTER; i++) {
                for (int j = 0; j < POINTER; j++) {
                    yuv420Colour(data, mColoursSelected, (i * POINTER + j + 1), (x - POINTER) + i, (y - POINTER) + j, mPrevSize.width, mPrevSize.height);
                }
            }
            mOnColourSelectedListener.onSelectedColour(Color.rgb(mColoursSelected[0], mColoursSelected[1], mColoursSelected[2]));
        }
    }

    //converting the YUV values to RGB, this is gonna be a head wreck, UPDATE: it was a head wreck....
    private void yuv420Colour(byte[] data, int[] avgColour, int i, int x, int y, int width, int height) {
        //This is referenced mostly from https://stackoverflow.com/questions/9325861/converting-yuv-rgbimage-processing-yuv-during-onpreviewframe-in-android/10125048#10125048
        final int s = width * height;

        //get the Y value, which is stored in the first block of data
        //the logical "AND 0xFF" is needed to deal with the signed issue
        final int Y = data[y * width + x] & 0xFF;

        //get the U and V values, stored after Y values, one per 2x2 block
        //of pixels, interleaved. Prepare them as floats with correct range
        //ready for calculation later
        final int x2 = x / 2;
        final int y2 = y / 2;

        //make this V for NV12/420SP
        //nv12 (another variant of YUV420) is a biplanar format with a full sized Y plane followed by a single chroma plane with weaved U and V values
        final float U = (float) (data[s + 2 * x2 + 1 + y2 * width] & 0xFF) - 128.0f;
        //make this U for NV12/420SP
        final float V = (float) (data[s + 2 * x2 + y2 * width] & 0xFF) - 128.0f;

        //YUV to RGB conversion
        //correct Y to allow for the fact that it is [16..235] and not [0..255]
        float yF = 1.164f * ((float) Y) - 16.0f;
        //doing the YUV to RGB conversion
        //these values appear to work, but there are others out there
        int red = (int) (yF + 1.596f * V);
        int green = (int) (yF - 0.813f * V - 0.391f * U);
        int blue = (int) (yF + 2.018f * U);

        //Clipping RGB values to 0-255
        red = red < 0 ? 0 : red > 255 ? 255 : red;
        green = green < 0 ? 0 : green > 255 ? 255 : green;
        blue = blue < 0 ? 0 : blue > 255 ? 255 : blue;

//        if (red < 0) {
//            red = 0;
//        } else {
//            if (red > 255) red = 255;
//            else red = red;
//        }

        avgColour[0] += (red - avgColour[0]) / i;
        avgColour[1] += (green - avgColour[1]) / i;
        avgColour[2] += (blue - avgColour[2]) / i;
    }

    //Setting an OnColourSelectedListener that's called when a colour is selected
    public void setOnColourSelectedListener(OnColourSelectedListener onColourSelectedListener) {
        mOnColourSelectedListener = onColourSelectedListener;
    }

    //interface for callback
    public interface OnColourSelectedListener {
        //called when new colour selected
        void onSelectedColour(int newColour);
    }
}
