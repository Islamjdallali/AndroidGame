package tees.ac.uk.mgd.w9090951.example.androidgame;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.SurfaceHolder;
import java.util.Random;


public class Fire extends Entities
{
    private float velocity = 500;
    private int screenWidth;

    public Fire(Bitmap bm, int height, int width, int startPosX, int startPosY, SurfaceHolder holder, boolean startIsAlive)
    {
        super(bm, height, width, startPosX, startPosY, holder,startIsAlive);
        screenWidth = startPosX;
        xPos = new Random().nextInt(screenWidth);
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
}
