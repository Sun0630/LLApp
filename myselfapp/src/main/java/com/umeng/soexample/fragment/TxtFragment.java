package com.umeng.soexample.fragment;

import android.widget.TextView;

import com.android.core.base.AbsBaseFragment;
import com.umeng.soexample.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import butterknife.Bind;

/**
 * 文章类别 Fragment
 * @author: liulei
 * @date: 2016-10-23 14:38
 */
public class TxtFragment extends AbsBaseFragment{

    @Bind(R.id.txt)
    TextView mTxt;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_txt;
    }

    @Override
    protected void onInitView() {
        try {
            InputStream is = mContext.getAssets().open("app.txt");
            int size = is.available();

            // Read the entire asset into a local byte buffer.
//            byte[] buffer = new byte[size];
//            is.read(buffer);
//            is.close();
            StringBuffer buffer = new StringBuffer();

            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"));
            while (reader.read()>-1){
                buffer.append(reader.readLine());
                buffer.append("\n");
            }
            // Convert the buffer into a string.
//            String text = new String(buffer, "GB2312");
            mTxt.setText(buffer.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
