package jp.or.iidukat.example.pacman;

import java.util.ArrayList;

import wifiP2P.DeviceDetailFragment;
import Team22.DS.cmu.edu.Message;
import Team22.DS.cmu.edu.MessagePasser;
import Team22.DS.cmu.edu.Node;
import Team22.DS.cmu.edu.TimeStampType;
import android.app.Activity;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class GooglePacman extends Activity implements OnClickListener {

	private PacmanGame game;
	private GameView gameView;
	private MessagePasser mp;
	 public static boolean isOwner = false;
	    public static String ownerIP = null;
	String[] names = { "player1", "player2", "player3", "player4" };
	private String name;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()  
        .detectDiskReads().detectDiskWrites().detectNetwork()  
        .penaltyLog().build());  
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()  
        .detectLeakedSqlLiteObjects().detectLeakedClosableObjects()  
        .penaltyLog().penaltyDeath().build()); 

		Log.d("oncreate", "oncreate");
		System.out.println("reach here1");
		super.onCreate(savedInstanceState);
		System.out.println("reach here2");

		name = Configure();
		System.out.println("reach here3");
		//int a[] = new int[5];
		//a[10] = 3;
		PacmanGame game = initGame(names, getIndex(names, name));
		ReceiveTask t = new ReceiveTask(game, mp);
		Thread recThread = new Thread(t);
		recThread.start();
		initGameView(game);
		initMainView();
		
		// setVolumeControlStream(AudioManager.STREAM_MUSIC);
	}

	private int getIndex(String[] names, String name) {
		for (int i = 0; i < names.length; i++)
			if (names[i].compareTo(name) == 0)
				return i;
		Log.d("dumbError", "name not in names array");
		return -1;
	}
	private String Configure(){
		System.out.println("start configure");
		String my_name = isOwner ? "player1" : "player2";
	//	String ip1 = "128.237.164.115";
		String ip1 = "128.237.213.251";
		String ip2 = "128.237.217.160";
		String local_ip = DeviceDetailFragment.getLocalIpv4Address();
		String other_ip = local_ip.compareTo(ip1) == 0 ? ip2 : ip1;
		String owner_ip = isOwner ? local_ip : other_ip;
		String guest_ip = isOwner ? other_ip : local_ip;
		ArrayList<Node> nodes = new ArrayList<Node>();
		Node n = new Node();
		n.setName(names[0]);
		n.setIp(owner_ip);
		n.setPort(20000);
		nodes.add(n);
		n = new Node();
		n.setName(names[1]);
		n.setIp(guest_ip);
		n.setPort(20001);
		nodes.add(n);
		mp = new MessagePasser(nodes, names[0], TimeStampType.LOGICAL);
		Log.d("asd", "dasdsa");
		System.out.println("owner(player1) " + owner_ip);
		System.out.println("guest(player2) " + guest_ip);
		System.out.println("my name" + my_name);
		return my_name;
	}
	private String ConfigureMessagePasser(){
		ArrayList<Node> nodes = new ArrayList<Node>();
		if(isOwner){
			System.out.println("owner");
			Node n = new Node();
			n.setIp(ownerIP);
			n.setPort(20000);
			n.setName(names[0]);
			nodes.add(n);
			mp = new MessagePasser(nodes, names[0], TimeStampType.LOGICAL);
			Message init = mp.receive();
			String other_ip = (String)init.getData();
			n = new Node();
			n.setIp(other_ip);
			n.setPort(20001);
			n.setName(names[1]);
			mp.get_node_list().add(n);
			return names[0];
		}
		else{
			System.out.println("false");

			Node n = new Node();
			n.setIp(ownerIP);
			n.setPort(20000);
			n.setName(names[0]);
			nodes.add(n);
			n = new Node();
			String localip = DeviceDetailFragment.getLocalIpv4Address();
			n.setIp(localip);
			n.setPort(20001);
			n.setName(names[1]);
			nodes.add(n);
			System.out.println("false");

			mp = new MessagePasser(nodes, names[1], TimeStampType.LOGICAL);
			System.out.println("false");

			Message init = new Message("","guest_init",localip);
			System.out.println("false");
			mp.send(init);
			System.out.println("false");

			return names[1];
		}
	}
	/*private void ConfigureMessagePasser(String name) {
		ArrayList<Node> nodes = new ArrayList<Node>();
		Node n;
		n = new Node();
		n.setIp("10.0.2.2");
		n.setName("player1");
		n.setPort(13500);
		nodes.add(n);
		n = new Node();
		n.setIp("10.0.2.2");
		n.setName("player2");
		n.setPort(14500);
		nodes.add(n);
		n = new Node();
		n.setIp("10.0.2.2");
		n.setName("player3");
		n.setPort(16743);
		// nodes.add(n);
		n = new Node();
		n.setIp("10.0.2.2");
		n.setName("player4");
		n.setPort(18832);
		// nodes.add(n);
		mp = new MessagePasser(nodes, name, TimeStampType.LOGICAL);
	}*/

	@Override
	public void onResume() {
		super.onResume();
		game.resume();
	}

	@Override
	public void onPause() {
		game.pause();
		super.onPause();
	}

	private void initMainView() {
		setContentView(R.layout.game);
		View newGameButton = findViewById(R.id.new_game_button);

		newGameButton.setOnClickListener(this);

		View killScreenButton = findViewById(R.id.killscreen_button);
		killScreenButton.setOnClickListener(this);
		View exitButton = findViewById(R.id.exit_button);
		exitButton.setOnClickListener(this);
	}

	private void initGameView(PacmanGame game) {
		gameView = new GameView(this);
		game.view = gameView;
		gameView.game = game;
	}

	private PacmanGame initGame(String[] names, int thisIndex) {
		game = new PacmanGame(this, mp, names, thisIndex);
		game.init();
		return game;
	}

	private void transitionToGameView() {
		setContentView(gameView);
		gameView.setFocusable(true);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.new_game_button:
			transitionToGameView();
			game.startNewGame();
			break;
		case R.id.killscreen_button:
			transitionToGameView();
			game.showKillScreen();
			break;
		case R.id.exit_button:
			finish();
			break;
		}

	}

}