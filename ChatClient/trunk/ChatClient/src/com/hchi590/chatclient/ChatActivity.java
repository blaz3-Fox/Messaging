package com.hchi590.chatclient;

import static com.hchi590.chatclient.Constants.DOWNLOAD;
import static com.hchi590.chatclient.Constants.FAILED_TO_CONNECT;
import static com.hchi590.chatclient.Constants.GCM_ID_REGISTERED;
import static com.hchi590.chatclient.Constants.GCM_MESSAGE_RECEIVED;
import static com.hchi590.chatclient.Constants.MESSAGE_UPDATE;
import static com.hchi590.chatclient.Constants.POST;
import static com.hchi590.chatclient.Constants.SENDER_ID;
import static com.hchi590.chatclient.Constants.STATE_MESSAGES;
import static com.hchi590.chatclient.Constants.FILE_DOWNLOADED;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.hchi590.chatclient.audio.DownloadMediaPlayer;
import com.hchi590.chatclient.audio.RecordAudioBase;
import com.hchi590.chatclient.audio.RecordMediaRecorder;
import com.hchi590.chatclient.audio.StreamMediaPlayer;

public class ChatActivity extends Activity {

	private File _tempDir = new File(Environment.getExternalStorageDirectory()
			+ "/chatTemp");

	private String _name = "c";

	private ArrayList<Message> _messages;
	private MessageAdapter _adapter;
	private ArrayList<String> _files;
	private File _selectedFile;

	private Handler _handler;

	private EditText _editText;
	private ListView _listView;
	private Spinner _uploadSpinner;
	private ArrayAdapter<String> _uploadAdapter;
	private Button _sendButton;
	private Button _uploadButton;

	private ChatActivity _self = this;
	private RecordAudioBase _recorder = new RecordMediaRecorder(
			_tempDir.getAbsolutePath() + "/test.3gp");
	private boolean _isRecording = false;

	private int _recordCount = 0;

	private String _regID;

	private boolean _retrieving;
	private boolean _streamingModeOn = true;

	private ExecutorService _sendPool;
	private ExecutorService _streamingPool;
	private ExecutorService _retrievePool;
	private ExecutorService _postPool;
	private ExecutorService _playPool;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			_messages = (ArrayList<Message>) savedInstanceState
					.get(STATE_MESSAGES);
		} else {
			_messages = new ArrayList<Message>();
		}
		setContentView(R.layout.activity_chat);
		initialise();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_chat, menu);
		return true;
	}

	/**
	 * Initialise controls and variables, attempt to retrieve server to contact
	 * from naming server instance.
	 */
	private void initialise() {

		// Set controls from resource file
		_editText = (EditText) this.findViewById(R.id.edit_message);
		_listView = (ListView) this.findViewById(R.id.list_view);
		_uploadSpinner = (Spinner) this.findViewById(R.id.file_spinner);
		_sendButton = (Button) this.findViewById(R.id.send_text_button);
		_uploadButton = (Button) this.findViewById(R.id.upload_file_button);

		// Disable send and upload buttons until GCM id is registered with
		// server
		_sendButton.setEnabled(false);
		_uploadButton.setEnabled(false);

		_messages = new ArrayList<Message>();
		_files = new ArrayList<String>();

		// Initialise spinner and set onItemSelectedListener for recorded sound
		// files
		_uploadAdapter = new ArrayAdapter<String>(_self,
				android.R.layout.simple_spinner_item, _files);
		_uploadAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		_uploadSpinner.setAdapter(_uploadAdapter);
		_uploadSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				_selectedFile = new File(_tempDir.getAbsolutePath() + "/"
						+ (String) parent.getItemAtPosition(pos));
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}

		});

		// Set custom ArrayAdapter for message list in activity window
		_adapter = new MessageAdapter(this, R.layout.row_self, _messages);
		_listView.setAdapter(_adapter);

		// Register app with GCM servers and register receivers for various
		// broadcast events
		registerGCM();
		registerReceiver(messageReceiver,
				new IntentFilter(GCM_MESSAGE_RECEIVED));
		registerReceiver(gcmRegisterReceiver, new IntentFilter(
				GCM_ID_REGISTERED));
		registerReceiver(messageUpdateReceiver,
				new IntentFilter(MESSAGE_UPDATE));
		registerReceiver(failToConnectReceiver, new IntentFilter(
				FAILED_TO_CONNECT));
		registerReceiver(downloadReceiver, new IntentFilter(FILE_DOWNLOADED));

		_handler = new Handler();

		// Initialise threadpools for execution of network tasks
		_sendPool = Executors.newCachedThreadPool();
		_postPool = Executors.newSingleThreadExecutor();
		_streamingPool = Executors.newCachedThreadPool();
		_retrievePool = Executors.newSingleThreadExecutor();
		_playPool = Executors.newCachedThreadPool();

		// Attempt to contact naming server to be assigned a ChatServer to
		// connect to
		GetServer getServer = new GetServer();
		getServer.start();
		synchronized (getServer) {
			try {
				getServer.wait(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		_retrieving = false;

		// Register GCM id with assigned server
		ToServer registerToServer = new RegisterToServer(_self, _regID);
		_sendPool.execute(registerToServer);

		// Retrieve any existing messages on server
		if (!_retrieving) {
			_retrieving = true;
			ToServer retrieveTextRequest = new RetrieveMessageToServer(_self,
					_messages.size() - 1);
			_retrievePool.execute(retrieveTextRequest);
		}

		Log.d("name", _name);

		// Create temporary directory to store recorded sound files
		_tempDir.mkdirs();
	}

	/**
	 * Register this app with the GCM servers.
	 */
	private void registerGCM() {
		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);
		_regID = GCMRegistrar.getRegistrationId(this);
		if (_regID.equals("")) {
			GCMRegistrar.register(getApplicationContext(), SENDER_ID);
			_regID = GCMRegistrar.getRegistrationId(this);
		} else {
			Log.v("Error", "Already registered");
		}
	}

	/**
	 * OnClickListener for "Send" Button. Posts the contents of the text entry
	 * to the server. Messages are sent using a single thread executor to
	 * prevent messages from being sent out of order.
	 * 
	 * @param v
	 *            Button that was clicked
	 */
	public void sendMessage(View v) {

		String text = _editText.getText().toString();
		if (!text.equals("")) {
			ToServer postToServer = new PostToServer(_self, new Message(text,
					_name, System.currentTimeMillis()));
			_sendPool.execute(postToServer);
		}
		_editText.setText("");

	}

	/**
	 * OnClickListener for "Upload" button. Uploads the selected file in the
	 * adjoining spinner to the server.
	 * 
	 * @param v
	 *            Button that was clicked
	 */
	public void uploadFile(View v) {
		Log.d("file", _selectedFile.getAbsolutePath());
		Log.d("file", "Attempting to get file");
		if (_recordCount != 0) {
			ToServer uploadFileToServer = new UploadFileToServer(_self,
					_selectedFile);
			_sendPool.execute(uploadFileToServer);
		}
	}

	/**
	 * BroadcastReceiver that handles Intent sent out upon completion of a
	 * download.
	 */
	private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String filename = intent.getStringExtra("file");
			Log.d("downloaded", filename);
			_playPool.execute(new DownloadMediaPlayer(filename));
		}

	};

	/**
	 * BroadcastReceiver that handles the the Intent sent out upon receiving a
	 * GCM push notification from the server. Gets GCM message bundled with
	 * intent from server and handles according to the type of push sent.
	 */
	private BroadcastReceiver messageReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String header = intent.getExtras().getString("header");
			String index = intent.getExtras().getString("data");
			long time = intent.getLongExtra("time", 0);
			// Retrieve any new messages from the server if notification
			// indicated a new message.
			if (header.equals(POST)) {
				Log.d("retrieve", ("Index to retrieve up to: " + index));
				// Retrieve only if the app is not already attempting to
				// retrieve messages.
				if (!_retrieving) {
					_retrieving = true;
					ToServer retrieveTextRequest = new RetrieveMessageToServer(
							_self, _messages.size() - 1);
					_retrievePool.execute(retrieveTextRequest);
				}
				// Handle the notification of a new file being uploaded to the
				// server.
			} else if (header.equals(DOWNLOAD)) {
				// Attempt to stream the file immediately.
				if (_streamingModeOn) {
					_streamingPool.execute(new StreamMediaPlayer(index));
				} else {
					_sendPool.execute(new DownloadFileToServer(context, index, _tempDir.getAbsolutePath()));
				}
			}
		}

	};

	/**
	 * BroadcastReceiver that handles the Intent sent out upon having
	 * successfully registered the GCM id with the chosen ChatServer.
	 */
	private BroadcastReceiver gcmRegisterReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			_name = intent.getExtras().getString("name");
			_recorder.setFile(_tempDir.getAbsolutePath() + "/" + _name
					+ "_test" + _recordCount + ".3gp");
			Log.d("registration", _name);
			_adapter.setName(_name);
			// Displays a toast to the user indicating a successful connection
			Toast.makeText(
					getApplicationContext(),
					"Connected to: "
							+ ServerAddress.getInstance().getServerAddress(),
					Toast.LENGTH_LONG).show();
			_sendButton.setEnabled(true);
		}

	};

	/**
	 * BroadcastReceiver that handles the Intent sent out upon having completed
	 * the retrieval of new message from the server. Adds new messages to
	 * messages list and notifies adapters of change to underlying data
	 * structure.
	 */
	private BroadcastReceiver messageUpdateReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			ArrayList<Message> messages = (ArrayList<Message>) intent
					.getSerializableExtra("messages");
			if (!messages.isEmpty()) {
				synchronized (_messages) {
					_messages.addAll(messages);
					Long time = System.currentTimeMillis()
							- messages.get(messages.size() - 1).getTime();
					Log.d("timing", messages.get(messages.size() - 1)
							.getMessage() + ":Message retrieved: " + time);
				}
			}
			_retrieving = false;
			updateUI();
		}

	};

	/**
	 * BroadcastReceiver that handles the Intent sent out upon having failed to
	 * connect to the chosen server.
	 */
	private BroadcastReceiver failToConnectReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String error = intent.getExtras().getString("message");
			Log.d("fail", "failed to connect");
			// Display a toast to the user indicating failure to connect.
			Toast.makeText(_self, error, Toast.LENGTH_LONG).show();
			_retrieving = false;
		}

	};

	/**
	 * Listener for selection of items of the menu.
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// Test a large send of consecutive messages.
		case R.id.large_send:
			for (int i = 0; i < 30; i++) {
				ToServer postToServer = new PostToServer(this, new Message(
						Integer.toString(i), _name, System.currentTimeMillis()));
				_postPool.execute(postToServer);

				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
			return true;
			// Bring up a dialog to allow the user to choose a specify a server
			// to connect to. Registers GCM id with the new server.
		case R.id.retry_connect:
			View serverDialogView = LayoutInflater.from(_self).inflate(
					R.layout.server_select_dialog, null);
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					_self);
			alertDialogBuilder.setView(serverDialogView);
			final EditText serverAddressInput = (EditText) serverDialogView
					.findViewById(R.id.server_address_editText);
			serverAddressInput.setText(ServerAddress.getInstance()
					.getServerAddress());
			final EditText serverPortInput = (EditText) serverDialogView
					.findViewById(R.id.server_port_editText);
			serverPortInput.setText(Integer.toString(ServerAddress
					.getInstance().getPort()));
			alertDialogBuilder
					.setTitle(R.string.server_dialog_title)
					.setPositiveButton(R.string.button_ok,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									String addressText = serverAddressInput
											.getText().toString();
									if (!addressText.equals("")) {
										ServerAddress.getInstance()
												.setServerAddress(addressText);
									}
									String portText = serverPortInput.getText()
											.toString();
									if (!portText.equals("")) {
										ServerAddress.getInstance().setPort(
												Integer.parseInt(portText));
									}
									ToServer registerToServer = new RegisterToServer(
											_self, _regID);
									_sendPool.execute(registerToServer);
									if (!_retrieving) {
										_retrieving = true;
										ToServer retrieveTextRequest = new RetrieveMessageToServer(
												_self, _messages.size() - 1);
										_retrievePool
												.execute(retrieveTextRequest);
									}
								}
							})
					.setNegativeButton(R.string.button_cancel,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();
								}
							});
			AlertDialog serverDialog = alertDialogBuilder.create();
			serverDialog.show();
			return true;
			// Record audio from microphone. Saves it to temporary folder.
		case R.id.record_audio:
			_recorder.toggleRecording(_isRecording);
			if (!_isRecording) {
				item.setTitle(R.string.menu_stop_record_audio);
			} else {
				item.setTitle(R.string.menu_start_record_audio);
				_files.add(_name + "_test" + _recordCount + ".3gp");
				_uploadAdapter.notifyDataSetChanged();
				_uploadSpinner.setSelection(_recordCount);
				_uploadButton.setEnabled(true);
				_recordCount++;
				_recorder.setFile(_tempDir.getAbsolutePath() + "/" + _name
						+ "_test" + _recordCount + ".3gp");
			}
			_isRecording = !_isRecording;
			return true;
			/*
			 * case R.id.play_audio: _player.togglePlay(_isPlaying); if
			 * (!_isPlaying) { item.setTitle(R.string.menu_stop_play_audio); }
			 * else { item.setTitle(R.string.menu_start_play_audio); }
			 * _isPlaying = !_isPlaying; return true;
			 */
		case R.id.media_mode:
			if (_streamingModeOn) {
				item.setTitle(R.string.menu_stream_upon_notification);
			} else {
				item.setTitle(R.string.menu_download_upon_notification);
			}
			_streamingModeOn = !_streamingModeOn;
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Posts a task to the handler to update the UI elements.
	 */
	public void updateUI() {
		_handler.post(new UpdateMessage());
	}

	/**
	 * Inform MessageAdapter that the messages list has been updated.
	 * 
	 * @author hchi590
	 * 
	 */
	private class UpdateMessage implements Runnable {

		@Override
		public void run() {
			_adapter.notifyDataSetChanged();
		}

	}

	/**
	 * Attempt to save messages for restoration if device is rotated. NOTE:
	 * rotation has been disabled. Check manifest.
	 */
	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putSerializable(STATE_MESSAGES, _messages);
		super.onSaveInstanceState(savedInstanceState);
	}

	/**
	 * Cleanup receivers and thread pools upon exit.
	 */
	public void onDestroy() {

		// Attempt to remove GCM id from the server.
		ToServer unregisterGCM = new UnregisterToServer(_self, _regID);
		unregisterGCM.start();
		synchronized (unregisterGCM) {
			try {
				unregisterGCM.wait(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// Unregister BroadcastReceivers and GCM receiver
		unregisterReceiver(messageReceiver);
		unregisterReceiver(gcmRegisterReceiver);
		unregisterReceiver(messageUpdateReceiver);
		unregisterReceiver(failToConnectReceiver);
		unregisterReceiver(downloadReceiver);
		GCMRegistrar.onDestroy(this);

		// Delete created sound files and temp folder
		for (File f : _tempDir.listFiles()) {
			f.delete();
		}
		_tempDir.delete();

		// Call for a shutdown of all thread pools
		_postPool.shutdownNow();
		_sendPool.shutdownNow();
		_streamingPool.shutdownNow();
		_retrievePool.shutdownNow();
		_playPool.shutdownNow();

		super.onDestroy();
	}

}
