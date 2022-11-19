package tees.ac.uk.mgd.w9090951.example.androidgame;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameView extends SurfaceView implements Runnable {

    private volatile boolean playing = true;
    private Thread gameThread;
    private long timeThisFrame;
    private float fps;
    private SurfaceHolder surfaceHolder;
    private Bitmap playerBitmap;
    private Bitmap fireBitmap;
    private int frameW = 50;
    private int frameH = 50;
    private int frameCount = 1;
    private int spawnTimerMax = 5;
    private int spawnTimerMin = 1;
    private float spawnTimer;
    private int width;
    private int height;
    private float steerVelocity;
    private List<Entities> entityList = new ArrayList<Entities>();
    private float dashLength = 500;
    private Canvas canvas;
    OnSwipeTouchListener onSwipeTouchListener;
    TileSensor tileSensor;

    private float score;
    private Paint scorePaint;

    public GameView(Context context) {
        super(context);
        onSwipeTouchListener = new OnSwipeTouchListener(context);
        spawnTimer = new Random().nextInt((spawnTimerMax - spawnTimerMin) + 1) + spawnTimerMin;
        tileSensor = new TileSensor(context);
        Log.d("Spawner : ", String.valueOf(spawnTimer));
        width = context.getResources().getDisplayMetrics().widthPixels;
        height = context.getResources().getDisplayMetrics().heightPixels;
        surfaceHolder = getHolder();
        playerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.player);
        playerBitmap = Bitmap.createScaledBitmap(playerBitmap,frameW * frameCount,frameH,false);
        fireBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.fire);
        fireBitmap = Bitmap.createScaledBitmap(fireBitmap,frameW * frameCount,frameH,false);

        scorePaint = new Paint();
        scorePaint.setColor(Color.BLACK);
        scorePaint.setTextSize(50);

        score = 0;

        Player player = new Player(playerBitmap,frameH,frameW,width / 2,height / 2,surfaceHolder);
        entityList.add(player);
    }

    @Override
    public void run()
    {
        while (playing)
        {
            long startFrameTime = System.currentTimeMillis();
            draw();
            update();
            timeThisFrame = System.currentTimeMillis() - startFrameTime;
            if (timeThisFrame >= 1)
            {
                fps = 1000 / timeThisFrame;
            }
        }
    }

    private void update()
    {
        spawnTimer -= 1 / fps;

        if (spawnTimer <= 0)
        {

            entityList.add(new Fire(fireBitmap,frameH,frameW,width,0,surfaceHolder));
            spawnTimer = new Random().nextInt((spawnTimerMax - spawnTimerMin) + 1) + spawnTimerMin;
        }

        for (int i = 1; i < entityList.size(); i++)
        {
            entityList.get(i).Move(fps);
            if (entityList.get(0).isCollision(entityList.get(i).xPos,entityList.get(i).yPos))
            {
                entityList.remove(i);
            }
        }

        entityList.get(0).Move(steerVelocity);

    }

    private void draw()
    {
        if (surfaceHolder.getSurface().isValid())
        {
            canvas = surfaceHolder.lockCanvas();
            canvas.drawColor(Color.WHITE);
            for (int i = 0; i < entityList.size(); i++)
            {
                entityList.get(i).draw(canvas);
            }
            score = score + 0.1f;
            canvas.drawText("Score : " + (int)score,width / 2,50,scorePaint);
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        return onSwipeTouchListener.onTouch(this, event);
    }


    public void resume()
    {
        gameThread = new Thread(this);
        gameThread.start();
        playing = true;
    }

    public void pause()
    {
        playing = false;
        try
        {
         gameThread.join();
        }
        catch (InterruptedException e)
        {
            Log.e("GameView", "Interrupted");
        }
    }

    public class TileSensor extends Activity implements SensorEventListener
    {
        SensorManager sensorManager;
        private float[] accel;
        private float[] mag;
        private float[] rotationMatrix = new float[16];
        private float[] orientation = new float[4];

        TileSensor(Context ctx)
        {
            sensorManager = (SensorManager) ctx.getSystemService(Context.SENSOR_SERVICE);
            registerListeners();
        }

        private float[] lowPass(float[] input, float[] output)
        {
            return output;
        }

        @Override
        public void onSensorChanged(SensorEvent sensorEvent)
        {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            {
                GetMag(sensorEvent);
            }
            else if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            {
                GetAccel(sensorEvent);
            }
            // multiply the rotation by 1 radian
            // 57.2957795 degrees = 1 radian

            float Y = orientation[1] *  57.2957795f;

            steerVelocity = Y;
        }

        private void GetMag(SensorEvent event)
        {
            if (mag == null) {
                mag = new float[3];
            }

            System.arraycopy(event.values, 0, mag, 0, 3);

            if (accel != null)
            {
                ComputeOrientation();
            }
        }

        private void GetAccel(SensorEvent event)
        {
            if (accel == null) {
                accel = new float[3];
            }

            System.arraycopy(event.values, 0, accel, 0, 3);
        }

        private void ComputeOrientation()
        {
            if (SensorManager.getRotationMatrix(rotationMatrix, null, mag, accel))
            {
                SensorManager.getOrientation(rotationMatrix, orientation);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i)
        {

        }

        private void unregisterListeners()
        {
            sensorManager.unregisterListener(this);
        }

        private void registerListeners()
        {
            sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_GAME);
            sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
        }

        @Override
        public void onDestroy()
        {
            unregisterListeners();
            super.onDestroy();
        }

        @Override
        public void onPause() {
            unregisterListeners();
            super.onPause();
        }

        @Override
        public void onResume() {
            registerListeners();
            super.onResume();
        }

    }

    public class OnSwipeTouchListener implements OnTouchListener
    {
        private final GestureDetector gestureDetector;

        public OnSwipeTouchListener(Context ctx)
        {
            gestureDetector = new GestureDetector(ctx,new GestureListener());
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            Log.d("Gesture", "onTouch");
            return gestureDetector.onTouchEvent(motionEvent);
        }
    }

    private final class GestureListener extends  SimpleOnGestureListener
    {
        private static final int swipeThreshold = 100;
        private static final int swipeVelocityThreshold = 100;


        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2,float velocityX, float velocityY)
        {
            boolean result = false;
            Log.d("Gesture", "onFling");
            try {
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > swipeThreshold) {
                    if (diffX > 0) {
                        ((Player)entityList.get(0)).Dash(dashLength);

                    } else
                    {
                        ((Player)entityList.get(0)).Dash(-dashLength);
                    }
                }
                result = true;
            }
            catch(Exception exception)
            {
                exception.printStackTrace();
            }
            return result;
        }
    }
}
