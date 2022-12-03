package tees.ac.uk.mgd.w9090951.example.androidgame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceHolder;

public abstract class Entities
{
    private Bitmap entityBitmap;
    protected int frameW = 50;
    protected int frameH = 50;
    private int frameCount = 1;
    private int currentFrame = 0;
    protected float xPos;
    protected float yPos;
    protected float startXPos;
    protected float startYPos;
    protected float velocity;
    private Rect frameToDraw = new Rect(0,0,frameW,frameH);
    private Rect whereToDraw = new Rect((int)xPos,(int)yPos, (int)xPos + frameW, frameH);
    private long lastFrameChangeTime = 1;
    private long frameLengthInMs = 50;
    private SurfaceHolder surfaceHolder;
    protected boolean isAlive;
    protected boolean canCollide;

    protected String entityName;

    public Entities(String name,Bitmap bm, int height, int width,int frameC, int startPosX, int startPosY, SurfaceHolder holder, boolean startIsAlive,boolean collide)
    {
        entityName = name;
        surfaceHolder = holder;
        entityBitmap = bm;
        frameW = width;
        frameH = height;
        startXPos = startPosX;
        startYPos = startPosY;
        xPos = startPosX;
        yPos = startPosY;
        isAlive = startIsAlive;
        canCollide = collide;
        frameCount = frameC;
        currentFrame = 0;
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

    public String GetName()
    {
        return entityName;
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

        if (time > (lastFrameChangeTime + frameLengthInMs))
        {
            currentFrame += 1;

            Log.d("Animation", "Time: " + time);
            Log.d("Animation", "target Time: " + (lastFrameChangeTime + frameLengthInMs));

            if (currentFrame >= frameCount)
            {
                currentFrame = 0;
            }

            lastFrameChangeTime = time;
        }

        frameToDraw.left = currentFrame * frameW;
        frameToDraw.right = frameToDraw.left + frameW;
    }

    public abstract void Move(float fps);
    public abstract void GetSteerInput(float velocity);
}
