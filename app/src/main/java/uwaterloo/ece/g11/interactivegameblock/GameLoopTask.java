package uwaterloo.ece.g11.interactivegameblock;

import android.app.Activity;
import android.content.Context;
import android.hardware.SensorEventListener;
import android.widget.RelativeLayout;

import java.util.TimerTask;

/**
 * Created by whcda on 6/19/2017.
 */

public class GameLoopTask extends TimerTask {
    private static int cycleWaitDuration = 10;
    private static int blockVelocity = 25;
    defaultSensorEventListener accelerometerListener;
    GameBlock block;
    private Activity activity;
    private RelativeLayout loGame;
    private Context context;
    private int cycleSinceLastMove = 0;


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
        cycleSinceLastMove = 0;
    }

    @Override
    public void run() {
        activity.runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        //if (cycleSinceLastMove <= cycleWaitDuration)
                        //    return;
                        //Log.d("RUN",String.format("%d, %d",curX,curY));
                        int tempX = block.curX, tempY = block.curY;
                        if (block.curX == block.destX && block.curY == block.destY) {

                            if (MainActivity.up && tempY != 0)
                                block.destY -= GameBlock.blockLength;
                            else if (MainActivity.down && tempY != 3)
                                block.destY += GameBlock.blockLength;
                            else if (MainActivity.left && tempX != 0)
                                block.destX -= GameBlock.blockLength;
                            else if (MainActivity.right && tempX != 3)
                                block.destX += GameBlock.blockLength;
                            MainActivity.up = MainActivity.down = MainActivity.left = MainActivity.right = false;

                        } else {
                            if (block.curY < block.destY)
                                tempY += blockVelocity;
                            else if (block.curY > block.destY)
                                tempY -= blockVelocity;
                            else if (block.curX < block.destX)
                                tempX += blockVelocity;
                            else if (block.curX > block.destX)
                                tempX -= blockVelocity;
                            redraw(tempX, tempY);

                        }


                    }
                }
        );

    }

    private void redraw(int desX, int desY) {
        block.setLocation(desX, desY);
    }
}
