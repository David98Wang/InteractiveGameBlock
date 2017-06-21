package uwaterloo.ece.g11.interactivegameblock;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by David Wang on 5/21/2017.
 */

class defaultSensorEventListener implements SensorEventListener {


    private final static int GESTURE_LENGTH = 10;

    private final static int THRESHOLD_X = 23;
    private final static int THRESHOLD_Y = 25;

    public int currentDirection;
    public static final int UP = 1;
    public static final int DOWN = 2;
    public static final int LEFT = 3;
    public static final int RIGHT = 4;

    public TextView tv;

    private ArrayList<Double> curSensorValue = new ArrayList<Double>();

    private ArrayList<ArrayList<Double>> historyData = new ArrayList<ArrayList<Double>>();
    //private LineGraphView lineGraphView;

    private TextView curOutputView;
    private TextView maxOutputView;

    private String sensorName;

    private int sensorType;
    private int historyDataSize;
    private int numberOfValues;

    private int velocityX;
    private int velocityY;
    private int velocityZ;

    public ArrayList<Integer> xHist = new ArrayList<Integer>();
    public ArrayList<Integer> yHist = new ArrayList<Integer>();
    public ArrayList<Integer> zHist = new ArrayList<Integer>();


    DecimalFormat defaultFormat = new DecimalFormat("0.00");


    public double avgROC;

    public boolean newChange;

    public defaultSensorEventListener() {
        this.historyDataSize = 0;
        this.sensorName = "Light";
        this.numberOfValues = 1;
        this.sensorType = Sensor.TYPE_LIGHT;
        curSensorValue.add(.0);
    }

    /**
     * @param curOutputView   TextView for CURRENT data.
     * @param historyDataSize How many historical data to store
     * @param numberOfValue   Number of values this sensor has.
     * @param sensorType      Type of Sensor
     * @param sensorName      Name of Sensor
     */
    protected defaultSensorEventListener(TextView curOutputView, int historyDataSize, int numberOfValue, int sensorType, String sensorName) {
        this.curOutputView = curOutputView;
        this.maxOutputView = maxOutputView;
        this.historyDataSize = historyDataSize;
        this.sensorName = sensorName;
        this.numberOfValues = numberOfValue;
        this.sensorType = sensorType;
        for (int i = 0; i < numberOfValue; i++) {
            curSensorValue.add(.0);
        }
        ArrayList<Double> temp = new ArrayList<Double>();
        ArrayList<Double> temp1 = new ArrayList<Double>();
        ArrayList<Double> temp2 = new ArrayList<Double>();
        for (int i = 0; i < this.historyDataSize; i++) {
            temp.add(.0);
            temp1.add(.0);
            temp2.add(.0);
        }
        historyData.add(temp);
        historyData.add(temp1);
        historyData.add(temp2);
        setOutputText("Current " + sensorName + " Reading: ", curOutputView, curSensorValue);
        //setOutputText("Record-High " + sensorName + " Reading: ", maxOutputView, maxSensorValue);
        velocityX = velocityY = velocityZ = 0;

        //Log.d("DEBUG",sensorName);
    }


    /**
     * Set TextView with Title and data provided
     *
     * @param titleText Title of data
     * @param tv        TextView to fill data in
     * @param values    Value(s) of data to fill in
     */
    private void setOutputText(String titleText, TextView tv, ArrayList values) {
        String setAs = titleText + "\n(";
        for (int i = 0; i < numberOfValues; i++) {
            double temp = ((Double) values.get(i));
            setAs += defaultFormat.format(temp);
            if (i != numberOfValues - 1)
                setAs += ", ";
        }
        setAs += ")";
        tv.setText(setAs);
    }


    private void updateValues(SensorEvent curSensorEvent) {
        for (int i = 0; i < numberOfValues; i++) {
            if (i + 1 >= curSensorValue.size())
                curSensorValue.add((double) curSensorEvent.values[i]);
            else
                curSensorValue.set(i, (double) curSensorEvent.values[i]);
        }
    }

    private void appendHistoryData() {
        //Log.d("DEBUG",sensorName+String.valueOf(historyData.size()));
        for (int i = 0; i < numberOfValues; i++) {
            historyData.get(i).add(curSensorValue.get(i));
            if (historyData.get(i).size() >= historyDataSize)
                historyData.get(i).remove(0);
        }
    }

    public void outputToFile(File externalFileDirectory) {
        File file = new File(externalFileDirectory, sensorName + "Data.csv");
        try {
            PrintWriter out = new PrintWriter(file);
            for (int i = 0; i < 100; i++) {
                for (int j = 0; j < numberOfValues; j++) {
                    out.print(String.format("%.2f", historyData.get(j).get(i)));
                    if (j + 1 != numberOfValues)
                        out.print(", ");
                }
                out.print("\n");
            }
            Log.i("FILE_IO", "Saved to " + file.getAbsolutePath());
            out.close();
        } catch (FileNotFoundException e) {
            Log.wtf("FATAL", "Literally just created the directory");
        }
    }

    public ArrayList<ArrayList<Double>> getHistoryData() {
        return historyData;
    }

    public void onSensorChanged(SensorEvent curSensorEvent) {
        if (curSensorEvent.sensor.getType() == sensorType) {
            updateValues(curSensorEvent);
            setOutputText("Current " + sensorName + " Reading: ", curOutputView, curSensorValue);

            //Log.d("DEBUG",this.sensorName+String.valueOf(curSensorEvent.values[0]));
            appendHistoryData();

            velocityX += curSensorEvent.values[0];
            velocityY += curSensorEvent.values[1];
            velocityZ += curSensorEvent.values[2];

            xHist.add(velocityX);
            yHist.add(velocityY);
            zHist.add(velocityZ);
            if (xHist.size() >= 100) {
                xHist.remove(0);
                yHist.remove(0);
                zHist.remove(0);
            }
            if (sensorType == Sensor.TYPE_LINEAR_ACCELERATION)
                detectDirection();
        }
    }

    private void detectDirection() {

        double baseLineX = 0, baseLineY = 0;
        //Calculates past velocity
        for (int i = xHist.size() - 1; i >= 0 && i >= xHist.size() - GESTURE_LENGTH * 2; i--) {
            baseLineX += xHist.get(i);
            baseLineY += yHist.get(i);
        }
        baseLineX /= (GESTURE_LENGTH * 2.0);
        baseLineY /= (GESTURE_LENGTH * 2.0);
        //Calculates the baseline velocity
        baseLineX = (xHist.get(xHist.size() - 1));
        baseLineY = (yHist.get(yHist.size() - 1));
        //Maintian history value
        if (xHist.size() > 10) {
            baseLineX += xHist.get(xHist.size() - 11);
            baseLineY += yHist.get(yHist.size() - 11);
            baseLineX /= 2.0;
            baseLineY /= 2.0;
        }

        boolean left, right, up, down; //Boolean variables for direction


        if (tv == null) return;
        int avgX = 0, avgY = 0;
        for (int i = xHist.size() - 1; i >= 0 && i >= xHist.size() - GESTURE_LENGTH; i--) {
            //Log.d("DEBUG",String.valueOf(i));
            avgX += xHist.get(i);
            avgY += yHist.get(i);
        }
        //Decide Direction by comparing to threshold value
        avgX /= GESTURE_LENGTH;
        avgY /= GESTURE_LENGTH;
        left = (avgX - baseLineX < -1 * THRESHOLD_X);
        right = (avgX - baseLineX > THRESHOLD_X);
        up = (avgY - baseLineY > THRESHOLD_Y);
        down = (avgY - baseLineY < -1 * THRESHOLD_Y);
        //Output Direction
        if (left) {
            Log.i("LOG", "left");
            //ret+="Left";
            tv.setText("LEFT");
            currentDirection = LEFT;
        } else if (right) {
            tv.setText("RIGHT");
            currentDirection = RIGHT;
        }
        if (up) {
            tv.setText("UP");
            currentDirection = UP;
        } else if (down) {
            tv.setText("DOWN");
            currentDirection = DOWN;
        }
//        double minX = 1000000, maxX = -1000000, minY = 1000000, maxY = -1000000;
//        for (int i = historyData.size() - 1; i >= 0 && i >= historyData.size() - GESTURE_LENGTH * 2; i--) {
//            minX = Math.min(minX, historyData.get(0).get(i));
//            maxX = Math.max(maxX, historyData.get(0).get(i));
//            minY = Math.min(minY, historyData.get(1).get(i));
//            maxY = Math.max(maxY, historyData.get(1).get(i));
//        }
//        Log.d("reset: ",String.format("%f %f",maxY-minY,maxX-minX));
//        //Log.d("reset: ", String.format("%f %f %f %f", minX, maxX, minY, maxY));
//        if (maxX - minX < 0.1 && velocityX != 0) {
//            for (int i = xHist.size() - 1; i > 0 && i >= xHist.size() - GESTURE_LENGTH * 2; i--)
//                xHist.set(i, 1);
//            velocityX = 0;
//            Log.d("reset", "x");
//        }
//        if (Math.abs(maxY - minY) < 0.1 && velocityY != 0) {
//            for (int i = xHist.size() - 1; i > 0 && i >= xHist.size() - GESTURE_LENGTH * 2; i--)
//                yHist.set(i, 1);
//            velocityY = 0;
//            Log.d("reset", "y");
//        }
    }

    /*private void detectDirection(){
        if(MainActivity.LOCK)
            return;
        double dx,dy,dz;
        if(debugCnt++%10==0)
        Log.d("DEBUG",velocityX+" "+velocityY+" "+velocityZ);
        boolean left,right,up,down,forward,backward;
        left=(velocityX<-1*THRESHOLD_X);
        right=(velocityX>THRESHOLD_X);
        up=(velocityY>THRESHOLD_Y);
        down=(velocityY<-1*THRESHOLD_Y);
        forward=(velocityZ>THRESHOLD_Z);
        backward=(velocityZ<-1*THRESHOLD_Z);
        if(tv==null)return;
        if ((up || down) &&(left||right)) {
            if(Math.abs(velocityX)>Math.abs(velocityY))
                up=down=false;
            else
                left=right=false;
        }
        if(left){
            Log.i("LOG","left");
            //ret+="Left";
            tv.setText("Left");
        }
        else if(right){
            Log.i("LOG","Right");
            //ret+="right";
            tv.setText("Right");
        }
        if(up){
            //tv.setText("up");
            tv.setText("up");
        }
        else if(down){
            tv.setText("down");
            //tv.setText("down");
        }
    }*/
    public void onAccuracyChanged(Sensor s, int i) {
    }
}
