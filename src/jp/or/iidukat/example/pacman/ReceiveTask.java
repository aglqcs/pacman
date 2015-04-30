package jp.or.iidukat.example.pacman;

import android.util.Log;
import Team22.DS.cmu.edu.Message;
import Team22.DS.cmu.edu.MessagePasser;

public class ReceiveTask implements Runnable {

	private PacmanGame game;
	private MessagePasser passer;

	public ReceiveTask(PacmanGame game, MessagePasser passer) {
		this.game = game;
		this.passer = passer;
	}

	@Override
	public void run() {
		System.out.println("start run (listening)");
		Message m;
		for (;;) {
			System.out.println("trying to get a message");
			m = passer.receive();
			Log.d("recTask", "message received");
			PosInfo pi = (PosInfo)m.getData();
			Log.d(m.getSource(), "x:"+pi.x+" y:"+pi.y+" d:"+pi.d);
			updatePos(m.getSource(), (PosInfo) m.getData());
		}
	}

	private void updatePos(String playerName, PosInfo coords) {
		game.updatePositions(playerName, coords);
	}

}
