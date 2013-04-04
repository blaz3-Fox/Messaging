/**
 * 
 */
package com.hchi590.chatclient.audio;

import java.io.IOException;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.Log;

/**
 * @author hchi590
 * 
 */
public class DownloadMediaPlayer extends PlayAudioBase {

	private MediaPlayer _player;

	public DownloadMediaPlayer(String filename) {
		super(filename);
		_player = new MediaPlayer();
	}
	
	/**
	 * 
	 */
	@Override
	protected boolean startPlaying() {
		_player = new MediaPlayer();

		try {
			_player.setDataSource(_filename);
			_player.setOnCompletionListener(completed);
			_player.prepare();
			Log.d("audiotiming", " startplaydownload" + System.currentTimeMillis());
			_player.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	private OnCompletionListener completed = new OnCompletionListener() {

		@Override
		public void onCompletion(MediaPlayer mp) {
			_player.release();
			_player = null;
		}
		
	};
	
	/**
	 * 
	 */
	@Override
	public boolean stopPlaying() {
		_player.stop();
		_player.release();
		_player = null;
		return false;
	}
	
	/**
	 * Allows Parent Context to register a callback when streaming of a file finishes.
	 * @param listener A MediaPlayer.OnCompletionListener
	 */
	public void registerOnCompletionListener(OnCompletionListener listener) {
		_player.setOnCompletionListener(listener);
	}

}
