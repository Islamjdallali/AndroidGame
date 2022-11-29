package tees.ac.uk.mgd.w9090951.example.androidgame;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.SurfaceHolder;

public class Player extends Entities
{
    public Player(String n,Bitmap bm, int height, int width, int startPosX, int startPosY, SurfaceHolder holder,boolean startIsAlive,boolean collide)
    {
        super(n,bm, height, width, startPosX, startPosY, holder,startIsAlive,collide);
        Log.d("TAG", "Player Pos X : " + xPos);
    }

    @Override
    public void Move(float fps)
    {
        xPos += velocity;

    }

    @Override
    public void GetSteerInput(float steerVelocity)
    {
        velocity = steerVelocity;
        Log.d("TEST", "called");
    }

    public void Dash(float dashLength)
    {
        xPos += dashLength;
    }
}
