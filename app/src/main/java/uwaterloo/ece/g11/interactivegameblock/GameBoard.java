package uwaterloo.ece.g11.interactivegameblock;

import android.content.Context;
import android.widget.ImageView;

/**
 * Created by whcda on 6/30/2017.
 */

public class GameBoard extends android.support.v7.widget.AppCompatImageView {
    int sizeX, sizeY;
    GameBlock field[][];
    public GameBoard(Context context, int sizeX, int sizeY) {
        super(context);
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        field = new GameBlock[sizeX][sizeY];
    }
}
