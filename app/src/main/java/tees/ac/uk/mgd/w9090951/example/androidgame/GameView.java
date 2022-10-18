package tees.ac.uk.mgd.w9090951.example.androidgame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements Runnable {

    private volatile boolean playing;
    private Thread gameThread;
    private long timeThisFrame;
    private float fps;
    private SurfaceHolder surfaceHolder;
    private Bitmap bitmap;
    private int frameW;
    private int frameH;
    private int frameCount;

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

    }

    private void draw()
    {

    }

    public void resume()
    {
        gameThread = new Thread(this);
        gameThread.start();
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
