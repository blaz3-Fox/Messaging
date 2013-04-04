/**
 * 
 */
package com.hchi590.chatclient.audio;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.Log;

import com.hchi590.chatclient.ServerAddress;

/**
 * @author hchi590
 * Concrete implementation of PlayAudio using the Android MediaPlayer.
 */
public class StreamMediaPlayer extends PlayAudioBase {

	private MediaPlayer _player;

	public StreamMediaPlayer() {
		super("test.mp3");
		_player = new MediaPlayer();
	}

	/**
	 * 
	 * @param filename Name of file to be streamed.
	 */
	public StreamMediaPlayer(String filename) {
		super(filename);
		_player = new MediaPlayer();
	}
	
	/**
	 * Start streaming.
	 */
	@Override
	protected boolean startPlaying() {
		
		//Initialise MediaPlayer
		_player.reset();
		
		//Wait for MediaPlayer to prepare before starting streaming
		_player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

			@Override
			public void onPrepared(MediaPlayer mp) {
				Log.d("audiotiming", _filename + " startplaystream " + System.currentTimeMillis());
				_player.start();
			}
		});
		
		//Set data source to specified and prepare asynchronously
		try {
			_player.setOnCompletionListener(completed);
			_player.setDataSource(generateURL(_filename));
			_player.prepareAsync();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return false;
		} catch (SecurityException e) {
			e.printStackTrace();
			return false;
		} catch (IllegalStateException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private OnCompletionListener completed = new OnCompletionListener() {

		@Override
		public void onCompletion(MediaPlayer mp) {
			_player.release();
			_player = null;
		}
		
	};
	
	/**
	 * Stop streaming and destroy instance of MediaPlayer
	 */
	@Override
	public boolean stopPlaying() {
		_isPlaying = false;
		_player.stop();
		_player.release();
		_player = null;
		return false;
	}

	/**
	 * Generates the URL to prepare from given the name of the file to be streamed.
	 * @param filename Name of file to be streamed.
	 * @return The URL of the file.
	 * @throws UnknownHostException 
	 */
	private String generateURL(String filename) throws UnknownHostException {
		InetAddress address = InetAddress.getByName(ServerAddress.getInstance().getServerAddress());
		String ip = address.getHostAddress();
		String url = "http://" + ip + ":" + ServerAddress.getInstance().getPort() + "/" + filename;
		return url;
	}
	
	/**
	 * Allows Parent Context to register a callback when streaming of a file finishes.
	 * @param listener A MediaPlayer.OnCompletionListener
	 */
	public void registerOnCompletionListener(OnCompletionListener listener) {
		_player.setOnCompletionListener(listener);
	}

}
