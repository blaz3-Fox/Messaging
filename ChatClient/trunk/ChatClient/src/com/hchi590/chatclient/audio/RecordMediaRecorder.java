/**
 * 
 */
package com.hchi590.chatclient.audio;

import java.io.IOException;

import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

/**
 * @author hchi590
 * Concrete implementation of RecordAudio using the MediaRecorder class built into the Android platform.
 */
public class RecordMediaRecorder extends RecordAudioBase {

	private MediaRecorder _recorder;
	
	public RecordMediaRecorder() {
		super(Environment.getExternalStorageDirectory() + "/test.3gp");
	}
	
	/**
	 * 
	 * @param filename Name of output file.
	 */
	public RecordMediaRecorder(String filename) {
		super(filename);
	}
	
	/**
	 * Start recording. Instantiates a new instance of MediaRecorder. Attempts to prepare and start.
	 */
	@Override
	protected boolean startRecording() {
		_recorder = new MediaRecorder();
		
		//Set recorder to take audio from the microphone. Outputs to a .3gp file.
		_recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        _recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        _recorder.setOutputFile(_filename);
        _recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        
        try {
        	_recorder.prepare();
        } catch (IOException e) {
        	e.printStackTrace();
        	Log.e("error", "_recorder.prepare() failed");
        	return false;
        }
        
        _recorder.start();
		return true;
	}

	/**
	 * Stop recording and destroys the current MediaRecorder instance.
	 */
	@Override
	protected boolean stopRecording() {
		_recorder.stop();
        _recorder.release();
        _recorder = null;
		return true;
	}

}
