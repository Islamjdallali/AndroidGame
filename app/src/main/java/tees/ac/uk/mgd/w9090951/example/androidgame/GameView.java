package tees.ac.uk.mgd.w9090951.example.androidgame;

import android.app.Activity;
import android.content.Context;
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
import android.graphics.Rect;
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
    private int currentFrame = 0;
    private boolean isMoving;
    private int spawnXMinPos = 0;
    private int spawnXMaxPos = 200;
    private int spawnTimerMax = 10;
    private int spawnTimerMin = 5;
    private float spawnTimer;
    private float xPos;
    private float yPos;
    private float steerVelocity;
    private List<Fire> fireList = new ArrayList<Fire>();
    private float velocity = 100;
    private float dashLength = 100;
    private Rect frameToDraw = new Rect(0,0,frameW,frameH);
    private Rect whereToDraw = new Rect((int)xPos,(int)yPos, (int)xPos + frameW, frameH);
    private Rect whereToDrawFire = new Rect((int)xPos,(int)yPos, (int)xPos + frameW, frameH);
    private Canvas canvas;
    private float lastFrameChangeTime = 1;
    private float frameLengthInMs = 2;
    OnSwipeTouchListener onSwipeTouchListener;
    TileSensor tileSensor;

    public GameView(Context context) {
        super(context);
        onSwipeTouchListener = new OnSwipeTouchListener(context);
        spawnTimer = new Random().nextInt((spawnTimerMax - spawnTimerMin) + 1) + spawnTimerMin;
        tileSensor = new TileSensor(context);
        Log.d("Spawner : ", String.valueOf(spawnTimer));
        xPos = context.getResources().getDisplayMetrics().widthPixels / 2;
        yPos = context.getResources().getDisplayMetrics().heightPixels / 2;
        surfaceHolder = getHolder();
        playerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.player);
        playerBitmap = Bitmap.createScaledBitmap(playerBitmap,frameW * frameCount,frameH,false);
        fireBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.fire);
        fireBitmap = Bitmap.createScaledBitmap(fireBitmap,frameW * frameCount,frameH,false);
    }

    @Override
    public void run()
    {
        while (playing)
        {
            long startFrameTime = System.currentTimeMillis();
            update();
            draw();
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

        //Log.d("Timer : ", String.valueOf(spawnTimer));

        if (spawnTimer <= 0)
        {
            //Log.d("Spawner : ","Spawn now");

            fireList.add(new Fire(new Random().nextInt((spawnXMaxPos - spawnXMinPos) + 1) + spawnXMinPos,0));
            spawnTimer = new Random().nextInt((spawnTimerMax - spawnTimerMin) + 1) + spawnTimerMin;
        }

        for (int i = 0; i < fireList.size(); i++)
        {
            fireList.get(i).Move(fps);
        }

        xPos += steerVelocity;

        if (isMoving)
        {
            xPos = xPos + velocity / fps;

            if (xPos > getWidth())
            {
                yPos += frameH;
                xPos = 800;
            }
            if (yPos + frameH > getHeight())
            {
                yPos = 800;
            }
        }

    }


    private void draw()
    {
        if (surfaceHolder.getSurface().isValid())
        {
            canvas = surfaceHolder.lockCanvas();
            canvas.drawColor(Color.WHITE);
            whereToDraw.set((int)xPos,(int)yPos,(int)xPos + frameW, (int) yPos + frameH);
            for (int i = 0; i < fireList.size(); i++)
            {
                whereToDrawFire.set((int)fireList.get(i).getPosX(), (int)fireList.get(i).getPosY(),(int)fireList.get(i).getPosX() + frameW, (int)fireList.get(i).getPosY() + frameH);
                canvas.drawBitmap(fireBitmap,frameToDraw,whereToDrawFire,null);
            }
            manageFrame();
            canvas.drawBitmap(playerBitmap,frameToDraw,whereToDraw,null);
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void manageFrame()
    {
        long time = System.currentTimeMillis();
        if (time > lastFrameChangeTime + frameLengthInMs)
        {
            lastFrameChangeTime = time;
            currentFrame++;
            if (currentFrame >= frameCount)
            {
                currentFrame = 0;
            }
        }

        frameToDraw.left = currentFrame * frameW;
        frameToDraw.right = frameToDraw.left + frameW;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        switch (event.getAction() & MotionEvent.ACTION_MASK) {
//            case MotionEvent.ACTION_DOWN:
//                Log.d("TAG", "onTouchEvent: Down");
//                isMoving = !isMoving;
//                break;
//        }
//
//        return true;
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
                       xPos += dashLength;

                    } else
                    {
                        xPos -= dashLength;
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
