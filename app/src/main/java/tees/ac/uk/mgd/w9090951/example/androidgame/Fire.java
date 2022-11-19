package tees.ac.uk.mgd.w9090951.example.androidgame;

import android.graphics.Bitmap;
import android.view.SurfaceHolder;
import java.util.Random;


public class Fire extends Entities
{
    private float velocity = 500;

    public Fire(Bitmap bm, int height, int width, int startPosX, int startPosY, SurfaceHolder holder)
    {
        super(bm, height, width, startPosX, startPosY, holder);
        xPos = new Random().nextInt(startPosX);
    }

    @Override
    public void Move(float fps)
    {
        yPos += velocity / fps;
    }
}
