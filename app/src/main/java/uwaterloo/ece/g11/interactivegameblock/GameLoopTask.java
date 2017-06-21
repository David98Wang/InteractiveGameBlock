package uwaterloo.ece.g11.interactivegameblock;

import android.app.Activity;
import android.content.Context;
import android.graphics.Path;
import android.hardware.SensorEventListener;
import android.util.Log;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.TimerTask;

/**
 * Created by whcda on 6/19/2017.
 */

public class GameLoopTask extends TimerTask {
    defaultSensorEventListener accelerometerListener;
    private Activity activity;
    private RelativeLayout loGame;
    private Context context;
    private int cycleSinceLastMove = 0;
    private static int cycleWaitDuration = 10;
    int curX, curY, desX, desY;
    GameBlock block;


    /**
     * Constructor for GameLoopTask. Just fill in variables by name
     *
     * @param accelerometerListener
     * @param activity
     * @param loGame
     * @param context
     */
    public GameLoopTask(SensorEventListener accelerometerListener, Activity activity, RelativeLayout loGame, Context context, GameBlock block) {
        this.accelerometerListener = (defaultSensorEventListener) accelerometerListener;
        this.activity = activity;
        this.loGame = loGame;
        this.context = context;
        this.block = block;
        curX = curY = 0;
        desX = desY = 0;
        cycleSinceLastMove = 0;
    }

    @Override
    public void run() {
        activity.runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        desY = curY;
                        desX = curX;
                        //if (cycleSinceLastMove <= cycleWaitDuration)
                        //    return;
                        Log.d("RUN",String.format("%d, %d",curX,curY));
                        if (MainActivity.up) {
                            if (curY != 0) desY--;
                        } else if (MainActivity.down) {
                            if (curY != 3) desY++;
                        } else if (MainActivity.left) {
                            if (curX != 0) desX--;
                        } else if (MainActivity.right) {
                            if (curX != 3) desX++;
                        }
                        redraw(desX,desY);
                        MainActivity.up=MainActivity.down=MainActivity.left=MainActivity.right=false;
                        curX=desX;
                        curY=desY;
                    }
                }
        );

    }

    private void redraw(int desX,int desY) {
        block.setLocation(desX,desY);
    }
}
