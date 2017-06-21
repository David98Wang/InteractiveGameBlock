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


    public static final int UP = 1;
    public static final int DOWN = 2;
    public static final int LEFT = 3;
    public static final int RIGHT = 4;
    private final static int GESTURE_LENGTH = 10;
    private final static int THRESHOLD_X = 23;
    private final static int THRESHOLD_Y = 25;
    public int currentDirection;
    public TextView tv;
    public ArrayList<Integer> xHist = new ArrayList<Integer>();
    public ArrayList<Integer> yHist = new ArrayList<Integer>();
    //private LineGraphView lineGraphView;
    public ArrayList<Integer> zHist = new ArrayList<Integer>();
    public double avgROC;
    public boolean newChange;
    DecimalFormat defaultFormat = new DecimalFormat("0.00");
    private ArrayList<Double> curSensorValue = new ArrayList<Double>();
    private ArrayList<ArrayList<Double>> historyData = new ArrayList<ArrayList<Double>>();
    private TextView curOutputView;
    private TextView maxOutputView;
    private String sensorName;
    private int sensorType;
    private int historyDataSize;
    private int numberOfValues;
    private int velocityX;
    private int velocityY;
    private int velocityZ;

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

        //Calculates the baseline velocity
        baseLineX = (xHist.get(xHist.size() - 1));
        baseLineY = (yHist.get(yHist.size() - 1));
        //Maintain history value
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
            MainActivity.left = true;
        } else if (right) {
            tv.setText("RIGHT");
            currentDirection = RIGHT;
            MainActivity.right = true;
        }
        if (up) {
            tv.setText("UP");
            currentDirection = UP;
            MainActivity.up = true;
        } else if (down) {
            tv.setText("DOWN");
            currentDirection = DOWN;
            MainActivity.down = true;
        }
        if (up || down || left || right) {
            for (int i = 0; i < xHist.size(); i++) {
                xHist.set(i, 0);
                yHist.set(i, 0);
            }
        }
    }

    public void onAccuracyChanged(Sensor s, int i) {
    }
}
