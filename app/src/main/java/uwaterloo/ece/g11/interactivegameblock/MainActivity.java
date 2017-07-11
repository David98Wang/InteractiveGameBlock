package uwaterloo.ece.g11.interactivegameblock;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {
    public static RelativeLayout loGLobal, loGameBoard;

    public static Timer myGameLoop;
    ArrayList<GameBlock>gameBlocks;
    GameBlock ivGameBlock;
    static final int layoutHeight = 1000, layoutWidth = 1000;
    public SensorEventListener accelerometerEventListener;
    public static boolean up, down, left, right;
    public static int blockZ = 1;
    public static AlertDialog endGameDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        configureAccelerometerSensor();
        initializeVariables();

        endGameDialog = new AlertDialog.Builder(MainActivity.this).create();
        //loGLobal.setBackgroundResource(R.drawable.gameboard);
    }

    /**
     * Configure accelerometer listener
     */
    private void configureAccelerometerSensor() {
        SensorManager accelerometerSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor accelerometerSensor = accelerometerSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        accelerometerEventListener = new defaultSensorEventListener((TextView) findViewById(R.id.tvCurAccelerometerData), 100, 3, Sensor.TYPE_LINEAR_ACCELERATION, "Accelerometer");
        accelerometerSensorManager.registerListener(accelerometerEventListener, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME);
        ((defaultSensorEventListener) accelerometerEventListener).tv = (TextView) findViewById(R.id.tvDirection);
    }

    private void initializeVariables() {
        loGLobal = (RelativeLayout) findViewById(R.id.LOGlobal);
        gameBlocks = new ArrayList<>();

        //Initialize game board
        initializeGameBoard();
        //Initialize game block
        initializeGameBlock();

        addEventListener();

        //Initialize Timer;
        myGameLoop = new Timer();
        TimerTask myGameloopTask = new GameLoopTask(accelerometerEventListener, MainActivity.this, loGLobal, getApplicationContext(), gameBlocks);
        myGameLoop.schedule(myGameloopTask, 50, 16);
    }

    private void initializeGameBoard() {
        loGameBoard = (RelativeLayout) findViewById(R.id.loGameBoard);
        loGameBoard.setBackgroundResource(R.drawable.gameboard);
        loGameBoard.getLayoutParams().height = layoutHeight;
        loGameBoard.getLayoutParams().width = layoutWidth;
    }

    private void initializeGameBlock() {
        ivGameBlock = new GameBlock(this,2,0,0);
        ivGameBlock.setZ(blockZ);
        gameBlocks.add(ivGameBlock);

        GameBlock ivGameBlock1 = new GameBlock(this,2,250,0);
        ivGameBlock1.setZ(blockZ);
        gameBlocks.add(ivGameBlock1);

        GameBlock ivGameBlock2 = new GameBlock(this,4,500,0);
        ivGameBlock2.setZ(blockZ);
        gameBlocks.add(ivGameBlock2);

        GameBlock ivGameBlock3 = new GameBlock(this,8,750,0);
        ivGameBlock3.setZ(blockZ);
        gameBlocks.add(ivGameBlock3);

        for(int i=0;i<gameBlocks.size();i++){
            loGameBoard.addView(gameBlocks.get(i));
            ViewGroup.LayoutParams layout = gameBlocks.get(i).getLayoutParams();
            layout.width=250;
            layout.height=250;
            gameBlocks.get(i).setX(gameBlocks.get(i).destX);
            gameBlocks.get(i).setY(gameBlocks.get(i).destY);
        }
        //ViewGroup.LayoutParams layout = ivGameBlock.getLayoutParams();
        //layout.height = 250;
        //layout.width = 250;

//        Timer t= new Timer();
//        TimerTask tt = new TimerTask() {
//            @Override
//            public void run() {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        int a=(int)(Math.random() * 4);
//                        int b=(int)(Math.random() * 4);
//                        Log.d("DEBUG",String.format("%d %d",a,b));
//                        ivGameBlock.setLocation(a,b);
//                    }
//                });
//
//            }
//        };
//        t.schedule(tt,1000,1000);
        //Log.d("IMAGE",String.valueOf(w));
    }

    private void addEventListener() {
        up = down = left = right = false;
        findViewById(R.id.btnUP).setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                up = true;
            }
        });
        findViewById(R.id.btnDown).setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                down = true;
            }
        });
        findViewById(R.id.btnLeft).setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                left = true;
            }
        });
        findViewById(R.id.btnRight).setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                right = true;
            }
        });
    }
    public static void displayDialog(){

    }
}
