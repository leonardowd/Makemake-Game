package com.games.makemake;

import java.util.Iterator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

public class Makemake extends ApplicationAdapter {
	private SpriteBatch batch;
	Texture spaceShipSoundImg;
	Texture meteorImg;
	private Sound takeSound;
	private Music spaceShipSound;
	private Rectangle spaceShip;
	private Rectangle meteor;
	private OrthographicCamera camera;
	private char direction = 'R';
	private static final int meteor_WIDTH_HEIGHT = 32;
	private static final int SPACESHIP_WIDTH_HEIGHT = 44;
	
	private static final int VIEWPORT_WIDTH = 800;
	private static final int VIEWPORT_HEIGHT = 480;
	private long speed = 100;
	private int scoreboard = 0;
	
	//bitmap
	private BitmapFont scoreFont;
	private BitmapFont gameOverFont;
	
	//Adding the meteors
	private Array<Rectangle> meteors = new Array<Rectangle>();
	
	//Collision
	private int leftWall = 0;
	private int righttWall = VIEWPORT_WIDTH;
	private int topWall = 0;
	private int downWall = VIEWPORT_HEIGHT;
	private boolean wallColision = false;
		
	@Override
	public void create () {
		batch = new SpriteBatch();
		spaceShipSoundImg = new Texture(Gdx.files.internal("shipBeige_manned.png"));
		meteorImg = new Texture(Gdx.files.internal("deathtex3.png"));
		
		//create the camera
		camera = new OrthographicCamera();
		camera.setToOrtho(false, VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
		
		//bitmap font
		scoreFont = new BitmapFont(Gdx.files.internal("font.fnt"), Gdx.files.internal("font.png"), false);
		gameOverFont = new BitmapFont(Gdx.files.internal("gameOverFont.fnt"), Gdx.files.internal("gameOverFont.png"), false);
		
		//load the sounds
		takeSound = Gdx.audio.newSound(Gdx.files.internal("takeSound.wav"));
		spaceShipSound = Gdx.audio.newMusic(Gdx.files.internal("space_ship_floating_sound_1.mp3"));
		
		// start the background music immediately
		spaceShipSound.setLooping(true);
		spaceShipSound.play();
		
		//instantiate spaceShip and meteors
		spawnSpaceShip();	
		spawnmeteor();
	}

	@Override
	public void render () {
		ScreenUtils.clear(0, 0, 0, 1);
		camera.update();
		
		batch.begin();
		batch.draw(spaceShipSoundImg, spaceShip.x, spaceShip.y);

		for(Rectangle meteor : meteors) {
			batch.draw(meteorImg, meteor.x, meteor.y);
		}
		
		scoreFont.draw(batch, "Scoreboard: " + scoreboard, 10, 460);
		
		batch.end();
		
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			direction = 'R';
		}
		if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			direction = 'L';
		}
		if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
			direction = 'U';
		}
		if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
			direction = 'D';
		}
		
		switch (direction) {
		case 'R':
			moveSpaceShipToRight();
			break;
		case 'L':
			moveSpaceShipToLeft();
			break;
		case 'U':
			moveSpaceShipUp();
			break;
		case 'D':
			moveSpaceShipDown();
			break;
		default:
			break;
		}
		
		detectWallColision();
		
		for(Iterator<Rectangle> i = meteors.iterator(); i.hasNext();) {
			Rectangle meteor = i.next();
			
			if(meteor.overlaps(spaceShip)) {
				takeSound.play();
				i.remove();
				spawnmeteor();
				takeMeteor();
			}
		}
		
		gameOver();
		
	}
	
	private void spawnSpaceShip() {
		//create the SpaceShip
		spaceShip = new Rectangle();
		spaceShip.x = VIEWPORT_WIDTH /2 - SPACESHIP_WIDTH_HEIGHT /2;
		spaceShip.y = VIEWPORT_HEIGHT / 2 - SPACESHIP_WIDTH_HEIGHT /2;
		spaceShip.width = SPACESHIP_WIDTH_HEIGHT;
		spaceShip.height = SPACESHIP_WIDTH_HEIGHT;
	}

	private void spawnmeteor() {
		meteor = new Rectangle();
		meteor.x = MathUtils.random(0, VIEWPORT_WIDTH - meteor_WIDTH_HEIGHT);
		meteor.y = MathUtils.random(0, VIEWPORT_HEIGHT - meteor_WIDTH_HEIGHT);
		meteor.width = meteor_WIDTH_HEIGHT;
		meteor.height = meteor_WIDTH_HEIGHT;
		meteors.add(meteor);
	}
	
	private void takeMeteor() {
		switch (direction) {
		case 'R':
			moveSpaceShipToRight();
			break;
		case 'L':
			moveSpaceShipToLeft();
			break;
		case 'U':
			moveSpaceShipDown();
			break;
		case 'D':
			moveSpaceShipUp();
			break;
		default:
			break;
		}
		
		this.speed = increaseSpeed(speed);
		this.scoreboard = increaseScoreboard(scoreboard);
	}
	
	private void moveSpaceShipToRight() {
		spaceShip.x += speed * Gdx.graphics.getDeltaTime();
	}

	private void moveSpaceShipToLeft() {
		spaceShip.x -= speed * Gdx.graphics.getDeltaTime();
	}
	
	private void moveSpaceShipUp() {
		spaceShip.y += speed * Gdx.graphics.getDeltaTime();
	}

	private void moveSpaceShipDown() {
		spaceShip.y -= speed * Gdx.graphics.getDeltaTime();
	}
	
	private long increaseSpeed(long speed) {
		speed = this.speed;		
			if (speed < 300) {
				speed += 20;			
			} else if (speed < 500) {
				speed += 10;
			} else if (speed < 800) {
				speed += 5;
			} else {
				speed += 1;
			}
		return speed;
	}
	
	private int increaseScoreboard(int score) {
		if (this.speed < 300) {
			score += 5;
		} else if (this.speed < 500) {
			score += 10;
		} else {
			score += 15;
		}
		
		return score;
	}
	
	private void detectWallColision() {
		if (spaceShip.x <= leftWall || spaceShip.x >= righttWall 
				|| spaceShip.y <= topWall || spaceShip.y >= downWall) {
			this.wallColision = true;
		}
	}
	
	private void gameOver() {
		if (this.wallColision == true) {
			batch.begin();
			spaceShipSound.stop();
			ScreenUtils.clear(0, 0, 0, 1);
			scoreFont.draw(batch, "Scoreboard: " + scoreboard, VIEWPORT_WIDTH / 2 - 50, VIEWPORT_HEIGHT / 2 + 50);
			scoreFont.draw(batch, "Close the game and reopen if you wanna play again", VIEWPORT_WIDTH / 2 - 180, VIEWPORT_HEIGHT / 2);
			gameOverFont.draw(batch, "GAME OVER", VIEWPORT_WIDTH / 2 - 100, VIEWPORT_HEIGHT / 2 + 100);
			batch.end();
		}
	}
	
	@Override
	public void dispose () {
		spaceShipSoundImg.dispose();
		meteorImg.dispose();
		takeSound.dispose();
		spaceShipSound.dispose();
		batch.disableBlending();
	}
}