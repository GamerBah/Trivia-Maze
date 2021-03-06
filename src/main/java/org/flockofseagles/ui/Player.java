package org.flockofseagles.ui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.Serializable;

public class Player implements Serializable {

	protected int xVal, yVal;

	public Player(int x, int y) {
		this.xVal = x;
		this.yVal = y;
	}

	public void draw(Canvas canvas) {
		ImageView im = new ImageView();
		im.setImage(new Image("/images/pixelPlayer.png"));
		GraphicsContext gc = canvas.getGraphicsContext2D();
		canvas.setUserData("Player");
		gc.drawImage(im.getImage(), 0, 0, canvas.getWidth(), canvas.getHeight());
	}

}
