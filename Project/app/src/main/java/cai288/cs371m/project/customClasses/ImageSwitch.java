package cai288.cs371m.project.customClasses;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageButton;

/**
 * Created by Cynthia on 11/8/2016.
 */

public class ImageSwitch extends ImageButton {

    private boolean state;

    public ImageSwitch(Context context){
        super(context);
    }

    public ImageSwitch(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public ImageSwitch (Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
    }

    public boolean isChecked(){
        return state;
    }

    public void setChecked(boolean state){
        this.state = state;
    }
}
