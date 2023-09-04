package com.example.gridlayout;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final int COLUMN_COUNT = 6;

    // save the TextViews of all cells in an array, so later on,
    // when a TextView is clicked, we know which cell it is
    private ArrayList<TextView> cell_tvs;
    private TextView[][] mineSweeperGrid;
    private boolean[][] isBomb;

    private int clock = 0;
    private boolean running = false;


    private int dpToPixel(int dp) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cell_tvs = new ArrayList<TextView>();
        mineSweeperGrid = new TextView[6][6];
        isBomb = new boolean[6][6];

        GridLayout grid = (GridLayout) findViewById(R.id.gridLayout01);
        LayoutInflater li = LayoutInflater.from(this);
        for (int i = 0; i<6; i++) {
            for (int j=0; j<6; j++) {
                TextView tv = (TextView) li.inflate(R.layout.custom_cell_layout, grid, false);
                //tv.setText(String.valueOf(i)+String.valueOf(j));
                tv.setTextColor(Color.GRAY);
                tv.setBackgroundColor(Color.GRAY);
                tv.setOnClickListener(this::onClickTV);

                GridLayout.LayoutParams lp = (GridLayout.LayoutParams) tv.getLayoutParams();
                lp.rowSpec = GridLayout.spec(i);
                lp.columnSpec = GridLayout.spec(j);

                grid.addView(tv, lp);

                cell_tvs.add(tv);
                mineSweeperGrid[i][j] = tv;
            }
        }
        placeRandomMines();
        runTimer();

    }
    private void placeRandomMines() { // part of mine sweeper set up
        int uniqueBombCnt = 0;
        while (uniqueBombCnt < 4) {
            int r= (int)(Math.random() * 6);
            int c = (int)(Math.random() * 6);

            if (isBomb[r][c]) { // no repeat bombs
                continue;
            }

            isBomb[r][c] = true;
            uniqueBombCnt++;
        }

    }

    private int numAdjacentMine(int i, int j) { // get adjacent bombs
        int[] row = {1,-1,0,0,1,-1,-1,1};
        int[] col = {0,0,1,-1,1,-1,1,-1};

        int adjBombCnt = 0;

        for (int k = 0; k < 8; k++) {
            int r1 = i + row[k];
            int c1 = j + col[k];

            if (r1 >= 0 && r1 < 6 && c1 >= 0 && c1 < 6 && isBomb[r1][c1]) {
                adjBombCnt++;
            }
        }
        return adjBombCnt;
    }
    private void revealAllAdjacentBFS(int i, int j) { // if adjcant has bomb, then stop there
        Queue<int[]> queue = new LinkedList();
        int[] row = {1,-1,0,0,1,-1,-1,1};
        int[] col = {0,0,1,-1,1,-1,1,-1};

        boolean[][] hasVisited = new boolean[6][6];
        queue.add(new int[]{i, j}); // add to queue
        hasVisited[i][j] = true; //
        while (!queue.isEmpty()) {
            int[] curr = queue.poll(); // pop it out
            for (int k = 0; k < 8; k++) { // check all adjacent
                int r1 = curr[0] + row[k];
                int c1 = curr[1] + col[k];

                if (r1 >= 0 && r1 < 6 && c1 >= 0 && c1 < 6 && !hasVisited[r1][c1]) {
                    int adjBomb = numAdjacentMine(r1, c1);
                    if (adjBomb == 0) {
                        queue.add(new int[]{r1, c1}); // add no adj bomb to queue
                    }
                    // reveal tile (bomb adj)
                    revealTile(mineSweeperGrid[r1][c1], adjBomb);

                    // update has visited grid
                    hasVisited[r1][c1] = true;
                }
            }
        }
    }

    private void revealTile(TextView tv, int numAdjBomb) {
        tv.setTextColor(Color.GRAY);
        tv.setBackgroundColor(Color.LTGRAY);
        tv.setText(String.valueOf(numAdjBomb));
    }

    private int findIndexOfCellTextView(TextView tv) {
        for (int n=0; n<cell_tvs.size(); n++) {
            if (cell_tvs.get(n) == tv)
                return n;
        }
        return -1;
    }


    public void onEndTimer() {
        running = false;
    }

    private void runTimer() {

        final TextView timeView = (TextView) findViewById(R.id.textView);
        final Handler handler = new Handler();
        running = true;

        handler.post(new Runnable() {
            @Override
            public void run() {
                int hours =clock/3600;
                int minutes = (clock%3600) / 60;
                int seconds = clock%60;
                String time = String.format("%d:%02d:%02d", hours, minutes, seconds);
                timeView.setText(time);

                if (running) {
                    clock++;
                }
                handler.postDelayed(this, 1000);
            }
        });
    }

    public void onClickTV(View view){
        TextView tv = (TextView) view;
        int n = findIndexOfCellTextView(tv);
        int i = n/COLUMN_COUNT; // row idx
        int j = n%COLUMN_COUNT; // col idx

        if (isBomb[i][j]) {
            tv.setTextColor(Color.GREEN);
            tv.setBackgroundColor(Color.parseColor("lime"));
            onEndTimer();
        } else {
            int numAdjBomb = numAdjacentMine(i, j);
            revealTile(mineSweeperGrid[i][j], numAdjBomb);
            if (numAdjBomb == 0) {
                revealAllAdjacentBFS(i,j);
            }
        }
//         tv.setText(String.valueOf(i)+String.valueOf(j));
//                if (tv.getCurrentTextColor() == Color.GRAY) {
//                    tv.setTextColor(Color.GREEN);
//                    tv.setBackgroundColor(Color.parseColor("lime"));
//                }else {
//                    tv.setTextColor(Color.GRAY);
//                    tv.setBackgroundColor(Color.LTGRAY);
//                }
    }
}