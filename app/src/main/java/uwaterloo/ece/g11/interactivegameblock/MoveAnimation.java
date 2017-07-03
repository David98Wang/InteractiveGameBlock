package uwaterloo.ece.g11.interactivegameblock;

import android.app.Activity;
import android.content.Context;
import android.widget.RelativeLayout;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.TimerTask;

/**
 * Created by whcda on 7/2/2017.
 */

public class MoveAnimation extends TimerTask {
    private Activity activity;
    private static final int initialVelocity = 25;
    ArrayList<GameBlock> gameBlocks;
    int prev[][];
    int cur[][];
    int direction;
    public MoveAnimation(Activity activity, ArrayList<GameBlock> gameBlocks, int prev[][], int cur[][],int direction) {
        this.activity = activity;
        this.gameBlocks = gameBlocks;
        this.prev = prev;
        this.cur = cur;
        this.direction = direction;
    }

    public void run() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(direction == 1)
                    moveUp();
                else if(direction ==2 )
                    moveRight();
                else if(direction ==3 )
                    moveDown();
                else
                    moveLeft();
            }
        });
    }
    private void moveUp(){

    }
    private  void moveRight(){

    }
    private void moveDown(){

    }
    private void moveLeft(){

    }
}
