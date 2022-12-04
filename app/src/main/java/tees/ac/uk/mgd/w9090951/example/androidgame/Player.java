package tees.ac.uk.mgd.w9090951.example.androidgame;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.SurfaceHolder;

public class Player extends Entities
{
    public Player(String n,Bitmap bm, int height, int width,int frameC, int startPosX, int startPosY, SurfaceHolder holder,boolean startIsAlive,boolean collide)
    {
        super(n,bm, height, width, frameC, startPosX, startPosY, holder,startIsAlive,collide);
    }

    private float dashSpeed;
    private float dashDistance;
    private float endPos;
    private float gameFps;

    @Override
    public void Move(float fps)
    {
        gameFps = fps;
        xPos += velocity;
    }

    @Override
    public void GetSteerInput(float steerVelocity)
    {
        velocity = steerVelocity;
    }

    public void Dash(float dashLength)
    {
        //if we are dashing to the right
        if (dashLength > 0)
        {
            endPos = xPos + dashLength;
            dashDistance = Math.abs(endPos - xPos);
            dashSpeed = 0.005f / gameFps;
        }

        while (dashDistance > 0.1f)
        {
            xPos += dashSpeed;
            dashDistance = endPos - xPos;
        }

        //xPos += dashLength;
    }
}
