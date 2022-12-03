package tees.ac.uk.mgd.w9090951.example.androidgame;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.SurfaceHolder;
import java.util.Random;


public class Fire extends Entities
{
    private int screenWidth;

    public Fire(String n,Bitmap bm, int height, int width,int frameC, int startPosX, int startPosY, SurfaceHolder holder, boolean startIsAlive, boolean collide)
    {
        super(n,bm, height, width, frameC, startPosX, startPosY, holder,startIsAlive,collide);
        screenWidth = startPosX;
        xPos = new Random().nextInt(screenWidth);
        velocity = 500;
    }

    @Override
    public void Move(float fps)
    {
        yPos += velocity / fps;

        if (!isAlive)
        {
            yPos = 0;
        }
    }

    @Override
    public void GetSteerInput(float velocity) {

    }
}
