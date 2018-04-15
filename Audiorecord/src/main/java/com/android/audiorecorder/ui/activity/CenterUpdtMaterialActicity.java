package com.android.audiorecorder.ui.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.audiorecorder.R;
import com.android.audiorecorder.ui.data.resp.UserResp;
import com.android.audiorecorder.ui.manager.UserDao;
import com.android.audiorecorder.ui.manager.UserManager;
import com.android.audiorecorder.utils.ActivityUtil;
import com.android.audiorecorder.utils.DateUtil;
import com.android.library.BaseApplication;
import com.android.library.ui.activity.BaseCompatActivity;
import com.android.library.ui.camera.cropper.scrop.Crop;
import com.android.library.ui.dialog.ImageDialog;
import com.android.library.ui.manager.BitmapMgr;
import com.android.library.ui.utils.ToastUtils;
import com.android.library.utils.FileUtil;
import com.android.library.utils.TextUtils;

import java.io.File;
import java.util.Date;

//import android.os.PersistableBundle;

/**
 * 
 * @description: 更新用户实体信息
 * @author: 23536
 * @date: 2016年5月26日 上午10:57:21
 */
public class CenterUpdtMaterialActicity extends BaseCompatActivity implements View.OnClickListener {
    /*用来标识请求照相功能的activity*/
    private static final int PHOTO_CAMERA = 3023;
    /*用来标识请求gallery的activity*/
    private static final int PHOTO_GALLERY = PHOTO_CAMERA + 1;
    /* 裁剪*/
    private static final int PHOTO_CROP = PHOTO_GALLERY + 1;
    /**
     * 请客号
     */
    private static final int REQUEST_NUMBER_CODE = 1;
    /**
     * 昵称
     */
    private static final int REQUEST_NICKNAME_CODE = REQUEST_NUMBER_CODE + 1;
    /**
     * 生日
     */
    private static final int REQUEST_BIRTHDAYS_CODE = REQUEST_NICKNAME_CODE + 1;
    /**
     * 居住地
     */
    private static final int REQUEST_ADDRESS_CODE = REQUEST_BIRTHDAYS_CODE + 1;
    /**
     * 兴趣
     */
    private static final int REQUEST_INTEREST_CODE = REQUEST_ADDRESS_CODE + 1;
    /**
     * 学校
     */
    private static final int REQUEST_SCHOOL_CODE = REQUEST_INTEREST_CODE + 1;
    /**
     * 公司
     */
    private static final int REQUEST_COMPANY_CODE = REQUEST_SCHOOL_CODE + 1;
    /**
     * 行业
     */
    private static final int REQUEST_INDUSTRY_CODE = REQUEST_COMPANY_CODE + 1;
    /**
     * 技能
     */
    private static final int REQUEST_SKILL_CODE = REQUEST_INDUSTRY_CODE + 1;
    /*拍照的照片存储位置*/
    private static final File PHOTO_DIR = new File(FileUtil.CFG_PATH_SDCARD_IMAGE);
    //需要传的值
    private final static String BUNDLE_REQ = "req";
    private final static String BUNDLE_ICON = "icon";
    private File photoFile;//照相机拍照得到的图片
   //private UserResp userResp = AppApplication.getUser();
    //底部window
    private ImageView iconTv;
    private ImageView center_account;
    private RelativeLayout numberRl;
    private RelativeLayout nicknameRl;
    private RelativeLayout birthdayRl;
    private RelativeLayout addressRl;
    private RelativeLayout interestRl;
    private RelativeLayout skillRl;
    private RelativeLayout schoolRl;
    private RelativeLayout industryRl;
    private RelativeLayout companyRl;
    private TextView numberTv;
    private TextView nicknameTv;
    private TextView sexTv;
    private TextView birthdayTv;
    private TextView addressTv;
    private TextView interestTv;
    private TextView skillTv;
    private TextView schoolTv;
    private TextView industryTv;
    private TextView companyTv;
    private int changematerial;
    private int changeHeader;
    //private UserReq userReq = new UserReq();
    private boolean isChange;
    private String headerIcon;
    private ImageDialog imageDialog;

    private UserManager mUserManager;
    private UserResp userResp;
    private UserDao mUserDao;
    private String TAG = "CenterUpdtMaterialActicity";
    
    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {

        public void onClick(View v) {
            //cameraWindow.dismiss();
            int i = v.getId();
            if (i == R.id.photoBtn) {
                doPickPhotoFromGallery();// 从相册中去获取

                //相机
            } else if (i == R.id.shootBtn) {/*String status = Environment.getExternalStorageState();
                    if (status.equals(Environment.MEDIA_MOUNTED)) {//判断是否有SD卡
                        doTakePhoto();// 用户点击了从照相机获取
                    } else {
                        Toast.makeText(activity, R.string.center_nosdcard, Toast.LENGTH_SHORT).show();
                    }*/

            } else {
            }
        }

    };

    public static Intent getTakePickIntent(File file) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        return intent;
    }

    // 封装请求Gallery的intent
    public static Intent getPhotoPickIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
        intent.setType("image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 500);
        intent.putExtra("outputY", 500);
        intent.putExtra("return-data", true);
        return intent;
    }

    /**
     * Constructs an intent for image cropping. 调用图片剪辑程序
     */
    public static Intent getCropImageIntent(Uri photoUri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(photoUri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 500);
        intent.putExtra("outputY", 500);
        intent.putExtra("return-data", true);
        return intent;
    }

    /*@Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        if (outState != null) {
            //outState.putSerializable(BUNDLE_REQ, userReq);
            outState.putString(BUNDLE_ICON, headerIcon);
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
        if (savedInstanceState != null) {
            headerIcon = savedInstanceState.getString(BUNDLE_ICON);
            //userReq = (UserReq) savedInstanceState.getSerializable(BUNDLE_REQ);
        }
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.center_updata_material);
        setTitle(R.string.center_updata);
        mUserManager = new UserManager(this);
        mUserDao = new UserDao();
        initUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(headerIcon != null && headerIcon.length()>0){
            BitmapMgr.loadBitmap(this, iconTv, UserManager.DOMAIN + userResp.headIcon);
        } else {
            iconTv.setImageResource(userResp.sex == UserManager.SEX_FEMALE ? R.drawable.female_icon1:R.drawable.male_icon1);
        }
    }

    private void initUI() {
        userResp = mUserDao.getUser(this);
        headerIcon = userResp.headIcon;
        //头像
        iconTv = (ImageView) findViewById(R.id.headerIconIv);
        //相机
        center_account = (ImageView) findViewById(R.id.cameraIv);
        center_account.setOnClickListener(this);
        //修改请客号
        numberRl = (RelativeLayout) findViewById(R.id.numberRl);
        numberTv = (TextView) findViewById(R.id.numberTv);
        //numberTv.setText(userResp.treamNum);
        numberRl.setOnClickListener(this);
        //修改昵称
        nicknameRl = (RelativeLayout) findViewById(R.id.nicknameRl);
        nicknameTv = (TextView) findViewById(R.id.nicknameTv);
        nicknameTv.setText(userResp.nickName);
        nicknameRl.setOnClickListener(this);
        //性别
        sexTv = (TextView) findViewById(R.id.sexTv);
        if (userResp.sex == UserManager.SEX_MALE) {
            sexTv.setText(R.string.male);
        } else {
            sexTv.setText(R.string.female);
        }
        //选择生日
        birthdayRl = (RelativeLayout) findViewById(R.id.birthdayRl);
        birthdayTv = (TextView) findViewById(R.id.birthdayTv);
        birthdayTv.setText(DateUtil.formatYMD(new Date(userResp.birthday)));
        birthdayRl.setOnClickListener(this);
        //选择居住地
        addressRl = (RelativeLayout) findViewById(R.id.addressRl);
        addressTv = (TextView) findViewById(R.id.addressTv);
        if(!TextUtils.isEmpty(userResp.cityCode)){
            addressTv.setText(userResp.cityCode);
        }
        addressRl.setOnClickListener(this);
        //选择兴趣
        interestRl = (RelativeLayout) findViewById(R.id.interestRl);
        interestTv = (TextView) findViewById(R.id.interestTv);
        interestTv.setText(String.valueOf(userResp.interest));
        interestRl.setOnClickListener(this);
        //技能
        skillRl = (RelativeLayout) findViewById(R.id.skillRl);
        skillTv = (TextView) findViewById(R.id.skillTv);
        skillTv.setText(String.valueOf(userResp.technique));
        skillRl.setOnClickListener(this);
        //选择学校
        schoolRl = (RelativeLayout) findViewById(R.id.schoolRL);
        schoolTv = (TextView) findViewById(R.id.schoolTV);
        schoolTv.setText(userResp.school);
        schoolRl.setOnClickListener(this);
        //选择行业
        industryRl = (RelativeLayout) findViewById(R.id.industryRl);
        industryTv = (TextView) findViewById(R.id.industryTv);
        industryTv.setText(String.valueOf(userResp.vocation));
        industryRl.setOnClickListener(this);
        //选择公司
        companyRl = (RelativeLayout) findViewById(R.id.companyRl);
        companyTv = (TextView) findViewById(R.id.companyTv);
        companyTv.setText(userResp.company);
        companyRl.setOnClickListener(this);
    }

    //所有的监听事件
    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.headerIconIv || i == R.id.cameraIv) {
            imageDialog = ImageDialog.getTakeSinglePhotoWindow();
            showDialog(imageDialog);

            //修改请客号
        } else if (i == R.id.numberRl) {//ActivityUtil.gotoCenterUpdtNumberActivity(activity, REQUEST_NUMBER_CODE);

            //修改昵称
        } else if (i == R.id.nicknameRl) {
            ActivityUtil.gotoCenterUpdtNickNameActivity(activity, REQUEST_NICKNAME_CODE);

            //修改生日
        } else if (i == R.id.birthdayRl) {
            //ActivityUtil.gotoCenterUpdtBirthdayActivity(activity, REQUEST_BIRTHDAYS_CODE);

            //修改居住地
        } else if (i == R.id.addressRl) {
            ActivityUtil.gotoCenterUpdtAddressActivity(activity, REQUEST_ADDRESS_CODE);

            //选择兴趣
        } else if (i == R.id.interestRl) {
            ActivityUtil.gotoCenterUpdtInterestActivity(activity, 0, REQUEST_INTEREST_CODE);

            //选择学校
        } else if (i == R.id.schoolRL) {
            ActivityUtil.gotoCenterUpdtSchoolActivity(activity, REQUEST_SCHOOL_CODE);

            //选择公司
        } else if (i == R.id.companyRl) {
            ActivityUtil.gotoCenterChoosCompanyActivity(activity, REQUEST_COMPANY_CODE);

            //选择行业
        } else if (i == R.id.industryRl) {
            ActivityUtil.gotoCenterChoosSkillActivity(activity, REQUEST_INDUSTRY_CODE);

            //选择技能
        } else if (i == R.id.skillRl) {
            ActivityUtil.gotoCenterChoosIndustryActivity(activity, REQUEST_SKILL_CODE);

        }
    }
    
    private void handleCrop(int resultCode, Intent result) {
        showWaitingDialog();
        Uri photoUri = Crop.getOutput(result);
        //上传头像
        uploadHeaderIcon(photoUri.getPath(), userResp.userCode);
    }

    private void uploadHeaderIcon(String path, int userCode){
        /*RequestParams params = new RequestParams();
        params.addBodyParameter("file", new File(path));
        params.addBodyParameter("userCode", String.valueOf(userCode));
        mHttpUtils.send(HttpMethod.POST, UserManager.HEADER_URL, params, new RequestCallBack<String>() {
            @Override
            public void onStart() {
                super.onStart();
                Log.d(TAG, "start...");
            }

            @Override
            public void onLoading(long total, long current, boolean isUploading) {
                super.onLoading(total, current, isUploading);
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                Log.d(TAG, "onSuccess reply: " + responseInfo.result);
                JSONObject json;
                JSONObject updateHeader = new JSONObject();
                int code = 0;
                try {
                    json = new JSONObject(responseInfo.result.toString());
                    if(json.has("data")){
                        JSONObject data = json.getJSONObject("data");
                        headerIcon = data.getString("path");
                        code = json.optInt("code", 0);
                    }
                    updateHeader.put("headIcon", headerIcon);
                    System.out.println("heaerIcon:" + headerIcon);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(code == 200){
                    changeHeader = mUserManager.modifyUser(userResp.userCode, updateHeader.toString());
                } else {
                    ToastUtils.showToast(R.string.center_upload_failed);
                    hideWaitingDlg();
                }
            }

            @Override
            public void onFailure(HttpException arg0, String msg) {
                Log.e(TAG, msg);
                hideWaitingDlg();
            }
        });*/
    }
    
    //所有的获取返回值
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Crop.RESULT_ERROR) {
            if (BaseApplication.DEBUG) {
                ToastUtils.showToast(Crop.getError(data).getMessage());
            } else {
                ToastUtils.showToast(R.string.crop_error);
            }
        }
        if (resultCode != RESULT_OK)//如果返回的requestCode不是RESULT_OK，直接返回
            return;
        switch (requestCode) {
            case Crop.REQUEST_CROP:
                // 调用剪切返回的
                handleCrop(resultCode, data);
                break;
            case PHOTO_GALLERY: {// 调用Gallery返回的
                showWaitingDialog();
                break;
            }
            case PHOTO_CAMERA: {// 照相机程序返回的,再次调用图片剪辑程序去修剪图片
                doCropPhoto(photoFile);
                break;
            }
            case REQUEST_NUMBER_CODE://请客号
                /*String treamnum = data.getStringExtra(CenterUpdtTreamNumActivity.RESULT_NUMBER);
                if (!(TextUtils.isEmpty(treamnum) || treamnum.equals(userResp.treamNum))) {
                    isChange = true;
                    userReq.treamNum = treamnum;
                    numberTv.setText(treamnum);
                } else {
                    isChange = false;
                    userReq.treamNum = null;
                    numberTv.setText(userResp.treamNum);
                }*/
                break;
            case REQUEST_NICKNAME_CODE://昵称
                String nickname = data.getStringExtra(CenterUpdtNickNameActivity.RESULT_NICKNAME);
                if (!(TextUtils.isEmpty(nickname) || nickname.equals(userResp.nickName))) {
                    isChange = true;
                    userResp.nickName = nickname;
                    nicknameTv.setText(nickname);
                } else {
                    isChange = false;
                    userResp.nickName = "";
                    nicknameTv.setText(userResp.nickName);
                }
                break;
            case REQUEST_BIRTHDAYS_CODE://生日
                String birthday = data.getStringExtra(CenterUpdtBirthdayActivity.RESULT_BIRTHDAY);
                if (!TextUtils.isEmpty(birthday) && !birthday.equalsIgnoreCase(DateUtil.formatYMD(new Date(userResp.birthday)))) {
                    isChange = true;
                    userResp.birthday = DateUtil.getDateByYMD(birthday).getTime();
                    birthdayTv.setText(birthday);
                } else {
                    isChange = false;
                    userResp.birthday = 0;
                    birthdayTv.setText(DateUtil.formatYMD(new Date(userResp.birthday)));
                }
                break;
            case REQUEST_ADDRESS_CODE://居住地
                String address = data.getExtras().getString(CenterChoosAddressActivity.RESULT_ADDRESS);
                if (!TextUtils.isEmpty(address) && !address.equalsIgnoreCase(userResp.cityCode)) {
                    isChange = true;
                    userResp.cityCode = address;
                } else {
                    isChange = false;
                }
                addressTv.setText(address);
                break;
            case REQUEST_INTEREST_CODE://兴趣
                interestTv.setText(data.getExtras().getString(CenterChoosInterestActivity.RESULT_INTEREST));
                break;
            case REQUEST_SCHOOL_CODE://学校
                String school = data.getStringExtra(CenterChoosSchoolActivity.RESULT_SCHOOL);
                if (!(TextUtils.isEmpty(school) || school.equals(userResp.school))) {
                    isChange = true;
                    userResp.school = school;
                    schoolTv.setText(school);
                } else {
                    isChange = false;
                    userResp.school = "";
                    schoolTv.setText(userResp.school);
                }
                break;
            case REQUEST_COMPANY_CODE://公司
                String company = data.getStringExtra(CenterChoosCompanyActivity.RESULT_COMPANY);
                if (!(TextUtils.isEmpty(company) || company.equals(userResp.company))) {
                    isChange = true;
                    userResp.company = company;
                    companyTv.setText(company);
                } else {
                    isChange = false;
                    userResp.company = "";
                    companyTv.setText(userResp.company);
                }
                break;
            case REQUEST_INDUSTRY_CODE://行业
                industryTv.setText(data.getExtras().getString(CenterChoosIndustryActivity.RESULT_INDUSTRY));
                break;
            case REQUEST_SKILL_CODE://技能
                skillTv.setText(data.getExtras().getString(CenterChoosSkillActivity.RESULT_SKILL));
                break;
        }
    }

    //调用网络请求成功
    @Override
    protected boolean onHandleBiz(int what, int result, Object obj) {
        if (what == changematerial) {
            switch (result) {
                case UserManager.RESULT_SUCCESS:
                    ToastUtils.showToast(R.string.updata_success);
                    /*if (!TextUtils.isEmpty(userReq.treamNum)) {
                        userResp.treamNum = userReq.treamNum;
                    }*/
                    if (!TextUtils.isEmpty(userResp.nickName)) {
                        userResp.nickName = userResp.nickName;
                    }
                    if (userResp.birthday > 0) {
                        userResp.birthday = userResp.birthday;
                    }
                    if (!TextUtils.isEmpty(userResp.school)) {
                        userResp.school = userResp.school;
                    }
                    if (!TextUtils.isEmpty(userResp.company)) {
                        userResp.company = userResp.company;
                    }
                    if (!TextUtils.isEmpty(userResp.cityCode)) {
                        userResp.cityCode = userResp.cityCode;
                    }
                    mUserDao.insertOrUpdateUser(activity, userResp);
                    super.onBackPressed();
                    break;
                default:
                    return false;
            }
        } else if (changeHeader == what) {
            switch (result) {
                case UserManager.RESULT_SUCCESS:
                    ToastUtils.showToast(R.string.center_upload_success);
                    userResp.headIcon = headerIcon;
                    mUserDao.insertOrUpdateUser(this, userResp);
                    //设置头像
                    BitmapMgr.loadBitmap(this, iconTv, UserManager.DOMAIN + headerIcon, userResp.sex == UserManager.SEX_FEMALE ? R.drawable.female_icon1:R.drawable.male_icon1);
                    break;
                default:
                    return false;
            }
        }
        return true;
    }

    //点击手机返回键保存
    @Override
    public void onBackPressed() {
        if (isChange) {
            showWaitingDialog();
            changematerial = mUserManager.updateUser(userResp.userCode, userResp);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * 拍照获取图片
     */

    protected void doTakePhoto() {
        try {
            // Launch camera to take photo for selected contact
            if (!PHOTO_DIR.exists() || PHOTO_DIR.isFile()) {
                PHOTO_DIR.delete();
            }
            PHOTO_DIR.mkdirs();// 创建照片的存储目录
            photoFile = new File(PHOTO_DIR, getPhotoFileName());// 给新照的照片文件命名
            final Intent intent = getTakePickIntent(photoFile);
            startActivityForResult(intent, PHOTO_CAMERA);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.center_photopickernotfoundtext, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 用当前时间给取得的图片命名
     */
    private String getPhotoFileName() {
        return "header.png";
    }

    // 请求Gallery程序
    protected void doPickPhotoFromGallery() {
        try {
            // Launch picker to choose photo for selected contact
            final Intent intent = getPhotoPickIntent();
            startActivityForResult(intent, PHOTO_GALLERY);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.center_photopickernotfoundtext, Toast.LENGTH_LONG).show();
        }
    }

    protected void doCropPhoto(File f) {
        try {
            // 启动gallery去剪辑这个照片
            final Intent intent = getCropImageIntent(Uri.fromFile(f));
            startActivityForResult(intent, PHOTO_GALLERY);
        } catch (Exception e) {
            Toast.makeText(this, R.string.center_photopickernotfoundtext, Toast.LENGTH_LONG).show();
        }
    }
}
