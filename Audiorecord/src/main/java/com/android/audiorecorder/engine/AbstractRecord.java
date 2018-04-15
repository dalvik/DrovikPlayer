/**   
 * Copyright © 2016 浙江大华. All rights reserved.
 * 
 * @title: AbstractRecord.java
 * @description: TODO
 * @author: 23536   
 * @date: 2016年4月28日 上午11:02:14 
 */
package com.android.audiorecorder.engine;

public abstract class AbstractRecord {

    public abstract void startRecord();
    
    public abstract void stopRecord();
    
    public void saveRecordFile(String json){
        
    }
}
