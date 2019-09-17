package ir.theartofliving.honarezendegi;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;


public class G extends Application
{

    public static Typeface face;
    public static Typeface faceBold;
    public static Typeface faceLight;
    public static Typeface faceUltraLight;
    public static Context mContext;
    public static LayoutInflater mLayoutInflater;

    public static int heightPX = 0;
    public static int widthPX = 0;

    @Override
    public void onCreate()
    {
        super.onCreate();

        face = Typeface.createFromAsset(getAssets(),"fonts/font_normal.ttf");
        faceBold = Typeface.createFromAsset(getAssets(), "fonts/font_bold.ttf");
        faceLight = Typeface.createFromAsset(getAssets(), "fonts/font_light.ttf");
        faceUltraLight = Typeface.createFromAsset(getAssets(), "fonts/font_ultra_light.ttf");

        mContext = getApplicationContext();
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        setDisplay();
    }

    void setDisplay()
    {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        heightPX = dm.heightPixels;
        widthPX = dm.widthPixels;
    }
}
