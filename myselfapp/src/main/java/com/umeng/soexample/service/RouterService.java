package com.umeng.soexample.service;

import com.baronzhang.android.router.annotation.router.CombinationUri;
import com.baronzhang.android.router.annotation.router.IntentExtrasParam;
import com.baronzhang.android.router.annotation.router.UriParam;

/**
 * Created by LiuLei on 2017/7/18.
 */

public interface RouterService {

    @CombinationUri(scheme = "router",host = "com.umeng.soexample.custom.ToShare")
    void startShareActivity(@UriParam("preActivity")String preActivity, @IntentExtrasParam("param")String stringParam);

}
