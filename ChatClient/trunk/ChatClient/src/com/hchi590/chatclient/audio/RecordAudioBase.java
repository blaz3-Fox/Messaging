/**
 * 
 */
package com.hchi590.chatclient.audio;

/**
 * @author hchi590 Base class for recording audio.
 *
 */
public abstract class RecordAudioBase {

	protected String _filename;
	
	public RecordAudioBase() {
		
	}
	
	/**
	 * 
	 * @param filename Name of output file.
	 */
	public RecordAudioBase(String filename) {
		_filename = filename;
	}
	
	/**
	 * Set the output file.
	 * @param filename Name of output file.
	 */
	public void setFile(String filename) {
		_filename = filename;
	}
	
	/**
	 * Stop or start recording.
	 * @param isRecording
	 * @return whether stopRecording or startRecording was successful.
	 */
	public boolean toggleRecording(boolean isRecording) {
		if (isRecording) {
			return stopRecording();
		} else {
			return startRecording();
		}
	}
	
	protected abstract boolean startRecording();
	
	protected abstract boolean stopRecording();
	
}
