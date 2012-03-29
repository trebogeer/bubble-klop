package com.trebogeer.klop.bubble;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import static com.trebogeer.klop.bubble.SysUtil.getWakeLock;

/**
 * @author dimav
 *         Date: 3/29/12
 *         Time: 8:33 AM
 */
public class BubblesActivity extends Activity {

    private static String TAG = "bubble-klop";

    private GLSurfaceView mGLView;
    private PowerManager.WakeLock wakeLock;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mGLView = new BubbleRenderer(this);
        mGLView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.FILL_PARENT));
        this.setContentView(mGLView);
        wakeLock = getWakeLock(this, TAG);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLView.onPause();
        wakeLock.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLView.onResume();
        wakeLock.acquire();
    }

}
