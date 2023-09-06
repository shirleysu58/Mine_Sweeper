package com.example.gridlayout;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ResultPage extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_page);

        Intent intent = getIntent();
        boolean wonGame = intent.getBooleanExtra("wonGame", false);

        if (wonGame) {
            String timePassed = Integer.toString(intent.getIntExtra("time", 0));
            // change text display
            TextView textView = findViewById(R.id.textViewResult);
            textView.setText("You Won! Passed in " + timePassed + "seconds");
        }

        findViewById(R.id.buttonNavigateToMain).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the second activity
                Intent intent = new Intent(ResultPage.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
