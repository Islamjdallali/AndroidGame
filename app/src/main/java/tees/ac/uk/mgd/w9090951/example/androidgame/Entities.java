package tees.ac.uk.mgd.w9090951.example.androidgame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.view.SurfaceHolder;

public abstract class Entities
{
    private Bitmap entityBitmap;
    private int frameW = 50;
    private int frameH = 50;
    private int frameCount = 1;
    private int currentFrame = 0;
    protected float xPos;
    protected float yPos;
    private Rect frameToDraw = new Rect(0,0,frameW,frameH);
    private Rect whereToDraw = new Rect((int)xPos,(int)yPos, (int)xPos + frameW, frameH);
    private float lastFrameChangeTime = 1;
    private float frameLengthInMs = 2;
    private SurfaceHolder surfaceHolder;

    public Entities(Bitmap bm, int height, int width, int startPosX, int startPosY, SurfaceHolder holder)
    {
        surfaceHolder = holder;
        entityBitmap = bm;
        frameW = width;
        frameH = height;
        xPos = startPosX;
        yPos = startPosY;
    }

    public void draw(Canvas canvas)
    {
        if (surfaceHolder.getSurface().isValid())
        {
            whereToDraw.set((int)xPos,(int)yPos,(int)xPos + frameW, (int) yPos + frameH);
            manageFrame();
            canvas.drawBitmap(entityBitmap,frameToDraw,whereToDraw,null);
        }
    }

    public void Move()
    {

    }

    public boolean isCollision(float x2, float y2)
    {
        return x2 > xPos && x2 < xPos + frameW && y2 > yPos && y2 < yPos + frameH;
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

    public abstract void Move(float velocity);
}