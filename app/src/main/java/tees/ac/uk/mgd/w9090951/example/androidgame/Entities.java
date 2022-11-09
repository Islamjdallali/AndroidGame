package tees.ac.uk.mgd.w9090951.example.androidgame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.view.SurfaceHolder;

public class Entities
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
    private Canvas canvas;

    public Entities(Bitmap bm, int height, int width, float startPosX, float startPosY, SurfaceHolder holder)
    {
        surfaceHolder = holder;
        entityBitmap = bm;
        frameW = width;
        frameH = height;
        xPos = startPosX;
        yPos = startPosY;
    }

    public void draw()
    {
        if (surfaceHolder.getSurface().isValid())
        {
            canvas = surfaceHolder.lockCanvas();
            whereToDraw.set((int)xPos,(int)yPos,(int)xPos + frameW, (int) yPos + frameH);
            manageFrame();
            canvas.drawBitmap(entityBitmap,frameToDraw,whereToDraw,null);
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
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
}
