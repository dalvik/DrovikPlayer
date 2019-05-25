package com.drovik.player.location;

import com.android.library.ui.activity.BaseCompatActivity;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.crixmod.sailorcast.SailorCast;
import com.drovik.player.R;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/***
 * 单点定位示例，用来展示基本的定位结果，配置在LocationService.java中
 * 默认配置也可以在LocationService中修改
 * 默认配置的内容自于开发者论坛中对开发者长期提出的疑问内容
 * 
 * @author baidu
 *
 */
public class LocationActivity extends BaseCompatActivity {

	private LocationService locationService;
	private TextView LocationResult;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location);
		setTitle(R.string.tools_gps_title);
		LocationResult = (TextView) findViewById(R.id.gps_info);
		LocationResult.setMovementMethod(ScrollingMovementMethod.getInstance());
		locationService = new LocationService(this);
		LocationClientOption option = locationService.getDefaultLocationClientOption();
		option.setScanSpan(1000);
		option.setNeedDeviceDirect(true);
		option.setLocationNotify(true);
		option.setIgnoreKillProcess(false);
		option.setIsNeedAltitude(true);
		locationService.setLocationOption(option);
	}

	/**
	 * 显示请求字符串
	 * 
	 * @param str
	 */
	public void logMsg(String str) {
		final String s = str;
		try {
			new Thread(new Runnable() {
				@Override
				public void run() {
					LocationResult.post(new Runnable() {
						@Override
						public void run() {
							LocationResult.setText(s);
						}
					});
				}
			}).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void logMsg(SpannableStringBuilder str) {
		final SpannableStringBuilder s = str;
		try {
			new Thread(new Runnable() {
				@Override
				public void run() {
					LocationResult.post(new Runnable() {
						@Override
						public void run() {
							LocationResult.setText(s);
						}
					});
				}
			}).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/***
	 * Stop location service
	 */
	@Override
	protected void onStop() {
		locationService.unregisterListener(mListener); //注销掉监听
		locationService.stop(); //停止定位服务
		super.onStop();
	}

	@Override
	protected void onStart() {
		super.onStart();
		//获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
		locationService.registerListener(mListener);
		//注册监听
		int type = getIntent().getIntExtra("from", 0);
		if (type == 0) {
			locationService.setLocationOption(locationService.getDefaultLocationClientOption());
		} else if (type == 1) {
			locationService.setLocationOption(locationService.getOption());
		}
		locationService.start();// 定位SDK start之后会默认发起一次定位请求，开发者无须判断isstart并主动调用request
	}

	
	/*****
	 *
	 * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
	 *
	 */
	private BDAbstractLocationListener mListener = new BDAbstractLocationListener() {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (null != location && location.getLocType() != BDLocation.TypeServerError) {
				SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
				String locationName = "定位时间 : ";
				String locationType = "\n定位类型 : ";
				/*ForegroundColorSpan locationNameColorSpan = new ForegroundColorSpan(Color.parseColor("#0099EE"));
				int index = 0;
				int locationNameLength = getTextLength(locationName);
				spannableStringBuilder.append(locationName);
				spannableStringBuilder.setSpan(locationNameColorSpan, index, locationNameLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				index += locationNameLength;
				if(!isEmpty(location.getTime())){
					spannableStringBuilder.append(location.getTime());
					index += getTextLength(location.getTime());
				}
				int locationTypeLength = getTextLength(locationType);
				spannableStringBuilder.append(locationType);
				ForegroundColorSpan locationTypeColorSpan = new ForegroundColorSpan(Color.parseColor("#0099EE"));
				spannableStringBuilder.setSpan(locationTypeColorSpan, index, locationTypeLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				index += locationTypeLength;
*/
				StringBuffer sb = new StringBuffer(256);
				/**
				 * 时间也可以使用systemClock.elapsedRealtime()方法 获取的是自从开机以来，每次回调的时间；
				 * location.getTime() 是指服务端出本次结果的时间，如果位置不发生变化，则时间不变
				 */
				sb.append(locationName);
				sb.append(location.getTime());
				sb.append(locationType);// 定位类型
				sb.append(location.getLocType());
				sb.append("\n类型描述 : ");// *****对应的定位类型说明*****
                sb.append(location.getLocTypeDescription());
				sb.append("\n纬度 : ");// 纬度
				sb.append(location.getLatitude());
				sb.append("\n经度 : ");// 经度
				sb.append(location.getLongitude());
				sb.append("\n半径 : ");// 半径
				sb.append(location.getRadius());
				sb.append("\n国家码 : ");// 国家码
				sb.append(location.getCountryCode());
				sb.append("\n国家名称 : ");// 国家名称
				sb.append(location.getCountry());
				sb.append("\n城市编码 : ");// 城市编码
				sb.append(location.getCityCode());
				sb.append("\n城市 : ");// 城市
				sb.append(location.getCity());
				sb.append("\n区 : ");// 区
				sb.append(location.getDistrict());
				sb.append("\n街道 : ");// 街道
				sb.append(location.getStreet());
				sb.append("\n地址信息 : ");// 地址信息
				sb.append(location.getAddrStr());
				sb.append("\n室内状态: ");// *****返回用户室内外判断结果*****
				sb.append(location.getUserIndoorState());
				sb.append("\n方向 : ");
				sb.append(location.getDirection());// 方向
				sb.append("\n位置描述: ");
                sb.append(location.getLocationDescribe());// 位置语义化信息
				sb.append("\nPoi: ");// POI信息
				if (location.getPoiList() != null && !location.getPoiList().isEmpty()) {
					for (int i = 0; i < location.getPoiList().size(); i++) {
						Poi poi = (Poi) location.getPoiList().get(i);
						sb.append(poi.getName() + ";");
					}
				}
				if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
					sb.append("\n速度 : ");
					sb.append(location.getSpeed());// 速度 单位：km/h
					sb.append("\n卫星数目 : ");
					sb.append(location.getSatelliteNumber());// 卫星数目
					sb.append("\n海拔 : ");
					sb.append(location.getAltitude() + " 米");// 海拔高度 单位：米
					sb.append("\nGPS信号质量 : ");
                    sb.append(location.getGpsAccuracyStatus());// *****gps质量判断*****
					sb.append("\n结果 : ");
					sb.append("GPS定位成功");
				} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
					// 运营商信息
				    if (location.hasAltitude()) {// *****如果有海拔高度*****
				        sb.append("\n海拔 : ");
	                    sb.append(location.getAltitude());// 单位：米
				    }
					sb.append("\n运营商 : ");// 运营商信息
					sb.append(location.getOperators());
					sb.append("\n结果 : ");
					sb.append("网络定位成功");
				} else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
					sb.append("\n结果 : ");
					sb.append("离线定位成功，离线定位结果也是有效的");
				} else if (location.getLocType() == BDLocation.TypeServerError) {
					sb.append("\n结果 : ");
					sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
				} else if (location.getLocType() == BDLocation.TypeNetWorkException) {
					sb.append("\n结果 : ");
					sb.append("网络不同导致定位失败，请检查网络是否通畅");
				} else if (location.getLocType() == BDLocation.TypeCriteriaException) {
					sb.append("\n结果 : ");
					sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
				}
				logMsg(sb.toString());
			}
		}

	};

	private int getTextLength(String content){
		return content.length();
	}

	private boolean isEmpty(String content){
		return TextUtils.isEmpty(content);
	}
}
