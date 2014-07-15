package com.jforeach.mazeman;




import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class Game extends Activity {
	Maze maze;
	GameView view;
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		Intent intent = getIntent();
		   startService(new Intent(this,ConnectionService.class));
		Bundle extras = intent.getExtras();
		this.maze = (Maze)getLastNonConfigurationInstance();
		if(this.maze == null) {
			this.maze = (Maze)extras.get("maze");
		}
		view = new GameView(this);
		view.setMaze(this.maze);
		registerReceiver(receiver, new IntentFilter("myproject"));
		setContentView(view);
		
	}
	@Override
	protected void onResume() {
		super.onResume();
		
		//create a register   myproject is the same key used in connectionservice
		registerReceiver(receiver, new IntentFilter("myproject"));
	}
	
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();
			if (bundle!=null) {
				
				//extra data inserted into the fired intent
				String data = bundle.getString("data");
				Log.i("data in main class", data);
				
				
				if ("bottom".equalsIgnoreCase(data)) {
					view.onSwingb();
				}
				else if ("right".equalsIgnoreCase(data)) {
					view.onSwingr();
				}
				else if ("left".equalsIgnoreCase(data)) {
					view.onSwingl();
				}
				
				//Toast.makeText(getApplicationContext(), "Ok", Toast.LENGTH_SHORT).show();
			}else{
				Log.i("data in main class", "bundle null");
				//Toast.makeText(getApplicationContext(), "not", Toast.LENGTH_SHORT).show();
			}
			//handleResult(bundle);
		}

		
	};
	
	public Object onRetainNonConfigurationInstance() {
		return this.maze;
	}
}
