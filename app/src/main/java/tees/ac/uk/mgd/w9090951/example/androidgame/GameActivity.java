package tees.ac.uk.mgd.w9090951.example.androidgame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class GameActivity extends AppCompatActivity {

    GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_acivity);
        gameView = new GameView(this);
        setContentView(gameView);
    }
}