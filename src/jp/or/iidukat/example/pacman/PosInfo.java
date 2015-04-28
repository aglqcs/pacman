package jp.or.iidukat.example.pacman;

import java.io.Serializable;

public class PosInfo implements Serializable{
	private static final long serialVersionUID = -1560851305174716893L;
	public float x;
	public float y;
	public Direction d;
	public String player;
	public PosInfo(float x, float y, Direction d, String name){
		this.x = x;
		this.y = y;
		this.d = d;
		player = name;
	}

}
