package tees.ac.uk.mgd.w9090951.example.androidgame;

import android.app.Activity;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.view.View;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameView extends SurfaceView implements Runnable {

    private String DEBUG_TAG = "Test";

    private volatile boolean playing = true;
    private Thread gameThread;
    private long timeThisFrame;
    private float fps;
    private SurfaceHolder surfaceHolder;
    private Bitmap playerBitmap;
    private Bitmap fireBitmap;
    private Bitmap backgroundBitmap;
    private int frameW = 50;
    private int frameH = 50;
    private int frameCount = 1;
    private int spawnTimerMax = 2;
    private int spawnTimerMin = 0;
    private float spawnTimer;
    private int screenWidth;
    private int screenHeight;
    private float steerVelocity;
    private List<Entities> entityList = new ArrayList<Entities>();
    private float dashLength = 500;
    private Canvas canvas;
    private Activity activity;
    OnSwipeTouchListener onSwipeTouchListener;
    TileSensor tileSensor;

    private float score;
    private float highScore;
    private Paint scorePaint;

    private int maxFireCount = 50;

    private Rect restartButton;
    private Bitmap restartBitmap;

    private Rect quitButton;
    private Bitmap quitBitmap;

    private MediaPlayer bgmusicPlayer;
    private MediaPlayer explosionPlayer;
    private MediaPlayer dashPlayer;

    private boolean isPlayerAlive;

    public GameView(Context context, Activity act) {
        super(context);
        onSwipeTouchListener = new OnSwipeTouchListener(context);
        spawnTimer = new Random().nextFloat() * (spawnTimerMax - spawnTimerMin) + spawnTimerMin;
        tileSensor = new TileSensor(context);
        Log.d("Spawner : ", String.valueOf(spawnTimer));
        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        screenHeight = context.getResources().getDisplayMetrics().heightPixels;
        surfaceHolder = getHolder();
        //Get all of the relevant bitmaps
        playerBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.player);
        playerBitmap = Bitmap.createScaledBitmap(playerBitmap,frameW * frameCount,frameH,false);
        fireBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.testfireball);
        fireBitmap = Bitmap.createScaledBitmap(fireBitmap,150,50,false);

        backgroundBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.background2);
        backgroundBitmap = Bitmap.createScaledBitmap(backgroundBitmap,2076,1080,false);

        restartBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.restart);
        restartBitmap = Bitmap.createScaledBitmap(restartBitmap,200,100,false);
        restartButton = new Rect((screenWidth / 2) - 100,screenHeight / 2 - 100,(screenWidth / 2) + 100,(screenHeight / 2));

        quitBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.quit);
        quitBitmap = Bitmap.createScaledBitmap(quitBitmap,200,100,false);
        quitButton = new Rect((screenWidth / 2) - 100,(screenHeight / 2) + 100,(screenWidth / 2) + 100,(screenHeight / 2) + 200);

        //Get textsize and color of the scoretext
        scorePaint = new Paint();
        scorePaint.setColor(Color.BLACK);
        scorePaint.setTextSize(50);

        activity = act;

        //Set the highscore and set the score to 0

        SharedPreferences settings = context.getSharedPreferences("ScorePrefs",0);
        highScore = settings.getFloat("HighScore",0);


        score = 0;

        //Add Player,Fire and the background into the entity list

        Background bg1 = new Background("BG1",backgroundBitmap,1080,2076,1, 0, 0,surfaceHolder,true,false);
        //entityList.add(bg1);
        Background bg2 = new Background("BG2",backgroundBitmap,1080,2076,1, 0, -1080,surfaceHolder,true,false);
        //entityList.add(bg2);

        Player player = new Player("Player",playerBitmap,frameH,frameW,1, screenWidth / 2, screenHeight / 2,surfaceHolder,true,true);
        entityList.add(player);

        for(int i = 0; i < maxFireCount; i++)
        {
            entityList.add(new Fire("Fire",fireBitmap,50,50,3, screenWidth,0,surfaceHolder,false,true));
        }

        isPlayerAlive = true;

        //get the revelant music files
        bgmusicPlayer = MediaPlayer.create(context,R.raw.bgmusic);
        explosionPlayer = MediaPlayer.create(context,R.raw.explosion);
        dashPlayer = MediaPlayer.create(context,R.raw.dash);

        //loop and start the bg music
        if (!bgmusicPlayer.isLooping())
        {
            bgmusicPlayer.setLooping(true);
        }
        bgmusicPlayer.start();

        //Log.d("GameView", "ScreenHeight: " + screenHeight);
        //Log.d("GameView", "ScreenWidth: " + screenWidth);
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
            for(int i = 1; i < entityList.size(); i++)
            {
                if (entityList.get(i).GetName() == "Fire")
                {
                    if (!entityList.get(i).GetIsAlive())
                    {
                        entityList.get(i).SetAlive(true);
                        break;
                    }
                }
            }

            spawnTimer = new Random().nextFloat() * (spawnTimerMax - spawnTimerMin) + spawnTimerMin;
        }

        for (int i = 0; i < entityList.size(); i++)
        {
            if (entityList.get(i).GetIsAlive())
            {
                entityList.get(i).Move(fps);
                entityList.get(i).GetSteerInput(steerVelocity);

                for (int j = i + 1; j < entityList.size(); j++)
                {
                    if (entityList.get(i).canCollide && entityList.get(j).canCollide)
                    {
                        if (entityList.get(i).isCollision(entityList.get(j).xPos,entityList.get(j).yPos))
                        {
                            if (entityList.get(i).GetName() == "Player")
                            {
                                explosionPlayer.start();
                                entityList.get(i).SetAlive(false);
                                entityList.get(j).SetAlive(false);
                                isPlayerAlive = false;
                            }
                        }
                    }
                }
            }
            else
            {
                entityList.get(i).SetPos(new Random().nextInt(screenWidth),0);
            }

            if (entityList.get(i).GetName() == "Fire")
            {
                if (entityList.get(i).GetYPos() > screenHeight)
                {
                    entityList.get(i).SetAlive(false);
                }
            }
        }

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

            if (isPlayerAlive)
            {
                score = score + 0.1f;
                SharedPreferences settings = activity.getApplicationContext().getSharedPreferences("ScorePrefs",0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putFloat("HighScore",score);

                editor.apply();
            }
            else
            {
                SharedPreferences settings = activity.getApplicationContext().getSharedPreferences("ScorePrefs",0);
                float currentHighScore = settings.getFloat("HighScore",0);
                if (currentHighScore > highScore)
                {
                    highScore = currentHighScore;
                }

                canvas.drawText("Highscore : " + (int)highScore, screenWidth / 2,100,scorePaint);

                canvas.drawBitmap(restartBitmap,null, restartButton,null);
                canvas.drawBitmap(quitBitmap,null, quitButton,null);
            }

            canvas.drawText("Score : " + (int)score, screenWidth / 2,50,scorePaint);
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (!isPlayerAlive)
        {
            if (restartButton.contains((int)event.getX(), (int)event.getY()))
            {
                for (int i = 0; i < entityList.size(); i++)
                {
                    entityList.get(i).Restart();
                    isPlayerAlive = true;
                    score = 0;
                }
            }

            if (quitButton.contains((int)event.getX(), (int)event.getY()))
            {
                bgmusicPlayer.stop();
                explosionPlayer.stop();
                dashPlayer.stop();
                activity.finish();
            }
        }

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
        private float alpha = 0.5f;
        private float orientationOutput;

        TileSensor(Context ctx)
        {
            sensorManager = (SensorManager) ctx.getSystemService(Context.SENSOR_SERVICE);
            registerListeners();
        }

        private float lowPass(float input, float output)
        {
            output = output + alpha * (input - output);
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

            float Y = lowPass( orientation[1],orientationOutput) *  57.2957795f;

            steerVelocity = -Y;
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
            if (SensorManager.getRotationMatrix(rotationMatrix, null, accel, mag))
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
                    if (diffX > 0)
                    {
                        ((Player)entityList.get(0)).Dash(dashLength);
                        dashPlayer.start();

                    } else
                    {
                        ((Player)entityList.get(0)).Dash(-dashLength);
                        dashPlayer.start();
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
