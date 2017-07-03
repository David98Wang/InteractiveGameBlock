package uwaterloo.ece.g11.interactivegameblock;

import android.app.Activity;
import android.content.Context;
import android.hardware.SensorEventListener;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.TimerTask;

/**
 * Created by David Wang on 6/19/2017.
 */

class GameLoopTask extends TimerTask {
    private static int cycleWaitDuration = 10;
    private boolean notMoving;
    private ArrayList<GameBlock> gameBlocks;
    private static int blockVelocity = 25;
    private defaultSensorEventListener accelerometerListener;
    private GameBlock block;
    private Activity activity;
    private RelativeLayout loGame;
    private Context context;
    private int cycleSinceLastMove = 0;

    private boolean justMoved = false;

    /**
     * Constructor for GameLoopTask. Just fill in variables by name
     *
     * @param accelerometerListener
     * @param activity
     * @param loGame
     * @param context
     */
    public GameLoopTask(SensorEventListener accelerometerListener, Activity activity, RelativeLayout loGame, Context context, ArrayList<GameBlock> gameBlocks) {
        this.accelerometerListener = (defaultSensorEventListener) accelerometerListener;
        this.activity = activity;
        this.loGame = loGame;
        this.context = context;
        this.gameBlocks = gameBlocks;
        this.block = block;
        cycleSinceLastMove = 0;
        notMoving = true;
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
                        notMoving = true;
                        for (int i = 0; i < gameBlocks.size(); i++)
                            if (gameBlocks.get(i).curY != gameBlocks.get(i).destY || gameBlocks.get(i).curX != gameBlocks.get(i).destX)
                                notMoving = false;
                        if (notMoving) {
                            blockVelocity = 25;
                            cleanUp();
                            //Log.d("BLOCK NOT", String.valueOf(gameBlocks.get(1).destY));
                            for (int i = 0; i < gameBlocks.size(); i++) {
                                if (gameBlocks.get(i).tvValue.getText() != String.valueOf(gameBlocks.get(i).value))
                                    gameBlocks.get(i).tvValue.setText(String.valueOf(gameBlocks.get(i).value));
                            }
                            if (justMoved) {
                                generateNewBlock();
                                justMoved = false;
                                return;
                            }
                            if (MainActivity.up) {
                                setUp();
                                justMoved = true;
                            } else if (MainActivity.down) {
                                setDown();
                                justMoved = true;
                            } else if (MainActivity.left) {
                                setLeft();
                                justMoved = true;
                            } else if (MainActivity.right) {
                                setRight();
                                justMoved = true;
                            }

                        } else {
                            //Log.d("BLOCK MOVING", String.valueOf(gameBlocks.get(1).destY));
                            for (int i = 0; i < gameBlocks.size(); i++) {
                                int blockCurX, blockCurY, blockDestX, blockDestY;
                                blockCurX = (int) gameBlocks.get(i).curX;
                                blockCurY = (int) gameBlocks.get(i).curY;
                                blockDestX = (int) gameBlocks.get(i).destX;
                                blockDestY = (int) gameBlocks.get(i).destY;
                                int velocity = Math.min(blockVelocity,Math.max(Math.abs(blockCurX-blockDestX),Math.abs(blockCurY-blockDestY)));
                                if (i == 1)
                                    Log.d("BLOCK LOOP", String.format("%d %d %d %d", blockCurX, blockCurY, blockDestX, blockDestY));
                                if (blockCurY < blockDestY)
                                    gameBlocks.get(i).moveY(velocity);
                                else if (blockCurY > blockDestY)
                                    gameBlocks.get(i).moveY(-1 * velocity);
                                else if (blockCurX < blockDestX)
                                    gameBlocks.get(i).moveX(velocity);
                                else if (blockCurX > blockDestX)
                                    gameBlocks.get(i).moveX(-1 * velocity);
                            }
                            blockVelocity += 10;
                        }
                        MainActivity.up = MainActivity.down = MainActivity.left = MainActivity.right = false;
                    }
                }
        );

    }

    private void setUp() {
        int index[][] = new int[4][4];
        for (int i = 0; i < 4; i++) for (int j = 0; j < 4; j++) index[i][j] = -1;
        for (int i = 0; i < gameBlocks.size(); i++)
            index[(int) (gameBlocks.get(i).curX / 250)][(int) (gameBlocks.get(i).curY / 250)] = i;
        //Collision detection loop and set destination loop
        for (int i = 0; i < 4; i++)
            Log.d("INLOOP", String.format("%d %d %d %d", index[0][i], index[1][i], index[2][i], index[3][i]));

        for (int xx = 0; xx < 4; xx++) {
            for (int yy = 1; yy < 4; yy++) {
                if (index[xx][yy] == -1) continue;
                int curValue = gameBlocks.get(index[xx][yy]).value;
                for (int cy = yy - 1; cy >= 0; cy--) {

                    if (index[xx][cy] != -1 && gameBlocks.get(index[xx][cy]).value == curValue && gameBlocks.get(index[xx][cy]).merged == false) {
                        gameBlocks.get(index[xx][cy]).merged = gameBlocks.get(index[xx][yy]).merged = true;
                        gameBlocks.get(index[xx][cy]).value = (gameBlocks.get(index[xx][cy]).value * 2);
                        gameBlocks.get(index[xx][yy]).destY = 0;
                        gameBlocks.get(index[xx][yy]).setZ(0);
                        gameBlocks.get(index[xx][yy]).value = 0;
                        index[xx][yy] = -1;
                        break;
                    } else if (index[xx][cy] != -1) break;
                    gameBlocks.get(index[xx][yy]).destY = cy * 250;
                    index[xx][cy] = index[xx][yy];
                    index[xx][yy] = -1;
                    yy--;
                }
            }
        }
        //Set merged as false


    }

    private void setDown() {
        int index[][] = new int[4][4];
        for (int i = 0; i < 4; i++) for (int j = 0; j < 4; j++) index[i][j] = -1;
        for (int i = 0; i < gameBlocks.size(); i++)
            index[(int) (gameBlocks.get(i).curX / 250)][(int) (gameBlocks.get(i).curY / 250)] = i;
        //Collision detection loop and set destination loop
        for (int xx = 0; xx < 4; xx++) {
            for (int yy = 2; yy >= 0; yy--) {
                if (index[xx][yy] == -1) continue;
                Log.d("LOOP", String.format("%d %d", xx, yy));
                int curValue = gameBlocks.get(index[xx][yy]).value;
                for (int cy = yy + 1; cy < 4; cy++) {
                    if (index[xx][cy] != -1 && gameBlocks.get(index[xx][cy]).value == curValue && gameBlocks.get(index[xx][cy]).merged == false) {
                        Log.d("MERGE", "IN");
                        gameBlocks.get(index[xx][cy]).merged = gameBlocks.get(index[xx][yy]).merged = true;
                        gameBlocks.get(index[xx][cy]).value = (gameBlocks.get(index[xx][cy]).value * 2);
                        gameBlocks.get(index[xx][yy]).destY = 750;
                        gameBlocks.get(index[xx][yy]).setZ(0);
                        gameBlocks.get(index[xx][yy]).value = 0;
                        index[xx][yy] = -1;
                        break;
                    } else if (index[xx][cy] != -1) break;
                    gameBlocks.get(index[xx][yy]).destY = cy * 250;
                    index[xx][cy] = index[xx][yy];
                    index[xx][yy] = -1;
                    yy++;
                }
            }
        }
    }

    private void setLeft() {
        int index[][] = new int[4][4];
        for (int i = 0; i < 4; i++) for (int j = 0; j < 4; j++) index[i][j] = -1;
        for (int i = 0; i < gameBlocks.size(); i++)
            index[(int) (gameBlocks.get(i).curX / 250)][(int) (gameBlocks.get(i).curY / 250)] = i;
        //Collision detection loop and set destination loop
        for (int yy = 0; yy < 4; yy++) {
            for (int xx = 1; xx < 4; xx++) {
                if (index[xx][yy] == -1) continue;
                Log.d("LOOP", String.format("%d %d", xx, yy));
                int curValue = gameBlocks.get(index[xx][yy]).value;
                for (int cx = xx - 1; cx >= 0; cx--) {
                    if (index[cx][yy] != -1 && gameBlocks.get(index[cx][yy]).value == curValue && !gameBlocks.get(index[cx][yy]).merged) {
                        Log.d("MERGE", "IN");
                        gameBlocks.get(index[cx][yy]).merged = gameBlocks.get(index[xx][yy]).merged = true;
                        gameBlocks.get(index[cx][yy]).value = (gameBlocks.get(index[cx][yy]).value * 2);
                        gameBlocks.get(index[xx][yy]).destX = 0;
                        gameBlocks.get(index[xx][yy]).setZ(0);
                        gameBlocks.get(index[xx][yy]).value = 0;
                        index[xx][yy] = -1;
                        break;
                    } else if (index[cx][yy] != -1) break;
                    gameBlocks.get(index[xx][yy]).destX = cx * 250;
                    index[cx][yy] = index[xx][yy];
                    index[xx][yy] = -1;
                    xx--;
                }
            }
        }
    }

    private void setRight() {
        int index[][] = new int[4][4];
        for (int i = 0; i < 4; i++) for (int j = 0; j < 4; j++) index[i][j] = -1;
        for (int i = 0; i < gameBlocks.size(); i++)
            index[(int) (gameBlocks.get(i).curX / 250)][(int) (gameBlocks.get(i).curY / 250)] = i;
        //Collision detection loop and set destination loop
        for (int yy = 0; yy < 4; yy++) {
            for (int xx = 2; xx >= 0; xx--) {
                if (index[xx][yy] == -1) continue;
                Log.d("LOOP", String.format("%d %d", xx, yy));
                int curValue = gameBlocks.get(index[xx][yy]).value;
                for (int cx = xx + 1; cx < 4; cx++) {
                    if (index[cx][yy] != -1 && gameBlocks.get(index[cx][yy]).value == curValue && !gameBlocks.get(index[cx][yy]).merged) {
                        Log.d("MERGE", "IN");
                        gameBlocks.get(index[cx][yy]).merged = gameBlocks.get(index[xx][yy]).merged = true;
                        gameBlocks.get(index[cx][yy]).value = (gameBlocks.get(index[cx][yy]).value * 2);
                        gameBlocks.get(index[xx][yy]).destX = 750;
                        gameBlocks.get(index[xx][yy]).setZ(0);
                        gameBlocks.get(index[xx][yy]).value = 0;
                        index[xx][yy] = -1;
                        break;
                    } else if (index[cx][yy] != -1) break;
                    gameBlocks.get(index[xx][yy]).destX = cx * 250;
                    index[cx][yy] = index[xx][yy];
                    index[xx][yy] = -1;
                    xx++;
                }
            }
        }
    }

    private void cleanUp() {
        for (int i = 0; i < gameBlocks.size(); i++) {
            gameBlocks.get(i).merged = false;
            if (gameBlocks.get(i).getZ() == 0) {
                ((ViewGroup) gameBlocks.get(i).getParent()).removeView(gameBlocks.get(i));
                gameBlocks.remove(i--);
            }
        }
    }

    private void generateNewBlock() {
        boolean taken[][] = new boolean[4][4];
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                taken[i][j] = false;
        for (int i = 0; i < gameBlocks.size(); i++) {
            int curX, curY;
            curX = (int) gameBlocks.get(i).curX;
            curY = (int) gameBlocks.get(i).curY;
            taken[curX / 250][curY / 250] = true;
        }
        int emptyBlock = 16 - gameBlocks.size();
        Random rand = new Random();
        int newBlockAt = rand.nextInt(emptyBlock);
        int setX = 3, setY = 3;
        boolean found = false;
        for (int i = 3; i >= 0; i--) {
            for (int j = 3; j >= 0; j--) {
                if (!taken[i][j]) {
                    newBlockAt--;
                }
                if(newBlockAt==0 && !taken[i][j]){
                    setX = i;
                    setY = j;
                }
            }
            if (found) break;
        }
        if (!found) Log.d("GAME", "LOST");
        //TODO implement end game behavior;
        //TODO Create abstract class if necessary;
        GameBlock gb = new GameBlock(gameBlocks.get(0).context, 2, setX * 250, setY * 250);
        gb.setZ(MainActivity.blockZ);
        MainActivity.loGameBoard.addView(gb);
        ViewGroup.LayoutParams layout = gb.getLayoutParams();
        layout.width = 250;
        layout.height = 250;
        gb.setX(gb.destX);
        gb.setY(gb.destY);
        gameBlocks.add(gb);
    }

    private void redraw(float desX, float desY) {
        block.setLocation(desX, desY);
    }
}
