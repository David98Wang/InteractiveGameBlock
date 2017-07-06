package uwaterloo.ece.g11.interactivegameblock;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
    private boolean ended;

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
        if(ended)
            MainActivity.myGameLoop.cancel();

        activity.runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        //if (cycleSinceLastMove <= cycleWaitDuration)
                        //    return;
                        //Log.d("RUN",String.format("%d, %d",curX,curY));

                        for (int i = 0; i < gameBlocks.size(); i++) {
                            TextView tvValue = gameBlocks.get(i).tvValue;
                            switch (gameBlocks.get(i).value) {
                                case 2:
                                    tvValue.setTextColor(Color.YELLOW);
                                    break;
                                case 4:
                                    tvValue.setTextColor(Color.RED);
                                    break;
                                case 8:
                                    tvValue.setTextColor(Color.GREEN);
                                    break;
                                case 16:
                                    tvValue.setTextColor(Color.BLUE);
                                    break;
                                case 32:
                                    tvValue.setTextColor(Color.WHITE);
                                    break;
                                case 64:
                                    tvValue.setTextColor(Color.MAGENTA);
                                    break;
                                case 128:
                                    tvValue.setTextColor(Color.DKGRAY);
                                    break;
                                case 256:
                                    tvValue.setTextColor(Color.CYAN);
                                    break;
                                default:
                                    tvValue.setTextColor(Color.YELLOW);
                            }
                        }
                        notMoving = true;
                        for (int i = 0; i < gameBlocks.size(); i++)
                            if (gameBlocks.get(i).curY != gameBlocks.get(i).destY || gameBlocks.get(i).curX != gameBlocks.get(i).destX)
                                notMoving = false;
                        if (notMoving) {
                            blockVelocity = 25;
                            cleanUp();
                            //Log.d("BLOCK NOT", String.valueOf(gameBlocks.get(1).destY));
                            for (int i = 0; i < gameBlocks.size(); i++) {
                                if (gameBlocks.get(i).tvValue.getText() != String.valueOf(gameBlocks.get(i).value)) {
                                    TextView tv = gameBlocks.get(i).tvValue;

                                    gameBlocks.get(i).tvValue.setText(String.valueOf(gameBlocks.get(i).value));
                                }

                            }
                            if (justMoved) {
                                generateNewBlock();
                                justMoved = false;
                                return;
                            }
                            if (MainActivity.up) {
                                setUp();
                            } else if (MainActivity.down) {
                                setDown();
                            } else if (MainActivity.left) {
                                setLeft();
                            } else if (MainActivity.right) {
                                setRight();
                            }

                        } else {
                            //Log.d("BLOCK MOVING", String.valueOf(gameBlocks.get(1).destY));
                            for (int i = 0; i < gameBlocks.size(); i++) {
                                int blockCurX, blockCurY, blockDestX, blockDestY;
                                blockCurX = (int) gameBlocks.get(i).curX;
                                blockCurY = (int) gameBlocks.get(i).curY;
                                blockDestX = (int) gameBlocks.get(i).destX;
                                blockDestY = (int) gameBlocks.get(i).destY;
                                int velocity = Math.min(blockVelocity, Math.max(Math.abs(blockCurX - blockDestX), Math.abs(blockCurY - blockDestY)));
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
        int ini[][] = new int[4][4];
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++) {
                ini[i][j] = 0;
            }
        for (int i = 0; i < gameBlocks.size(); i++)
            ini[(int) gameBlocks.get(i).curX / 250][(int) gameBlocks.get(i).curY / 250] = gameBlocks.get(i).value;
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
        int fin[][] = new int[4][4];
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++) {
                fin[i][j] = 0;
            }
        for (int i = 0; i < gameBlocks.size(); i++)
            fin[(int) gameBlocks.get(i).destX / 250][(int) gameBlocks.get(i).destY / 250] = gameBlocks.get(i).value;
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++) {
                if (ini[i][j] != fin[i][j])
                    justMoved = true;
            }

    }

    private void setDown() {
        int ini[][] = new int[4][4];
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++) {
                ini[i][j] = 0;
            }
        for (int i = 0; i < gameBlocks.size(); i++)
            ini[(int) gameBlocks.get(i).curX / 250][(int) gameBlocks.get(i).curY / 250] = gameBlocks.get(i).value;
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
        int fin[][] = new int[4][4];
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++) {
                fin[i][j] = 0;
            }
        for (int i = 0; i < gameBlocks.size(); i++)
            fin[(int) gameBlocks.get(i).destX / 250][(int) gameBlocks.get(i).destY / 250] = gameBlocks.get(i).value;
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++) {
                if (ini[i][j] != fin[i][j])
                    justMoved = true;
            }
    }

    private void setLeft() {
        int ini[][] = new int[4][4];
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++) {
                ini[i][j] = 0;
            }
        for (int i = 0; i < gameBlocks.size(); i++)
            ini[(int) gameBlocks.get(i).curX / 250][(int) gameBlocks.get(i).curY / 250] = gameBlocks.get(i).value;
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
        int fin[][] = new int[4][4];
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++) {
                fin[i][j] = 0;
            }
        for (int i = 0; i < gameBlocks.size(); i++)
            fin[(int) gameBlocks.get(i).destX / 250][(int) gameBlocks.get(i).destY / 250] = gameBlocks.get(i).value;
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++) {
                if (ini[i][j] != fin[i][j])
                    justMoved = true;
            }
    }

    private void setRight() {
        int ini[][] = new int[4][4];
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++) {
                ini[i][j] = 0;
            }
        for (int i = 0; i < gameBlocks.size(); i++)
            ini[(int) gameBlocks.get(i).destX / 250][(int) gameBlocks.get(i).destY / 250] = gameBlocks.get(i).value;
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
        int fin[][] = new int[4][4];
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++) {
                fin[i][j] = 0;
            }
        for (int i = 0; i < gameBlocks.size(); i++)
            fin[(int) gameBlocks.get(i).destX / 250][(int) gameBlocks.get(i).destY / 250] = gameBlocks.get(i).value;
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++) {
                if (ini[i][j] != fin[i][j])
                    justMoved = true;
            }
    }

    private void cleanUp() {
        for (int i = 0; i < gameBlocks.size(); i++) {
            gameBlocks.get(i).merged = false;
            if (Math.abs(gameBlocks.get(i).getZ() - 0.0)<=0.0001) {
                ((ViewGroup) gameBlocks.get(i).getParent()).removeView(gameBlocks.get(i));
                gameBlocks.remove(i--);
            }
        }
    }

    private void generateNewBlock() {
        //TODO: Finish endgame behavior
        boolean taken[][] = new boolean[4][4];
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++)
                taken[i][j] = false;
        for (int i = 0; i < gameBlocks.size(); i++) {
            int destX, destY;
            destX =  gameBlocks.get(i).destX;
            destY =  gameBlocks.get(i).destY;
            taken[destX / 250][destY / 250] = true;
        }
        int emptyBlock = 16 - gameBlocks.size();
        int newBlockAt = -1;
        Random rand = new Random();
        if(emptyBlock != 0){
            newBlockAt = rand.nextInt(emptyBlock);
        }

        Log.d("newBlock",String.valueOf(newBlockAt));
        int setX = 3, setY = 3;
        boolean found = false;
        for (int i = 3; i >= 0; i--) {
            for (int j = 3; j >= 0; j--) {

                if (newBlockAt == 0 && !taken[i][j]) {
                    Log.d("newBlock",String.format("%d %d %d",i,j,emptyBlock));
                    setX = i;
                    setY = j;
                    newBlockAt --;
                }else if (!taken[i][j]) {
                    newBlockAt--;
                }
                if (!taken[i][j]) found = true;
            }

        }
        if (!found) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MainActivity.endGameDialog.setTitle("Game Ended");
                    MainActivity.endGameDialog.setMessage("You Lost. Restart?");
                    MainActivity.endGameDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    MainActivity.endGameDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Restart", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            restartGame();
                            dialog.dismiss();
                        }
                    });
                    MainActivity.endGameDialog.show();
                    ended  = true;
                }
            });

        }
        boolean win = false;
        for (int i = 0; i < gameBlocks.size(); i++) {
            if(gameBlocks.get(i).value == 256)
                win = true;
        }
        if(win){
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MainActivity.endGameDialog.setTitle("Game Ended");
                    MainActivity.endGameDialog.setMessage("You Won. Restart?");
                    MainActivity.endGameDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    MainActivity.endGameDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Restart", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            restartGame();
                            dialog.dismiss();
                        }
                    });
                    MainActivity.endGameDialog.show();
                    ended = true;
                }
            });
        }
        int num = rand.nextInt(8);
        int newNum = 2;
        if (num == 0) newNum = 4;
        //TODO implement end game behavior;
        //TODO Create abstract class if necessary;
        GameBlock gb = new GameBlock(gameBlocks.get(0).context, newNum, setX * 250, setY * 250);
        gb.setZ(MainActivity.blockZ);
        MainActivity.loGameBoard.addView(gb);
        ViewGroup.LayoutParams layout = gb.getLayoutParams();
        layout.width = 250;
        layout.height = 250;
        gb.setX(gb.destX);
        gb.setY(gb.destY);
        gameBlocks.add(gb);
    }

    public void restartGame() {

    }

    private void redraw(int desX, int desY) {
        block.setLocation(desX, desY);
    }
}
