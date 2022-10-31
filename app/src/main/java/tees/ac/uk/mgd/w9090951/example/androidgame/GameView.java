package tees.ac.uk.mgd.w9090951.example.androidgame;

import android.content.Context;
import android.gesture.Gesture;
import android.util.DisplayMetrics;
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

public class GameView extends SurfaceView implements Runnable {

    private volatile boolean playing = true;
    private Thread gameThread;
    private long timeThisFrame;
    private float fps;
    private SurfaceHolder surfaceHolder;
    private Bitmap bitmap;
    private int frameW = 50;
    private int frameH = 50;
    private int frameCount = 1;
    private int currentFrame = 0;
    private boolean isMoving;
    private float xPos;
    private float yPos;
    private float velocity = 100;
    private float dashLength = 100;
    private Rect frameToDraw = new Rect(0,0,frameW,frameH);
    private Rect whereToDraw = new Rect((int)xPos,(int)yPos, (int)xPos + frameW, frameH);
    private Canvas canvas;
    private float lastFrameChangeTime = 1;
    private float frameLengthInMs = 2;
    OnSwipeTouchListener onSwipeTouchListener;
    DisplayMetrics displayMetrics;

    public GameView(Context context) {
        super(context);
        onSwipeTouchListener = new OnSwipeTouchListener(context);
        displayMetrics = new DisplayMetrics();
        xPos = context.getResources().getDisplayMetrics().widthPixels / 2;
        yPos = context.getResources().getDisplayMetrics().heightPixels / 2;
        surfaceHolder = getHolder();
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.player);
        bitmap = Bitmap.createScaledBitmap(bitmap,frameW * frameCount,frameH,false);
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
            manageFrame();
            canvas.drawBitmap(bitmap,frameToDraw,whereToDraw,null);
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
