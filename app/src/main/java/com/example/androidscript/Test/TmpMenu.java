package com.example.androidscript.Test;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Bundle;
//import android.provider.Settings;
//import android.view.View;
//import android.widget.Button;
//import android.widget.Toast;
//
//import com.example.androidscript.R;
//
import com.example.androidscript.util.BtnMaker;
import com.example.androidscript.util.AutoClick;
import com.example.androidscript.Menu.MenuActivity;
//public class TmpMenu extends AppCompatActivity {
//    private Button btnToMenu;
//    private Button btnToFloat;
//    private static final int DRAW_OVER_OTHER_APP_PERMISSION_REQUEST_CODE = 1222;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_tmp_menu);
//        btnToMenu = BtnMaker.jump(R.id.button_to_menu,this,MenuActivity.class);
//        btnToFloat = BtnMaker.jump(R.id.button_to_menu,this,FloatingWidgetService.class);
//    }
//
//
//    public void createFloatingWidget(View view) {
//        //Check if the application has draw over other apps permission or not?
//        //This permission is by default available for API<23. But for API > 23
//        //you have to ask for the permission in runtime.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
//            //If the draw over permission is not available open the settings screen
//            //to grant the permission.
//            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
//                    Uri.parse("package:" + getPackageName()));
//            startActivityForResult(intent, DRAW_OVER_OTHER_APP_PERMISSION_REQUEST_CODE);
//        } else
//            //If permission is granted start floating widget service
//            startFloatingWidgetService();
//
//    }
//
//    /*  Start Floating widget service and finish current activity */
//    private void startFloatingWidgetService() {
//        startService(new Intent(TmpMenu.this, FloatingWidgetService.class));
//        finish();
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == DRAW_OVER_OTHER_APP_PERMISSION_REQUEST_CODE) {
//            //Check if the permission is granted or not.
//            if (resultCode == RESULT_OK)
//                //If permission granted start floating widget service
//                startFloatingWidgetService();
//        } else {
//            super.onActivityResult(requestCode, resultCode, data);
//        }
//    }
//}
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.androidscript.R;

public class TmpMenu extends AppCompatActivity {

    /*  Permission request code to draw over other apps  */
    private static final int DRAW_OVER_OTHER_APP_PERMISSION_REQUEST_CODE = 1222;
    private Button btnToMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tmp_menu);


        btnToMenu = BtnMaker.jump(R.id.button_to_menu,this,MenuActivity.class);

        if (!SimulatedClickService.isStart()) {
            try {
                this.startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            } catch (Exception e) {
                this.startActivity(new Intent(Settings.ACTION_SETTINGS));
                e.printStackTrace();
            }
        }
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
        if (requestCode == DRAW_OVER_OTHER_APP_PERMISSION_REQUEST_CODE) {
            //Check if the permission is granted or not.
            if (resultCode == RESULT_OK)
                //If permission granted start floating widget service
                startFloatingWidgetService();

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
