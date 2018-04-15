package com.android.library.ui.checklist;

import android.os.Parcel;
import android.os.Parcelable;

public class LibCheckListCheckedImg implements Parcelable {
    private String image;
    private String name;
    private String id;
    private String memo;

    public LibCheckListCheckedImg() {
    }
    
    public LibCheckListCheckedImg(Parcel arg0) {
        if (arg0 != null) {
            image = arg0.readString();
            name = arg0.readString();
            id = arg0.readString();
            memo = arg0.readString();
        }
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(image);
        dest.writeString(name);
        dest.writeString(id);
        dest.writeString(memo);
    }

    public static final Creator<LibCheckListCheckedImg> CREATOR = new Creator<LibCheckListCheckedImg>() {

        @Override
        public LibCheckListCheckedImg[] newArray(int arg0) {
            return new LibCheckListCheckedImg[arg0];
        }

        @Override
        public LibCheckListCheckedImg createFromParcel(Parcel arg0) {
            return new LibCheckListCheckedImg(arg0);
        }
    };
}
