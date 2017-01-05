package com.umeng.soexample.activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.core.base.AbsBaseActivity;
import com.umeng.soexample.R;
import com.umeng.soexample.adapter.ResultAdapter;
import com.umeng.soexample.bean.UserVO;
import com.umeng.soexample.custom.SearchView;
import com.heaton.liulei.utils.utils.SPUtils;
import com.heaton.liulei.utils.utils.ToastUtil;

import org.litepal.crud.DataSupport;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by liulei on 2016/5/31.
 */
public class SQLActivity extends AbsBaseActivity implements SearchView.SearchViewListener {

    @Bind(R.id.search_view)
    SearchView searchView;
    @Bind(R.id.listView)
    ListView listView;

    List<UserVO>allDatas = new ArrayList<>();
    List<String>hotDatas = new ArrayList<>();
    /**
     * 自动补全列表adapter
     */
    private ArrayAdapter<String> autoCompleteAdapter;

    /**
     * 热搜框列表adapter
     */
    private ArrayAdapter<String> hintAdapter;

    /**
     * 结果adapter
     */
    private ResultAdapter resultAdapter;

    /**
     * 搜索结果的数据
     */
    private List<UserVO> resultData = new ArrayList<>();

    @Override
    protected int getLayoutResource() {
        isShowTool(false);
        return R.layout.activity_sql;
    }

    @Override
    protected void onInitView() {
        initData();
        searchView.setSearchViewListener(this);
//        searchView.setAutoCompleteAdapter();
        hintAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, hotDatas);
//        resultAdapter = new ResultAdapter(this,resultData,R.layout.sql_result_item);
        searchView.setTipsHintAdapter(hintAdapter);
        listView.setAdapter(resultAdapter);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        //添加数据到数据库中
//        insertDb();
        boolean isFirst = SPUtils.get(getBaseContext(),"db_",true);//判断是第一次进入，默认是   如果第一次进入则把本地联系人插入到数据库UserVO
        if(isFirst){
            requestPermission(new String[]{Manifest.permission.READ_CONTACTS}, "请求访问联系人权限", new GrantedResult() {
                @Override
                public void onResult(boolean granted) {
                    if(granted){
                        List<UserVO> localList = getLocalContactsInfos();
                        //把联系人全部插入到数据库
                        DataSupport.saveAll(localList);
                        SPUtils.put(getBaseContext(),"db_",false);
                    }else {
                        ToastUtil.showToast("权限拒绝");
                        return;
                    }
                }
            });
        }
        //从数据库获取数据
        getDbData();
        //初始化热搜榜数据
        getHotData();
        //初始化自动补全数据
        getAutoCompleteData(null);
        //初始化搜索结果数据
        getResultData(null);
    }

    private void getDbData() {
        allDatas = DataSupport.findAll(UserVO.class);
        Log.e("SQL----getDbData",allDatas.size()+"");
    }

    private void getResultData(String result) {
        resultData = DataSupport.where("username like ?","%"+result+"%").find(UserVO.class);
        if(resultAdapter == null){
            resultAdapter = new ResultAdapter(this,resultData,R.layout.sql_result_item);
        }
//        resultAdapter.notifyDataSetChanged();
        resultAdapter.refreshDatas(resultData);
        Log.e("SQL----getResultData",resultData.size()+"");
    }

    private void getAutoCompleteData(String text) {

    }

    private void getHotData() {
        hotDatas.add("王宝强&马蓉");
        hotDatas.add("易建联81分刷新纪录");
        hotDatas.add("深圳暴雨");
        hotDatas.add("客机失联");
    }

    /**
     * 当搜索框 文本改变时 触发的回调 ,更新自动补全数据
     * @param text
     */
    @Override
    public void onRefreshAutoComplete(String text) {
//更新数据
        getAutoCompleteData(text);
    }

    /**
     * 点击搜索键时edit text触发的回调
     *
     * @param text
     */
    @Override
    public void onSearch(String text) {
        //更新result数据
        if(text.equals("")){
            return;
        }
        getResultData(text);
//        listView.setVisibility(View.VISIBLE);
        //第一次获取结果 还未配置适配器
//        if (listView.getAdapter() == null) {
//            //获取搜索数据 设置适配器
//            listView.setAdapter(resultAdapter);
//        }
        //更新搜索数据
//        resultAdapter.notifyDataSetChanged();
    }

    // ----------------得到本地联系人信息-------------------------------------
    public List<UserVO> getLocalContactsInfos() {
        List<UserVO>localList = new ArrayList<>();
        ContentResolver cr = getContentResolver();
        String str[] = { ContactsContract.CommonDataKinds.Phone.CONTACT_ID, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.PHOTO_ID };
        Cursor cur = cr.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, str, null,
                null, null);

        if (cur != null) {
            while (cur.moveToNext()) {
                UserVO userVO = new UserVO();
                userVO.setPhonenum(cur.getString(cur
                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));// 得到手机号码
                userVO.setUsername(cur.getString(cur
                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
                // contactsInfo.setContactsPhotoId(cur.getLong(cur.getColumnIndex(Phone.PHOTO_ID)));
                long contactid = cur.getLong(cur
                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                long photoid = cur.getLong(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_ID));
                // 如果photoid 大于0 表示联系人有头像 ，如果没有给此人设置头像则给他一个默认的
                if (photoid > 0) {
                    Uri uri = ContentUris.withAppendedId(
                            ContactsContract.Contacts.CONTENT_URI, contactid);
                    InputStream input = ContactsContract.Contacts
                            .openContactPhotoInputStream(cr, uri);
                    userVO.setBitmap(BitmapFactory.decodeStream(input));
                } else {
                    userVO.setBitmap(BitmapFactory.decodeResource(
                            getResources(), R.drawable.ic_launcher));
                }

//                System.out.println("---------联系人电话--"
//                        + contactsInfo.getContactsPhone());
                localList.add(userVO);

            }
        }
        cur.close();
        return localList;

    }

}
