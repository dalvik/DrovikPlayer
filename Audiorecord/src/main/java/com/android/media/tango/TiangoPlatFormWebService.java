/**   
 * Copyright © 2016 浙江大华. All rights reserved.
 * 
 * @title: TiangoPlatFormWebService.java
 * @description: TODO
 * @author: 23536   
 * @date: 2016年4月8日 下午5:02:50 
 */
package com.android.media.tango;

import com.android.library.net.base.IDataCallback;
import com.android.library.net.utils.JSONType;
import com.android.media.AbstractPlatForm;
import com.android.media.data.TiangoImageBaseData;
import com.android.media.data.TiangoThumbBaseData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/** 
 * @description: TODO
 * @author: 23536
 * @date: 2016年4月8日 下午5:02:50  
 */
public class TiangoPlatFormWebService extends AbstractPlatForm {

    private final static String URL_IMAGE_TYPE = "www.tngou.net/tnfs/api/classify";
    private final static String URL_IMAGE_THUMB = "http://www.tngou.net/tnfs/api/list";
    private final static String URL_IMAGE_LIST = "http://www.tngou.net/tnfs/api/show";
    
    public TiangoPlatFormWebService(IDataCallback callback) {
        super(callback);
    }
    

    @Override
    public int getTypeList(){
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("1", "");
            jsonObj.put("1", "");
            return doGetRequest(URL_IMAGE_THUMB, "", null, new JSONType<TiangoThumbBaseData<TiangoImageThumbListResp>>(){});
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    public List<TiangoImageType> getImageType(){
        List<TiangoImageType> list = new ArrayList<TiangoImageType>();
        TiangoImageType type = new TiangoImageType();
        type.description = "这是描述";
        type.id =  1;
        type.keywords = "关键字";
        type.name = "名称";
        type.seq = 1;
        type.title = "标题";
        list.add(type);
        return list;
    }

    @Override
    public int getThumbList(String json) {
        try {
            JSONObject param = new JSONObject(json);
            TiangoImageThumbListReq req = new TiangoImageThumbListReq();
            req.id = param.optInt("id");
            req.page = param.optInt("page");
            req.rows = param.optInt("rows");
            return doGetRequest(URL_IMAGE_THUMB, "", req, new JSONType<TiangoThumbBaseData<TiangoImageThumbListResp>>(){});
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public int getImageList(String json) {
        try {
            JSONObject param = new JSONObject(json);
            TiangoImageListReq req = new TiangoImageListReq();
            return doGetRequest(URL_IMAGE_LIST, "", req, new JSONType<TiangoImageBaseData<TiangoImageListResp>>(){});
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return -1;
    }

}
