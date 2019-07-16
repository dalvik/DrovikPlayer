

package com.drovik.compass;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.library.ui.activity.BaseCompatActivity;

import java.util.Locale;

public class CompassActivity extends BaseCompatActivity implements SensorEventListener{

    private final float MAX_ROATE_DEGREE = 1.0f;
    private SensorManager mSensorManager;
    private float mDirection;
    private float mTargetDirection;
    private AccelerateInterpolator mInterpolator;
    protected final Handler mHandler = new Handler();
    private boolean mStopDrawing;
    private boolean mChinease;

    View mCompassView;
    CompassView mPointer;
    TextView mAltitudeTextView;
    TextView mPressureTextView;
    LinearLayout mDirectionLayout;
    LinearLayout mAngleLayout;

    //sensor data
    private float[] accelerometerValues = new float[3];  //data of acclerometer sensor
    private float[] magneticFieldValues = new float[3]; //data of magnetic field sensor
    private float[] ABC = new float[16];
    private float[] DEF = new float[16];

    private static final String TAG = "CompassActivity";

    protected Runnable mCompassViewUpdater = new Runnable() {
        @Override
        public void run() {
            if (mPointer != null && !mStopDrawing) {
                if (mDirection != mTargetDirection) {

                    // calculate the short routine
                    float to = mTargetDirection;
                    if (to - mDirection > 180) {
                        to -= 360;
                    } else if (to - mDirection < -180) {
                        to += 360;
                    }

                    // limit the max speed to MAX_ROTATE_DEGREE
                    float distance = to - mDirection;
                    if (Math.abs(distance) > MAX_ROATE_DEGREE) {
                        distance = distance > 0 ? MAX_ROATE_DEGREE : (-1.0f * MAX_ROATE_DEGREE);
                    }

                    // need to slow down if the distance is short
                    mDirection = normalizeDegree(mDirection + ((to - mDirection) * mInterpolator.getInterpolation(Math.abs(distance) > MAX_ROATE_DEGREE ? 0.4f : 0.3f)));
                    mPointer.updateDirection(mDirection);
                }
                updateDirection();
                mHandler.postDelayed(mCompassViewUpdater, 20);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.compass_layout);
        setActionBarVisiable(View.GONE);
        fullScreen(R.color.black);
        com.android.library.utils.Utils.setStatusTextColor(true, this);
        setActionBarBackgroundColor(R.color.base_actionbar_background, R.color.base_actionbar_background);///actionbar and status bar
        //actionbar and status bar
        findViewById(R.id.action_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CompassActivity.this.finish();
            }
        });
        initResources();
        initServices();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL );
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_NORMAL );
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_NORMAL );
        mStopDrawing = false;
        mHandler.postDelayed(mCompassViewUpdater, 20);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mStopDrawing = true;
        mSensorManager.unregisterListener(this);
    }

    private void initResources() {
        mDirection = 0.0f;
        mTargetDirection = 0.0f;
        mInterpolator = new AccelerateInterpolator();
        mStopDrawing = true;
        mChinease = TextUtils.equals(Locale.getDefault().getLanguage(), "zh");

        mCompassView = findViewById(R.id.view_compass);
        mPointer = (CompassView) findViewById(R.id.compass_pointer);
        mAltitudeTextView = (TextView) findViewById(R.id.textview_altitude);
        mPressureTextView = (TextView) findViewById(R.id.textview_pressure);
        mDirectionLayout = (LinearLayout) findViewById(R.id.layout_direction);
        mAngleLayout = (LinearLayout) findViewById(R.id.layout_angle);

        mPointer.setImageResource(mChinease ? R.mipmap.compass_cn : R.mipmap.compass);
    }

    private void initServices() {
        // sensor manager
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        // location manager
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
    }

    private void updateDirection() {
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        mDirectionLayout.removeAllViews();
        mAngleLayout.removeAllViews();

        ImageView east = null;
        ImageView west = null;
        ImageView south = null;
        ImageView north = null;
        float direction = normalizeDegree(mTargetDirection * -1.0f);
        if (direction > 22.5f && direction < 157.5f) {
            // east
            east = new ImageView(this);
            east.setImageResource(mChinease ? R.mipmap.e_cn : R.mipmap.e);
            east.setLayoutParams(lp);
        } else if (direction > 202.5f && direction < 337.5f) {
            // west
            west = new ImageView(this);
            west.setImageResource(mChinease ? R.mipmap.w_cn : R.mipmap.w);
            west.setLayoutParams(lp);
        }

        if (direction > 112.5f && direction < 247.5f) {
            // south
            south = new ImageView(this);
            south.setImageResource(mChinease ? R.mipmap.s_cn : R.mipmap.s);
            south.setLayoutParams(lp);
        } else if (direction < 67.5 || direction > 292.5f) {
            // north
            north = new ImageView(this);
            north.setImageResource(mChinease ? R.mipmap.n_cn : R.mipmap.n);
            north.setLayoutParams(lp);
        }

        if (mChinease) {
            // east/west should be before north/south
            if (east != null) {
                mDirectionLayout.addView(east);
            }
            if (west != null) {
                mDirectionLayout.addView(west);
            }
            if (south != null) {
                mDirectionLayout.addView(south);
            }
            if (north != null) {
                mDirectionLayout.addView(north);
            }
        } else {
            if (south != null) {
                mDirectionLayout.addView(south);
            }
            if (north != null) {
                mDirectionLayout.addView(north);
            }
            if (east != null) {
                mDirectionLayout.addView(east);
            }
            if (west != null) {
                mDirectionLayout.addView(west);
            }
        }

        int direction2 = (int) direction;
        boolean show = false;
        if (direction2 >= 100) {
            mAngleLayout.addView(getNumberImage(direction2 / 100));
            direction2 %= 100;
            show = true;
        }
        if (direction2 >= 10 || show) {
            mAngleLayout.addView(getNumberImage(direction2 / 10));
            direction2 %= 10;
        }
        mAngleLayout.addView(getNumberImage(direction2));

        ImageView degreeImageView = new ImageView(this);
        degreeImageView.setImageResource(R.mipmap.degree);
        degreeImageView.setLayoutParams(lp);
        mAngleLayout.addView(degreeImageView);
    }

    private ImageView getNumberImage(int number) {
        ImageView image = new ImageView(this);
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        switch (number) {
            case 0:
                image.setImageResource(R.mipmap.number_0);
                break;
            case 1:
                image.setImageResource(R.mipmap.number_1);
                break;
            case 2:
                image.setImageResource(R.mipmap.number_2);
                break;
            case 3:
                image.setImageResource(R.mipmap.number_3);
                break;
            case 4:
                image.setImageResource(R.mipmap.number_4);
                break;
            case 5:
                image.setImageResource(R.mipmap.number_5);
                break;
            case 6:
                image.setImageResource(R.mipmap.number_6);
                break;
            case 7:
                image.setImageResource(R.mipmap.number_7);
                break;
            case 8:
                image.setImageResource(R.mipmap.number_8);
                break;
            case 9:
                image.setImageResource(R.mipmap.number_9);
                break;
        }
        image.setLayoutParams(lp);
        return image;
    }

    float orientation[] = new float[3];

    private SensorEventListener mPressureSensorEventListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent event) {
            mAltitudeTextView.setText(getString(R.string.altitude, (int) calculateAltitude(event.values[0])));
            mPressureTextView.setText(getString(R.string.pressure, event.values[0] / 10));
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };


    private float calculateAltitude(float pressure) {
        float sp = 1013.25f; //standard pressure
        return (sp - pressure) * 100.0f / 12.7f;
    }

    private float normalizeDegree(float degree) {
        return (degree + 720) % 360;
    }

   /* @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d(TAG, "==> onSensorChanged: " + event.sensor.getType());
        if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE)
            return;

        // Gets the value of the sensor that has been changed
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                accelerometerValues = event.values.clone();
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                magneticFieldValues = event.values.clone();
                break;
        }
        if (accelerometerValues != null && magneticFieldValues != null) {
            boolean success = SensorManager.getRotationMatrix(ABC, DEF, accelerometerValues, magneticFieldValues);
            if (success) {
                SensorManager.getOrientation(ABC, orientation);
                double azimuth = Math.toDegrees(orientation[0]);
                //double pitch = Math.toDegrees(orientation[1]);
                //double roll = Math.toDegrees(orientation[2]);
                //float direction = orientation[0];
                mTargetDirection = (float) -azimuth;
                //Log.d(TAG, "==> " + mTargetDirection + " " + direction + " " + azimuth);
            }
        }
        //Log.d(TAG, "==> onSensorChanged: " + mDirection);
        //getOritation();
    }*/

   private  boolean ready;

   @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                for(int i=0; i<3; i++){
                    accelerometerValues[i] =  event.values[i];
                }
                if(magneticFieldValues[0] != 0)
                    ready = true;

                break;

            case Sensor.TYPE_MAGNETIC_FIELD:
                for(int i=0; i<3; i++){
                    magneticFieldValues[i] = event.values[i];
                }
                if(accelerometerValues[2] != 0)
                    ready = true;

                break;
        }

        if(!ready)
            return;

        boolean cek = SensorManager.getRotationMatrix(ABC, DEF, accelerometerValues, magneticFieldValues);
        //Log.d(TAG, "==> onSensorChanged: " + cek);
       if (cek) {
           SensorManager.getOrientation(ABC, orientation);
           double azimuth = Math.toDegrees(orientation[0]);
           //double pitch = Math.toDegrees(orientation[1]);
           //double roll = Math.toDegrees(orientation[2]);
           //float direction = orientation[0];
           mTargetDirection = (float) -azimuth;
           //Log.d(TAG, "==> " + mTargetDirection + " " + mTargetDirection + " " + azimuth);
       }
        /*if(cek){
            SensorManager.getOrientation(ABC, DEF);
            mInclination = SensorManager.getInclination(inclineMatrix);

            //display every 30th values
            if(counter++ % 30 == 0){
                //do your code, what you want to do to the result
                counter = 1;
            }
        }*/
   }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
