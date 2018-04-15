package com.android.library.ui.view;

import android.app.ActionBar;
import android.content.Context;
import android.media.AudioManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.SeekBar;

import com.android.library.R;

public class VolumePopupWindow extends PopupWindow {

	private View view;
	private VerticalSeekBar seekBar;
	private AudioManager audioManager;
	private static final int MULTIPLYING_POWER = 5;

	public VolumePopupWindow(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
		view = inflater.inflate(R.layout.base_popup_volume_adjust, null);
		seekBar = (VerticalSeekBar) view.findViewById(R.id.volume_progress);
		audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		
		seekBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) * MULTIPLYING_POWER);
		seekBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) * MULTIPLYING_POWER);
		seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
				audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
						progress / MULTIPLYING_POWER, 0);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}
		});
		setOutsideTouchable(true);
		setContentView(view);
		setWidth(ActionBar.LayoutParams.WRAP_CONTENT);
		setHeight(ActionBar.LayoutParams.WRAP_CONTENT);
		setFocusable(false);
	}

	public void seekUp() {
		if (seekBar.getProgress() < seekBar.getMax()) {
			seekBar.setProgress(seekBar.getProgress() + 1);
			seekBar.onSizeChanged(getWidth(), getHeight(), 0, 0);
		}
	}

	public void seekDown() {
		if (seekBar.getProgress() > 0) {
			seekBar.setProgress(seekBar.getProgress() - 1);
			seekBar.onSizeChanged(getWidth(), getHeight(), 0, 0);
		}
	}

}
