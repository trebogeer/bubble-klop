package com.trebogeer.klop.bubble;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import com.threed.jpct.Camera;
import com.threed.jpct.Config;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Light;
import com.threed.jpct.Logger;
import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.RGBColor;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureManager;
import com.threed.jpct.World;
import com.threed.jpct.util.BitmapHelper;
import com.threed.jpct.util.MemoryHelper;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;
import java.lang.reflect.Field;
import java.util.Random;

import static com.trebogeer.klop.bubble.SysUtil.getWakeLock;

/**
 * @author dimav
 *         Date: 3/29/12
 *         Time: 8:33 AM
 */
public class BubblesActivity extends Activity {

    static BubblesActivity master = null;

    private static final byte BUBBLES = 4;

    private static String TAG = "bubble-klop";

    private GLSurfaceView mGLView;
    private PowerManager.WakeLock wakeLock = null;
    private FrameBuffer fb = null;
    private World world = null;
    private Light sun = null;
    private Object3D background = null;
    private Object3D[] bubbles = null;
    private int fps = 0;
    private RGBColor back = new RGBColor(50, 50, 100);

    float translate[] = new float[]{-2.11f, -1.11f, -3.01f};
    private short up = 1;
    private int cnt;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        Logger.log("onCreate");
        if (master != null) {
            copy(master);
        }
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mGLView = new GLSurfaceView(getApplication());
        mGLView.setEGLConfigChooser(new GLSurfaceView.EGLConfigChooser() {
            public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
                // Ensure that we get a 16bit framebuffer. Otherwise, we'll fall
                // back to Pixelflinger on some device (read: Samsung I7500)
                int[] attributes = new int[]{EGL10.EGL_DEPTH_SIZE, 16, EGL10.EGL_NONE};
                EGLConfig[] configs = new EGLConfig[1];
                int[] result = new int[1];
                egl.eglChooseConfig(display, attributes, configs, 1, result);
                return configs[0];
            }
        });
        mGLView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.FILL_PARENT));
        mGLView.setRenderer(new BRenderer());

        this.setContentView(mGLView);
        wakeLock = getWakeLock(this, TAG);
    }

    private void copy(Object src) {
        try {
            Logger.log("Copying data from master Activity!");
            Field[] fs = src.getClass().getDeclaredFields();
            for (Field f : fs) {
                f.setAccessible(true);
                f.set(this, f.get(src));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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

    class BRenderer implements GLSurfaceView.Renderer {

        private long time = System.currentTimeMillis();

        public BRenderer() {
            Config.maxPolysVisible = 500;
            Config.farPlane = 1500;
            Config.glTransparencyMul = 0.1f;
            Config.glTransparencyOffset = 0.1f;
            Config.useVBO = true;

            Texture.defaultToMipmapping(true);
            Texture.defaultTo4bpp(true);
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            if (fb != null) {
                fb.dispose();
            }
            fb = new FrameBuffer(gl, width, height);

            if (master == null) {

                world = new World();
                world.setAmbientLight(20, 20, 20);


                sun = new Light(world);
                sun.setIntensity(250, 250, 250);

                Texture texture = new Texture(BitmapHelper.convert(getResources().getDrawable(R.drawable.bublebgnd)));
                TextureManager.getInstance().addTexture("bublebgnd", texture);

                // Create a texture out of the icon...:-)
                texture = new Texture(BitmapHelper.rescale(BitmapHelper.convert(getResources().getDrawable(R.drawable.icon)), 64, 64));
                TextureManager.getInstance().addTexture("tex", texture);

                bubbles = new Object3D[BUBBLES];
                for (byte i = 0; i < BUBBLES; i++) {
                    Object3D bubble = Primitives.getSphere(8);
                    bubble.calcTextureWrapSpherical();
                    //bubble.setCulling(true);
                    bubble.translate(-30 +i*20, 30.0f, 0.0f);
                    bubble.setTransparency(70);
                    //  bubble.rotateX(-(float) Math.PI / Math.abs(r.nextInt()/3));
                    bubble.setTexture("tex");
                    bubble.strip();
                    bubble.build();

                    bubbles[i] = bubble;
                }


                background = Primitives.getPlane(1, 110f);
                //background.$
                // background.calcTextureWrapSpherical();
                background.setTexture("bublebgnd");
                background.strip();
                background.build();

                world.addObject(background);

                //world.
                world.addObjects(bubbles);

                Camera cam = world.getCamera();
                cam.moveCamera(Camera.CAMERA_MOVEOUT, 70);
                cam.lookAt(background.getTransformedCenter());
                //  cam.lookAt(bubbles.getTransformedCenter());

                SimpleVector sv = new SimpleVector();
                sv.set(background.getTransformedCenter());
                sv.y -= 100;
                sv.z -= 100;
                sun.setPosition(sv);
                MemoryHelper.compact();

                if (master == null) {
                    Logger.log("Saving master Activity!");
                    master = BubblesActivity.this;
                }
            }
        }

        @Override
        public void onDrawFrame(GL10 gl) {
//            if (touchTurn != 0) {
//                cube.rotateY(touchTurn);
//                touchTurn = 0;
//            }
//
//            if (touchTurnUp != 0) {
//                cube.rotateX(touchTurnUp);
//                touchTurnUp = 0;
//            }
            //  up++;
            if (up == 1 && cnt > 330) {
                up = -1;

            } else if (up == -1 && cnt < -130) {
                up = 1;
            }
            cnt += up;

            for (byte i = 0; i < BUBBLES; i++) {

                float x = i == 0 ? 0.08f * up : 0.0f;
                float y = i == 1 ? 0.15f * up : 0.0f;

                bubbles[i].translate(i == 3 ? -(0.1f * up) : x, /*translate[0] +*/ i == 2 ? -(0.15f * up) : y, 0.0f);
                //bubbles[i].scale(0.01f);
              //  bubbles[i].animate(3 * x);
            }

            fb.clear(/*back*/);
            world.renderScene(fb);
            world.draw(fb);
            fb.display();

            if (System.currentTimeMillis() - time >= 1000) {
                Logger.log(fps + "fps");
                fps = 0;
                time = System.currentTimeMillis();
            }
            fps++;
        }
    }

//    private void blitNumber(int number, int x, int y) {
//        if (font != null) {
//            String sNum = Integer.toString(number);
//
//            for (int i = 0; i < sNum.length(); i++) {
//                char cNum = sNum.charAt(i);
//                int iNum = cNum - 48;
//                fb.blit(font, iNum * 5, 0, x, y, 5, 9, FrameBuffer.TRANSPARENT_BLITTING);
//                x += 5;
//            }
//        }
//    }

}
