package tees.ac.uk.mgd.w9090951.example.androidgame;

import android.content.Context;
import android.util.Log;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements Runnable {

    private volatile boolean playing;
    private Thread gameThread;

    public GameView(Context context) {
        super(context);
    }

    @Override
    public void run() {
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
