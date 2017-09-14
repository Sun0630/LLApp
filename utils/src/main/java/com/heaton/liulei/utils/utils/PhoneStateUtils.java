package com.heaton.liulei.utils.utils;

import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

/**
 * 监听来电时工具类
 * 作者：刘磊 on 2015-12-16
 * 邮箱：186****6281@163.com
 */
public class PhoneStateUtils {
    public static final String CALL_RINGING = "call_ringing";
    public boolean ringing = false;
    public static MyPhoneStateListener mListener;
    public static TelephonyManager mManager;

    public static TelephonyManager getManager(){
        //获取电话服务
        if(mManager == null){
            mManager = (TelephonyManager) LLUtils.getmContext().getSystemService(LLUtils.getmContext().TELEPHONY_SERVICE);
        }
        return mManager;
    }

//    public static boolean getPhoneState(){
//
//    }
    public static MyPhoneStateListener getPhoneStateListener(){
        if(mListener == null){
            mListener = new MyPhoneStateListener();
        }
        return mListener;
    }
    static class MyPhoneStateListener extends PhoneStateListener {

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
//                    result += " 手机空闲起来了 ";
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    LLUtils.getmContext().sendBroadcast(new Intent(CALL_RINGING));
//                    result += " 手机铃声响了，来电号码:" + incomingNumber;
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
//                    result += " 电话被挂起了 ";
                default:
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }

    }


}
