package com.trebogeer.klop.bubble;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Environment;
import android.os.PowerManager;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.WindowManager;

/**
 * @author dimav
 *         Date: 3/29/12
 *         Time: 8:41 AM
 */
public class SysUtil {
    private static SoundPool soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);

    public static boolean hasAccelerometer(final SensorManager manager) {
        return manager.getSensorList(Sensor.TYPE_ACCELEROMETER).size() > 0;
    }

    public static boolean hasAccelerometer(final Context context) {
        SensorManager manager = getSensorManager(context);
        return hasAccelerometer(manager);
    }

    public static int pointerIndex(final MotionEvent event) {
        return (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
    }

    public static int getAction(MotionEvent event) {
        return event.getAction() & MotionEvent.ACTION_MASK;
    }

    public static boolean hasExternalStorageReadWrite() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static boolean hasExternalStorageReadOnly() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    public static int[] addSounds(final String[] sounds) {
        int[] soundIds = new int[sounds.length];
        for (int i = 0; i < sounds.length; i++) {
            soundIds[i] = soundPool.load(sounds[i], 1);
        }
        return soundIds;
    }

    public static int addSound(final String sound) {
        return soundPool.load(sound, 1);
    }

    public static int addSound(final String sound, final SoundPool.OnLoadCompleteListener listener) {
        soundPool.setOnLoadCompleteListener(listener);
        return addSound(sound);
    }

    public static int addAndPlay(final Context context, final int resId) {
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                playSoundMid(sampleId);
            }
        });
        return soundPool.load(context, resId, 1);
    }

    public static void playSoundLoud(final int soundId) {
        soundPool.play(soundId, 1.0f, 1.0f, 0, 0, 1);
    }

    public static void playSoundMid(final int soundId) {
        soundPool.play(soundId, 0.5f, 0.5f, 0, 0, 1);
    }

    public static void playSoundQuite(final int soundId) {
        soundPool.play(soundId, 0.1f, 0.1f, 0, 0, 1);
    }

    public static void unloadSound(final int soundId) {
        soundPool.unload(soundId);
    }

    public static PowerManager.WakeLock getWakeLock(final Context context, final String lockName) {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        return powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, lockName);
    }

    public static SensorManager getSensorManager(final Context context) {
        return (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    public static Vibrator getVibrator(final Context context) {
        return ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE));
    }

    public static WindowManager getWindowManager(final Context context) {
        return ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE));
    }
}
