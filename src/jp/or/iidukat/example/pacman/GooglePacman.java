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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class GooglePacman extends Activity implements OnClickListener {

	private PacmanGame game;
	private GameView gameView;
	private MessagePasser mp;
	public static boolean isOwner;
	public static String OwnerIp;
	String[] names = { "player1", "player2", "player3", "player4" };
	private String name;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		name = ConfigureMessagePasser();
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
	
	private String ConfigureMessagePasser(){
		ArrayList<Node> nodes = new ArrayList<Node>();
		if(isOwner){
			Node n = new Node();
			n.setIp(OwnerIp);
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
			Node n = new Node();
			n.setIp(OwnerIp);
			n.setPort(20000);
			n.setName(names[0]);
			nodes.add(n);
			n = new Node();
			String localip = DeviceDetailFragment.getLocalIpv4Address();
			n.setIp(localip);
			n.setPort(20001);
			n.setName(names[1]);
			nodes.add(n);
			mp = new MessagePasser(nodes, names[1], TimeStampType.LOGICAL);
			Message init = new Message("","guest_init",localip);
			mp.send(init);
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
		setContentView(R.layout.main);

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