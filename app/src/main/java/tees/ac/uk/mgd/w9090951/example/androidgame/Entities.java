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
    protected float startXPos;
    protected float startYPos;
    private Rect frameToDraw = new Rect(0,0,frameW,frameH);
    private Rect whereToDraw = new Rect((int)xPos,(int)yPos, (int)xPos + frameW, frameH);
    private float lastFrameChangeTime = 1;
    private float frameLengthInMs = 2;
    private SurfaceHolder surfaceHolder;
    protected boolean isAlive;

    public Entities(Bitmap bm, int height, int width, int startPosX, int startPosY, SurfaceHolder holder, boolean startIsAlive)
    {
        surfaceHolder = holder;
        entityBitmap = bm;
        frameW = width;
        frameH = height;
        startXPos = startPosX;
        startYPos = startPosY;
        xPos = startPosX;
        yPos = startPosY;
        isAlive = startIsAlive;
    }

    public void draw(Canvas canvas)
    {
        if (isAlive)
        {
            if (surfaceHolder.getSurface().isValid())
            {
                whereToDraw.set((int)xPos,(int)yPos,(int)xPos + frameW, (int) yPos + frameH);
                manageFrame();
                canvas.drawBitmap(entityBitmap,frameToDraw,whereToDraw,null);
            }
        }
    }

    public void SetAlive(boolean isDead)
    {
        isAlive = isDead;
    }

    public boolean GetIsAlive()
    {
        return isAlive;
    }

    public void Restart()
    {
        xPos = startXPos;
        yPos = startYPos;
        isAlive = true;
    }

    public void SetPos(float x, float y)
    {
        xPos = x;
        yPos = y;
    }

    public float GetYPos()
    {
        return yPos;
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
