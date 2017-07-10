package uwaterloo.ece.g11.interactivegameblock;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by David Wang on 7/10/2017.
 */

public abstract class GameBlockTemplate extends RelativeLayout {
    int curX,curY,destX,destY;

    public GameBlockTemplate(Context context) {
        super(context);
    }

    public GameBlockTemplate(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GameBlockTemplate(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public GameBlockTemplate(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public abstract void setLocation(int curX,int curY);
}
