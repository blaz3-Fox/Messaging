/**
 * 
 */
package com.hchi590.chatclient.audio;


/**
 * @author hchi590 Base class for playing media from the ChatServer.
 * 
 */
public abstract class PlayAudioBase extends Thread {

	protected String _filename;
	protected boolean _isPlaying = false;

	public PlayAudioBase() {

	}

	/**
	 * 
	 * @param filename Name of output file
	 */
	public PlayAudioBase(String filename) {
		_filename = filename;
	}

	/**
	 * Set output file
	 * @param filename Name of output file
	 */
	public void setFile(String filename) {
		_filename = filename;
	}

	public void run() {
		_isPlaying = true;
		startPlaying();
		//Watch for interruptions to thread. Stop streaming if thread is interrupted.
		while (_isPlaying) {

			if (Thread.currentThread().isInterrupted()) {
				//stopStreaming();
				break;
			} else {
			}
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				break;
			}
		}
		stopPlaying();
	}

	protected abstract boolean startPlaying();

	public abstract boolean stopPlaying();
}
