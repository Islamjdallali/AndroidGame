package tees.ac.uk.mgd.w9090951.example.androidgame;

import android.graphics.Bitmap;
import android.graphics.Rect;


public class Fire
{
    private float xPos;
    private float yPos;
    private float velocity = 50;
    private int frameW = 50;
    private int frameH = 50;
    private int frameCount = 1;
    private Rect whereToDraw = new Rect((int)xPos,(int)yPos, (int)xPos + frameW, frameH);

    public Fire(float startPosX, float startPosY)
    {
        xPos = startPosX;
        yPos = startPosY;
    }

    public void Move(float fps)
    {
        yPos += velocity / fps;
    }

    public float getPosX()
    {
        return xPos;
    }

    public float getPosY()
    {
        return yPos;
    }
}
