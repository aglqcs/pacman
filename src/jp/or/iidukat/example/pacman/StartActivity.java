package jp.or.iidukat.example.pacman;

import wifiP2P.PeerFinderActivity;
import jp.or.iidukat.example.pacman.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;

public class StartActivity extends Activity implements OnClickListener{
	public static boolean isHost;
	//pop-up dialog for user to choose whether or not to be a host
	//host can invite other people to game 
	//guest player need to wait for invitation to play
	@Override
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.main);
	        
			AlertDialog.Builder builder = new AlertDialog.Builder(StartActivity.this);
			builder.setTitle("Choose start mode: ")
				   .setMessage("Do you want to host a new game?\n **Yes: host a game and invite others to play\n"
					+ " No: enter as a guest and wait for others invitation**");
			builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		                public void onClick(DialogInterface dialog, int which) {
		                   //go to host mode, allow inviting
		                	isHost = true;
							 Intent myIntent = new Intent(((Dialog) dialog).getContext(), PeerFinderActivity.class);
						     startActivity(myIntent);    
		                }
					
			});
			builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
		        @Override
		        public void onClick(DialogInterface dialog, int which) {
		        	isHost = false;
		        	Intent myIntent = new Intent(((Dialog) dialog).getContext(), PeerFinderActivity.class);
				    startActivity(myIntent);    
		            //go to device list view and wait for invitations
		            //how to disable user from connecting other players
		        }
			});
			
			builder.show();
	 }
	@Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub
		
	}
    

}
