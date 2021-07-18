package com.example.androidscript.Test;

import com.example.androidscript.util.BtnMaker;
import com.example.androidscript.Menu.MenuActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;

import com.example.androidscript.R;
import com.example.androidscript.util.AutoClick;
import com.example.androidscript.util.SaveImg;
import com.example.androidscript.util.ScreenShot;
//Test
public class TmpMenu extends AppCompatActivity {

    /*  Permission request code to draw over other apps  */
    private static final int DRAW_OVER_OTHER_APP_PERMISSION_REQUEST_CODE = 1222;
    private Button btnToMenu;
    private Button btnToTest;

    private MediaProjectionManager mpm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tmp_menu);

        btnToMenu = BtnMaker.jump(R.id.button_to_menu, this, MenuActivity.class);
        btnToTest = BtnMaker.jump(R.id.button_to_test, this, TestActivity.class);


//        if (!AutoClick.isStart()) {//Set Floating Accessibility
            try {
                this.startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            } catch (Exception e) {
                this.startActivity(new Intent(Settings.ACTION_SETTINGS));
                e.printStackTrace();
            }
//        }


        mpm = (MediaProjectionManager) this.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        startActivityForResult(this.mpm.createScreenCaptureIntent(), 123);//Set Screen Mirror
    }

    /*  start floating widget service  */
    public void createFloatingWidget(View view) {
        //Check if the application has draw over other apps permission or not?
        //This permission is by default available for API<23. But for API > 23
        //you have to ask for the permission in runtime.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            //If the draw over permission is not available open the settings screen
            //to grant the permission.
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, DRAW_OVER_OTHER_APP_PERMISSION_REQUEST_CODE);
        } else {
            //If permission is granted start floating widget service
            startFloatingWidgetService();
        }
    }

    /*  Start Floating widget service and finish current activity */
    private void startFloatingWidgetService() {
        startService(new Intent(TmpMenu.this, FloatingWidgetService.class));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 123) {
            ScreenShot.Instance(1000, 1000, new Point(0, 0), this.mpm.getMediaProjection(Activity.RESULT_OK, data));
            SaveImg.bitmap(ScreenShot.instance.Shot(), "Image");
            System.out.println("Alive\n");
        } else if (requestCode == DRAW_OVER_OTHER_APP_PERMISSION_REQUEST_CODE && resultCode == RESULT_OK) {
            startFloatingWidgetService();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
