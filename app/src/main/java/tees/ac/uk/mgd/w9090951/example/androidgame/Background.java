package tees.ac.uk.mgd.w9090951.example.androidgame;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.SurfaceHolder;

public class Background extends Entities
{
    public Background(String n,Bitmap bm, int height, int width,int frameC, int startPosX, int startPosY, SurfaceHolder holder, boolean startIsAlive,boolean collide)
    {
        super(n,bm, height, width, frameC, startPosX, startPosY, holder, startIsAlive,collide);
        velocity = 20;
    }

    @Override
    public void Move(float fps)
    {
        yPos += velocity;

        Log.d("BG", "frame height: " + frameH);

        if (yPos > frameH)
        {
            yPos = -1080;
        }
    }

    @Override
    public void GetSteerInput(float velocity) {

    }
}
