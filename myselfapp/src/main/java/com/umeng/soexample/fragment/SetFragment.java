package com.umeng.soexample.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;

import com.android.core.StaticValue;
import com.android.core.base.AbsBaseActivity;
import com.android.core.base.AbsBaseFragment;
import com.android.core.widget.dialog.DialogManager;
import com.heaton.liulei.utils.utils.SPUtils;
import com.umeng.soexample.Constants;
import com.umeng.soexample.R;
import com.umeng.soexample.activity.SetPatternActivity;
import com.umeng.soexample.activity.SuggestActivity;
import com.umeng.soexample.manager.DataCleanManager;
import com.heaton.liulei.utils.utils.StringUtils;

import butterknife.Bind;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * @author: liulei
 * @date: 2016-10-24 09:06
 */
public class SetFragment extends AbsBaseFragment {

    @Bind(R.id.cache_size)
    TextView cache_size;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_setting;
    }


    @Override
    protected void onInitView() {
        long size = DataCleanManager.getPhotoCacheSize(getContext());
        cache_size.setText(StringUtils.long2Str(size));
    }

    @OnClick(R.id.rl_language)
    void language(){
//        ((AbsBaseActivity)getActivity()).showDialog(getActivity(), "提示", "这是一个很叼的弹出框", "确定", "取消", true,new DialogManager.DialogLisener() {
//            @Override
//            public void confirmLisener(SweetAlertDialog sweetAlertDialog) {
//                sweetAlertDialog.dismiss();
//                ToastUtil.showToast("点击了确定");
//            }
//
//            @Override
//            public void cancelLisener(SweetAlertDialog sweetAlertDialog) {
//                sweetAlertDialog.dismiss();
//                ToastUtil.showToast("点击了取消");
//            }
//        });
        showDialog();
    }

    public void showDialog(){
        new DialogManager.Builder(getActivity()).title("提示")
                .message("这是建造者模式的对话框,这是一个二次封装的对话框，可能稍微有点简单，将就着用吧！我也没办法")
                .confirmText("我同意")
                .cancelable(false)
                .build()
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                        com.android.core.control.ToastUtil.show("我同意了");
                    }
                })
                .show();
    }

    @OnClick(R.id.rl_app_update)
    void update(){
        ((AbsBaseActivity)getActivity()).showDialog("提示","这是第一个类型的弹出框","确定");
    }

    @OnClick(R.id.rl_clock)
    void clock(){
        ((AbsBaseActivity)getActivity()).showDialog("提示", "这是第一个类型的弹出框", "确定", "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }

    @OnClick(R.id.rl_about)
    void about(){
        ((AbsBaseActivity)getActivity()).showDialog("提示", "这是第一个类型的弹出框", "确定", "取消", "中间按钮", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }

    @OnClick(R.id.add_psw)
    void add_psw(){
        new AlertDialog.Builder(getActivity()).setTitle("温馨提示")
                .setMessage("当打开加密图案后，每次进入APP都要进行验证，是否继续")
                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startActivity(SetPatternActivity.class);
                    }
                }).show();
    }

    @OnClick(R.id.switch_theme)
    void switch_theme(){
        new AlertDialog.Builder(getActivity()).setTitle("选择主题")
                .setSingleChoiceItems(new String[]{"标准模式", "夜间模式"}, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //暂时注释
//                        if(which == 0){
//                            StaticValue.color = com.android.core.R.color.success_stroke_color;
//                            //主题色  白黑 夜间模式
//                            StaticValue.THEME_MODE = 0;
//                            getActivity().recreate();
//                        }else {
//                            StaticValue.color = R.color.background_material_dark;
//                            Log.e(TAG,"切换到了深色主题"+which);
//                            StaticValue.THEME_MODE = 1;
//                            getActivity().recreate();
//                        }
                    }
                }).setNegativeButton("取消",null).show();
    }

    @OnClick(R.id.clear_cache)
    void clear(){
        new AlertDialog.Builder(getActivity()).setTitle("提示")
                .setMessage("确定要删除缓存图片?")
                .setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        DataCleanManager.deletePhotoCache(getContext());
                        showProgress("清除中,请稍后...");
                        new Handler(Looper.getMainLooper()).postDelayed(()->hideProgress(),2000);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    @OnClick(R.id.rl_suggest)
    void suggest(){
        startActivity(SuggestActivity.class);
    }

    @OnClick(R.id.text_exit)
    void exit(){
        SPUtils.remove(getContext(), Constants.APP_PSW);
        SPUtils.remove(getContext(), Constants.APP_COUNT);
        SPUtils.remove(getContext(),StaticValue.IS_PSW_OPEN);
        getActivity().finish();
    }

}
