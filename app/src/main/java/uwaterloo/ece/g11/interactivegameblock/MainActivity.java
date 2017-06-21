package uwaterloo.ece.g11.interactivegameblock;

import android.app.Activity;
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

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {
    RelativeLayout loGLobal, loGameBoard;
    GameBlock ivGameBlock;
    static final int layoutHeight = 1000, layoutWidth = 1000;
    static final float baseX = -45f, baseY = -45f;
    public SensorEventListener accelerometerEventListener;
    public static boolean up, down, left, right;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        configureAccelerometerSensor();
        initializeVariables();


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


        //Initialize game board
        initializeGameBoard();
        //Initialize game block
        initializeGameBlock();

        addEventListener();

        //Initialize Timer;
        Timer myGameLoop = new Timer();
        TimerTask myGameloopTask = new GameLoopTask(accelerometerEventListener, this, loGLobal, getApplicationContext(), ivGameBlock);
        myGameLoop.schedule(myGameloopTask, 50, 50);
    }

    private void initializeGameBoard() {
        loGameBoard = (RelativeLayout) findViewById(R.id.loGameBoard);
        loGameBoard.setBackgroundResource(R.drawable.gameboard);
        loGameBoard.getLayoutParams().height = layoutHeight;
        loGameBoard.getLayoutParams().width = layoutWidth;
    }

    private void initializeGameBlock() {
        ivGameBlock = new GameBlock(this, 0,0);
        loGameBoard.addView(ivGameBlock);


        ViewGroup.LayoutParams layout = ivGameBlock.getLayoutParams();
        layout.height = 250;
        layout.width = 250;

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
}
