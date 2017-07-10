package uwaterloo.ece.g11.interactivegameblock;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by whcda on 6/19/2017.
 */

public class GameBlock extends GameBlockTemplate {
    public static final int CELL_COLOR[] = {Color.YELLOW, Color.RED, Color.BLUE, Color.GREEN, Color.CYAN, Color.GRAY};


    public int value;
    public TextView tvValue;
   public  Color color;
    Context context;
    public boolean merged = false;
    int curX, curY, destX, destY;
    public static final float blockLength = 250f;
    public static final double IMAGE_STAGE = 1.0d;
    Timer t;
    TimerTask tt;
    public GameBlock(Context context, final int value, int coordX, int coordY) {
        super(context);
        this.context = context;
        this.setBackgroundResource(R.drawable.gameblock);

        this.value = value;
        this.curX = this.destX = coordX;
        this.curY = this.destY = coordY;

        //initialize tvValue to position of GameCell;
        tvValue = new TextView(context);
        this.addView(tvValue);
        tvValue.setTextSize(50);
        tvValue.setTextColor(Color.YELLOW);
        tvValue.setGravity(Gravity.CENTER);
        tvValue.setHeight(250);
        tvValue.setWidth(250);
        tvValue.setText(String.valueOf(value));
        tvValue.setX(0);
        tvValue.setY(0);

        t = new Timer();
        tt = new TimerTask() {
            @Override
            public void run() {

            }
        };
        t.schedule(tt,0,10);
    }

    public GameBlock(Context context) {
        super(context);
    }

    public GameBlock(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public GameBlock(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public GameBlock(Context context, int coordX, int coordY) {
        super(context);
        this.setBackgroundResource(R.drawable.gameblock);
        setX(coordX * blockLength);
        setY(coordY * blockLength);
        this.curX = this.destX = coordX;
        this.curX = this.destX = coordY;
    }

    public void setLocation(int  curX, int curY) {
        this.curX = curX;
        this.curY = curY;
        setX(curX);
        setY(curY);

    }

    public void setDestination(int destX, int destY) {
        this.destX = destX;
        this.destY = destY;
    }

    public void moveY(int velocity) {
        this.curY += velocity;
        setY(curY);
    }

    public void moveX(int velocity) {
        this.curX += velocity;
        setX(curX);
    }

    public void setValue(int value) {
        this.value = value;
        tvValue.setText(String.valueOf(value));
    }
}
