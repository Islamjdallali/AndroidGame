package tees.ac.uk.mgd.w9090951.example.androidgame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class GameActivity extends AppCompatActivity {

    GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_acivity);
        gameView = new GameView(this,this);
        setContentView(gameView);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        gameView.resume();
    }

    @Override
    protected  void onPause()
    {
        super.onPause();
        gameView.pause();
    }
}