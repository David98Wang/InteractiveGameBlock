package uwaterloo.ece.g11.interactivegameblock;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by whcda on 6/19/2017.
 */

public class GameBlock extends android.support.v7.widget.AppCompatImageView {
    int curX,curY,destX,destY;
    public static final float blockLength = 250f;
    public static final double IMAGE_STAGE = 1.0d;

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
        this.setImageResource(R.drawable.gameblock);
        setX(coordX * blockLength);
        setY(coordY * blockLength);
        this.curX = this.destX = coordX;
        this.curX = this.destX = coordY;
    }

    public void setLocation(int curX, int curY) {
        this.curX = curX;
        this.curY = curY;
        setX(curX);
        setY(curY);
    }
    public void setDestination(int destX,int destY){
        this.destX = destX;
        this.destY = destY;
    }

}
