package tees.ac.uk.mgd.w9090951.example.androidgame;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.SurfaceHolder;

public class Background extends Entities
{
    public Background(String n,Bitmap bm, int height, int width, int startPosX, int startPosY, SurfaceHolder holder, boolean startIsAlive,boolean collide)
    {
        super(n,bm, height, width, startPosX, startPosY, holder, startIsAlive,collide);
        velocity = 500;
    }

    @Override
    public void Move(float fps)
    {
        yPos += velocity / fps;

        Log.d("BG", "Move: " + yPos);

        if (yPos > frameH)
        {
            yPos = 0;
        }
    }

    @Override
    public void GetSteerInput(float velocity) {

    }
}
