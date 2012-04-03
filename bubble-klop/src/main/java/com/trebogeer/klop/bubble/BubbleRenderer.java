package com.trebogeer.klop.bubble;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.Display;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.nio.FloatBuffer;

/**
 * @author dimav
 *         Date: 3/29/12
 *         Time: 8:48 AM
 */
public class BubbleRenderer extends GLSurfaceView implements GLSurfaceView.Renderer {

    private Square background;
    private Context context;
    private Sphere sphere;
    private float xRot = 2;
    private float yRot = 2;

    /* The buffers for our light values */
    private FloatBuffer lightAmbientBuffer;
    private FloatBuffer lightDiffuseBuffer;
    private FloatBuffer lightPositionBuffer;

    public BubbleRenderer(Context context) {
        super(context);
        setRenderer(this);
        this.context = context;
        float[] lightAmbient = {0.5f, 0.5f, 0.5f, 1.0f};
        float[] lightDiffuse = {1.0f, 1.0f, 1.0f, 1.0f};
        float[] lightPosition = {0.0f, 0.0f, 2.0f, 1.0f};

        lightAmbientBuffer = GLEUtils.allocateFloatBuffer(lightAmbient, lightAmbient.length * 4);

        lightDiffuseBuffer = GLEUtils.allocateFloatBuffer(lightDiffuse, lightDiffuse.length * 4);

        lightPositionBuffer = GLEUtils.allocateFloatBuffer(lightPosition, lightPosition.length * 4);

        final Display display = SysUtil.getWindowManager(context).getDefaultDisplay();
        int dw = display.getWidth();
        int dh = display.getHeight();
      //  this.background = new Square(dw, dh);
        this.sphere = new Sphere(1, 25);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.i("Bubbles#onSurfaceCreated", "Enterd method");

        //And there'll be light!
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, lightAmbientBuffer);        //Setup The Ambient Light
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, lightDiffuseBuffer);        //Setup The Diffuse Light
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, lightPositionBuffer);    //Position The Light
        gl.glEnable(GL10.GL_LIGHT0);                                            //Enable Light 0
        gl.glEnable(GL10.GL_LIGHT1);
//
//        //Blending
//        gl.glColor4f(1.0f, 1.0f, 1.0f, 0.5f);                //Full Brightness. 50% Alpha ( NEW )
//        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE);        //Set The Blending Function For Translucency ( NEW )

        //Settings
        gl.glDisable(GL10.GL_DITHER);                //Disable dithering
        gl.glEnable(GL10.GL_TEXTURE_2D);            //Enable Texture Mapping
        gl.glShadeModel(GL10.GL_SMOOTH);             //Enable Smooth Shading
        //gl.glClearColor(166 / GL10.GL_RGBA, 166 / GL10.GL_RGBA, 166 / GL10.GL_RGBA, 0.5f);     //Black Background  - 0.0f,0.0f,0.0f
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        gl.glClearDepthf(1.0f);                     //Depth Buffer Setup
        gl.glEnable(GL10.GL_DEPTH_TEST);             //Enables Depth Testing
        gl.glDepthFunc(GL10.GL_LEQUAL);             //The Type Of Depth Testing To Do

        //Really Nice Perspective Calculations
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);

      //  background.loadGLTexture(gl, context);
        Log.i("Bubbles#onSurfaceCreated", "Exit method");
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLEUtils.onSurfaceChanged(gl, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // Clear color and depth buffers
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        // ----- Render the Cube -----
        gl.glLoadIdentity();                  // Reset the model-view matrix

        //    Check if the light flag has been set to enable/disable lighting
        gl.glEnable(GL10.GL_LIGHTING);

        // Translate into the screen
       // background.draw(gl, 0);

        gl.glTranslatef(0.0f, 0.0f, -5.0f);

        gl.glRotatef(xRot, 0.0f, 1.0f, 0.0f);
        gl.glRotatef(yRot, 1.0f, 0.0f, 0.0f);
        sphere.draw(gl);
    }
}
