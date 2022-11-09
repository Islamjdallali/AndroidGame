package tees.ac.uk.mgd.w9090951.example.androidgame;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.SurfaceHolder;

public class Player extends Entities
{

    public Player(Bitmap bm, int height, int width, int startPosX, int startPosY, SurfaceHolder holder)
    {
        super(bm, height, width, startPosX, startPosY, holder);
        Log.d("TAG", "Player Pos X : " + xPos);
    }

    @Override
    public void Move(float velocity)
    {
        xPos += velocity;

    }

    public void Dash(float dashLength)
    {
        xPos += dashLength;
    }
}
