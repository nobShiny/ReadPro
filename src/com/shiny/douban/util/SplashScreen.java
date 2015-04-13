package com.shiny.douban.util;

import com.shiny.douban.R;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;


/**
 * ����ҳͼƬ��ͣ�������룬Ȼ���Զ���ʧ��
 * 
 */
public class SplashScreen {
    
    public final static int SLIDE_LEFT = 1;
    public final static int SLIDE_UP = 2;
    public final static int FADE_OUT = 3;
    
    private Dialog splashDialog;
    
    private Activity activity;
    
    public SplashScreen(Activity activity){
        this.activity = activity;
    }
    
    /**
     * ��ʾ��
     * @param imageResource ͼƬ��Դ
     * @param millis ͣ��ʱ�䣬�Ժ���Ϊ��λ��
     * @param animation ��ʧʱ�Ķ���Ч����ȡֵ�����ǣ�SplashScreen.SLIDE_LEFT, SplashScreen.SLIDE_UP, SplashScreen.FADE
     */
    public void show(final int imageResource, final int animation){
        Runnable runnable = new Runnable() {
            public void run() {
                // Get reference to display
            	DisplayMetrics metrics = new DisplayMetrics();
//                Display display = activity.getWindowManager().getDefaultDisplay();

                // Create the layout for the dialog
                LinearLayout root = new LinearLayout(activity);
                root.setMinimumHeight(metrics.heightPixels);
                root.setMinimumWidth(metrics.widthPixels);
                root.setOrientation(LinearLayout.VERTICAL);
                root.setBackgroundColor(Color.BLACK);
                root.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT, 0.0F));
                root.setBackgroundResource(imageResource);

                // Create and show the dialog
                splashDialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
                // check to see if the splash screen should be full screen
                if ((activity.getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN)
                        == WindowManager.LayoutParams.FLAG_FULLSCREEN) {
                    splashDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                            WindowManager.LayoutParams.FLAG_FULLSCREEN);
                }
                
                Window window = splashDialog.getWindow();
                switch(animation){
                    case SLIDE_LEFT:
                        window.setWindowAnimations(R.style.dialog_anim_slide_left);
                        break;
                    case SLIDE_UP:
                        window.setWindowAnimations(R.style.dialog_anim_slide_up);
                        break;
                    case FADE_OUT:
                        window.setWindowAnimations(R.style.dialog_anim_fade_out);
                        break;
                }
            
                splashDialog.setContentView(root);
                splashDialog.setCancelable(false);
                splashDialog.show();

                // Set Runnable to remove splash screen just in case
                /*final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        removeSplashScreen();
                    }
                }, millis);*/
            }
        };
        activity.runOnUiThread(runnable);
    }
    
    public void removeSplashScreen(){
        if (splashDialog != null && splashDialog.isShowing()) {
            splashDialog.dismiss();
            splashDialog = null;
        }
    }

}
