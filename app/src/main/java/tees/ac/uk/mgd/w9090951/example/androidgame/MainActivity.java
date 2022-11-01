package tees.ac.uk.mgd.w9090951.example.androidgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void LaunchGameActivity(View view)
    {
        Intent intent = new Intent(this,GameActivity.class);
        startActivity(intent);
    }

    public void QuitGame(View view)
    {
        finish();
        System.exit(0);
    }
}