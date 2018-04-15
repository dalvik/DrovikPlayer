package com.android.audiorecorder.dao;

import android.util.Xml;

import com.android.audiorecorder.utils.StringUtil;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

public class UpdateInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int versionCode;
	
	private String versionName;
	
	private String downloadUrl;
	
	private String updateLog;
	
	private String packageSize;

	//add config item
	private String uploadUrl;
	
	private String sendSuggesetPhoneNumber;
	
	private String duration = "";
	
	public UpdateInfo() {
		super();
	}

	public int getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	public String getUpdateLog() {
		return updateLog;
	}

	public void setUpdateLog(String updateLog) {
		this.updateLog = updateLog;
	}

	
	public String getPackageSize() {
		return packageSize;
	}

	public void setPackageSize(String packageSize) {
		this.packageSize = packageSize;
	}

	public String getUploadUrl() {
        return uploadUrl;
    }

    public void setUploadUrl(String uploadUrl) {
        this.uploadUrl = uploadUrl;
    }

    public String getSendSuggesetPhoneNumber() {
        return sendSuggesetPhoneNumber;
    }

    public void setSendSuggesetPhoneNumber(String sendSuggesetPhoneNumber) {
        this.sendSuggesetPhoneNumber = sendSuggesetPhoneNumber;
    }
    
    public void setDuration(String duration){
        this.duration = duration;
    }
    
    public String getDuration(){
        return duration;
    }
    
    @Override
    public String toString() {
        return "UpdateInfo [versionCode=" + versionCode + ", versionName="
                + versionName + ", downloadUrl=" + downloadUrl + ", updateLog="
                + updateLog + ", packageSize=" + packageSize + ", uploadUrl="
                + uploadUrl + ", sendSuggesetPhoneNumber="
                + sendSuggesetPhoneNumber + "]";
    }

    public static UpdateInfo parse(InputStream inputStream) throws IOException {
		UpdateInfo updateInfo = null;
		XmlPullParser xmlPullParser = Xml.newPullParser();
		try {
			xmlPullParser.setInput(inputStream, "utf-8");
			xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			int eventType = xmlPullParser.getEventType();
			while(eventType != XmlPullParser.END_DOCUMENT) {
				switch(eventType) {
				case XmlPullParser.START_TAG:
					String tag = xmlPullParser.getName();
					if(tag.equalsIgnoreCase("android")) {
						updateInfo = new UpdateInfo();
					}else if(updateInfo != null) {
						if(tag.equalsIgnoreCase("versionCode")) {
							updateInfo.setVersionCode(StringUtil.toInt(xmlPullParser.nextText(),0));
						} else if(tag.equalsIgnoreCase("versionName")) {
							updateInfo.setVersionName(xmlPullParser.nextText());
						} else if(tag.equalsIgnoreCase("downloadUrl")) {
							updateInfo.setDownloadUrl(xmlPullParser.nextText());
						} else if(tag.equalsIgnoreCase("updateLog")) {
							updateInfo.setUpdateLog(xmlPullParser.nextText());
						} else if(tag.equalsIgnoreCase("packageSize")) {
							updateInfo.setPackageSize(xmlPullParser.nextText());
						} else if(tag.equalsIgnoreCase("uploadUrl")) {
						    updateInfo.setUploadUrl(xmlPullParser.nextText());
                        } else if(tag.equalsIgnoreCase("phonenumber")) {
                            updateInfo.setSendSuggesetPhoneNumber(xmlPullParser.nextText());
                        } else if(tag.equalsIgnoreCase("duration")) {
                            updateInfo.setDuration(xmlPullParser.nextText());
                        }
					}
					break;
				case XmlPullParser.END_TAG:
					break;
					default:
						break;
				}
				eventType = xmlPullParser.next();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return updateInfo;
	}

}
