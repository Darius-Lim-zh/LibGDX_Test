package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class MyGame extends ApplicationAdapter {
	private OrthographicCamera camera;
	private SpriteBatch batch;
	//Texture img;
	private Texture dropImage;
	private Texture bucketImage;
	private Sound dropSound;
	private Music rainMusic;
	private Rectangle bucket;
	private Array<Rectangle> raindrops;
	private long lastDropTime;
	private BitmapFont font;
	
	@Override
	public void create () {
		// loads the droplet and bucket
		dropImage = new Texture(Gdx.files.internal("droplet.png"));
		bucketImage = new Texture(Gdx.files.internal("bucket.png"));

		// loads the drop sound effect and music
		dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
		rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));

		// Plays the music on loop
		rainMusic.setLooping(true);
		rainMusic.play();

		// Creates camera and spritebatch
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		batch = new SpriteBatch();

		// Creates rectagle to represent bucket
		bucket = new Rectangle();
		bucket.x = (800 / 2) - (64 / 2);
		bucket.y = 20;
		bucket.width = 64;
		bucket.height = 64;

		// Creates raindrops array and spawns first raindrop
		raindrops = new Array<Rectangle>();
		spawnRainDrop();
		//img = new Texture("badlogic.jpg");
	}

	private void spawnRainDrop(){
		Rectangle raindrop = new Rectangle();
		raindrop.x = MathUtils.random(0, 800-64);
		raindrop.y = 480;
		raindrop.width = 64;
		raindrop.height = 64;
		raindrops.add(raindrop);
		lastDropTime = TimeUtils.nanoTime();
	}

	@Override
	public void render () {
		// Clears the screen with a dark blue
		ScreenUtils.clear(0, 0, 0.2f, 1);

		// Updates the camera matrices
		camera.update();

		// Spritebatch renders in the coordinate specified by camera
		batch.setProjectionMatrix(camera.combined);

		// Begins a new batch and draws the bucket and all drops
		batch.begin();
		batch.draw(bucketImage, bucket.x, bucket.y);
		for(Rectangle raindrop: raindrops) {
			batch.draw(dropImage, raindrop.x, raindrop.y);
		}
		batch.end();

		// Processes user input
		// Mouse controls
		if(Gdx.input.isTouched()){
			Vector3 touchPos = new Vector3();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(),0);
			camera.unproject(touchPos);
			bucket.x = touchPos.x - (64/2);
		}
		// Keyboard controls
		if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
			bucket.x -= 200 * Gdx.graphics.getDeltaTime();
		}
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			bucket.x += 200 * Gdx.graphics.getDeltaTime();
		}

		// Ensures they stay within the screen
		if(bucket.x < 0) {
			bucket.x = 0;
		}
		if(bucket.x > 800 - 64) {
			bucket.x = 800 - 64;
		}

		// Spawns a new drop every second
		if(TimeUtils.nanoTime() - lastDropTime > 1000000000) {
			spawnRainDrop();
		}

		// Moves the raindrops down the screen
		for (Iterator<Rectangle> iter = raindrops.iterator(); iter.hasNext(); ) {
			Rectangle raindrop = iter.next();
			raindrop.y -= 200 * Gdx.graphics.getDeltaTime();
			// Removes raindrops that go below the screen
			if(raindrop.y + 64 < 0) {
				iter.remove();
			}
			// Plays the sound effect and remove raindrop
			if(raindrop.overlaps(bucket)){
				dropSound.play();
				iter.remove();
			}
		}
	}

	@Override
	public void dispose () {
		// dispose of all native resources
		batch.dispose();
		dropImage.dispose();
		bucketImage.dispose();
		dropSound.dispose();
		rainMusic.dispose();
		//img.dispose();
	}
}
