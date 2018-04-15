package com.android.audiorecorder.provider;

public interface FileColumn {
    
    public final static int FILE_DOWN_LOAD = 1;
    public final static int FILE_UP_LOAD = 2;
    
    public final static int STATE_FILE_UP_DOWN_INIT = 0;
    public final static int STATE_FILE_UP_DOWN_ING = 1;
    public final static int STATE_FILE_UP_DOWN_SUCCESS = 2;
    public final static int STATE_FILE_UP_DOWN_FAILED = 3;
    public final static int STATE_FILE_UP_DOWN_WAITING = 4;
    
    
    /**
     * _id:
     * file_type:        0:jpg-1:audio-2:video-3:other
     * mimetype:         jpg/jpeg/wav/wmv...
     * local:            0:local 1:network
     * up_down_load:     0:up 1:down
     * local_path:
     * remote_path:
     * file_size:
     * _duration:
     * resulation_x:
     * resulation_y:
     * up_down_status:   1:down or up load
     * down_load_time:   local : insert time, remote: download time
     * up_load_time:     upload time
     * up_down_load_byte:
     * up_down_load_result:   0: fail 1:success
     * up_down_load_msg:
     * @param context
     */
    /**
     * file _id
     */
    public static final String COLUMN_ID = "_id";
    
    /**
     * file_type<br/>
     * 0:jpg<br/>
     * 1:audio<br/>
     * 2:video<br/>
     * 3:other<br/>
     */
    public static final String COLUMN_FILE_TYPE = "file_type";
    
    /**
     * file mime type<br/>
     * 0:jpg<br/>
     * 1:audio<br/>
     * 2:video<br/>
     * 3:text<br/>
     * 4:apk<br/>
     * 5:zip<br/>
     * 6:other<br/>
     */
    public static final String COLUMN_MIME_TYPE = "mime_type";
    
    /**
     * thumb name<br/>
     * yyyymm
     */
    public static final String COLUMN_THUMB_NAME = "thumb_name";

    /**
     * file local path
     */
    public static final String COLUMN_LOCAL_PATH = "file_local_path";
    
    /**
     * file remote path
     */
    public static final String COLUMN_REMOTE_PATH = "file_remote_path";
    
    /**
     * file length
     */
    public static final String COLUMN_FILE_SIZE = "file_size";
    
    /**
     * audio video file duration
     */
    public static final String COLUMN_FILE_DURATION = "_duration";
    
    /**
     * launch mode<br/>
     * 0:idle<br/>
     * 1:call<br/>
     * 2:manly<br/>
     * 3:auto<br/>
     */
    public static final String COLUMN_LAUNCH_MODE = "launch_mode";
    
    /**
     * video file resolution x
     */
    public static final String COLUMN_FILE_RESOLUTION_X = "resolution_x";
    
    /**
     * video file resolution y
     */
    public static final String COLUMN_FILE_RESOLUTION_Y = "resolution_y";

    /**
     * video file thumbnail path if exist
     */
    public static final String COLUMN_FILE_THUMBNAIL = "thumbnail_path";

    /**
     * file up or download<br/>
     * local file no item<br/>
     * remote file:<br/>
     * 0:default no action<br/>
     * 1:down<br/>
     * 2:up<br/>
     */
    public static final String COLUMN_UP_OR_DOWN = "up_or_down";
    
    /**
    * down or up load status<br/>
    * 0:unupload<br/>
    * 1:loading<br/>
    * 2:success<br/>
    * 3:fail<br/>
    * 4:wait<br/>
    * -1:delete<br/>
    */
    public static final String COLUMN_UP_DOWN_LOAD_STATUS = "up_down_status";
    
    /**
     * down load time<br/>
     * local file: insert time<br/>
     * remote file: down load time<br/>
     * when record use create time
     */
    public static final String COLUMN_DOWN_LOAD_TIME = "down_load_time";
    
    /**
     * up load time
     */
    public static final String COLUMN_UP_LOAD_TIME = "up_load_time";
    
    /**
     * down or up load byte
     */
    public static final String COLUMN_UP_LOAD_BYTE = "up_down_load_byte";
    
    /**
     * show notification<br/>
     * 0:hide default<br/>
     * 1:show<br/>
     */
    public static final String COLUMN_SHOW_NOTIFICATION = "show_notifiaction";
    
    /**
     * down or up load error message
     */
    public static final String COLUMN_UP_LOAD_MESSAGE = "up_down_load_msg";

    /**
     * use phone record
     */
    public static final String COLUMN_CALL_NUMBER = "phone_call_number";

    /**
     * use phone record
     * in: 1
     * out: 2
     */
    public static final String COLUMN_CALL_TYPE = "phone_call_type";

    /**
     *  call record count
     */
    public static final String COLUMN_CALL_COUNT = "phone_call_count";

    /**
     * file setting table struct : key : value 
     */
    public static final String COLUMN_SETTING_KEY = "_key";
    
    public static final String COLUMN_SETTING_VALUE = "_value";
    
    /**
     * database file rebuild
     */
    public static final String COLUMN_FILE_INIT = "file_init";
    
    /**
     * uuid
     */
    public static final String COLUMN_UUID = "uuid";//对注册用户有效
    
    /**
     * remote server url
     */
    public static final String COLUMN_SERVER_UPLOAD_URL = "upload_url";

}
