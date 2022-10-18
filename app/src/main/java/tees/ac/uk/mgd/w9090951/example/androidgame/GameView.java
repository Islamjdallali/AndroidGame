package tees.ac.uk.mgd.w9090951.example.androidgame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements Runnable {

    private volatile boolean playing = true;
    private Thread gameThread;
    private long timeThisFrame;
    private float fps;
    private SurfaceHolder surfaceHolder;
    private Bitmap bitmap;
    private int frameW = 800;
    private int frameH = 800;
    private int frameCount = 1;
    private int currentFrame = 0;
    private boolean isMoving;
    private float xPos;
    private float yPos;
    private float velocity = 10;
    private Rect frameToDraw = new Rect(0,0,frameW,frameH);
    private Rect whereToDraw = new Rect((int)xPos,(int)yPos, (int)xPos + frameW, frameH);
    private Canvas canvas;
    private float lastFrameChangeTime = 1;
    private float frameLengthInMs = 2;

    public GameView(Context context) {
        super(context);
        surfaceHolder = getHolder();
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.circle);
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
    public boolean onTouchEvent(MotionEvent event)
    {
        switch (event.getAction() & MotionEvent.ACTION_MASK)
        {
            case MotionEvent.ACTION_DOWN:
                isMoving = !isMoving;
                break;
        }

        return true;
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
}
