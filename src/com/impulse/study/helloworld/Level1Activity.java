/**
 * 
 */
package com.impulse.study.helloworld;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import org.anddev.andengine.audio.sound.Sound;
import org.anddev.andengine.audio.sound.SoundFactory;
import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.IEntity;
import org.anddev.andengine.entity.modifier.AlphaModifier;
import org.anddev.andengine.entity.modifier.IEntityModifier;
import org.anddev.andengine.entity.modifier.MoveModifier;
import org.anddev.andengine.entity.modifier.MoveXModifier;
import org.anddev.andengine.entity.modifier.MoveYModifier;
import org.anddev.andengine.entity.modifier.ParallelEntityModifier;
import org.anddev.andengine.entity.modifier.RotationAtModifier;
import org.anddev.andengine.entity.modifier.RotationModifier;
import org.anddev.andengine.entity.modifier.ScaleModifier;
import org.anddev.andengine.entity.modifier.SequenceEntityModifier;
import org.anddev.andengine.entity.particle.ParticleSystem;
import org.anddev.andengine.entity.particle.emitter.CircleParticleEmitter;
import org.anddev.andengine.entity.particle.initializer.AlphaInitializer;
import org.anddev.andengine.entity.particle.initializer.ColorInitializer;
import org.anddev.andengine.entity.particle.initializer.RotationInitializer;
import org.anddev.andengine.entity.particle.initializer.VelocityInitializer;
import org.anddev.andengine.entity.particle.modifier.ExpireModifier;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.texture.BuildableTexture;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.builder.BlackPawnTextureBuilder;
import org.anddev.andengine.opengl.texture.builder.ITextureBuilder.TextureSourcePackingException;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;
import org.anddev.andengine.util.Debug;
import org.anddev.andengine.util.modifier.IModifier;
import org.anddev.andengine.util.modifier.IModifier.IModifierListener;
import org.anddev.andengine.util.modifier.ease.EaseQuadOut;
import org.anddev.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;

import android.content.SharedPreferences;
import android.os.Handler;
import android.text.style.BulletSpan;
import android.widget.Toast;

/**
 * @author zhaohaifeng
 * 
 */
public class Level1Activity extends BaseGameActivity {

	private static final int CAMERA_WIDTH = 480;

	private static final int CAMERA_HEIGHT = 320;
	private String tag = "Level1Activity";

	protected Camera mCamera;
	protected Scene mMainScene;

	private Texture mLevel1BackTexture;
	private Texture mPigTexture;
	private BuildableTexture mObstacleBoxTexture;
	private TextureRegion mBoxTextureRegion;
	private TextureRegion mLevel1BackTextureRegion;
	private TextureRegion mBulletTextureRegion;
	private TextureRegion mCrossTextureRegion;
	private TextureRegion mHatchetTextureRegion;

	private TiledTextureRegion mPigTiledTextureRegion;

	private AnimatedSprite[] asprVamp = new AnimatedSprite[10];
	private int nVamp;
	Random gen;

	private Handler mHandler;

	private Texture mParticleTexture;
	private TextureRegion mParticleTextureRegion;
	private ParticleSystem particleSystem;
	private CircleParticleEmitter particleEmitter;

	private Sound mExploSound, mGunshotSound, mWhiffieSound;
	private SharedPreferences audioOptions;

	private Sprite bullet,hatchet;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.anddev.andengine.ui.IGameInterface#onLoadEngine()
	 */
	public Engine onLoadEngine() {

		audioOptions = getSharedPreferences("audio", MODE_PRIVATE);
		this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		return new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE,
				new FillResolutionPolicy(), this.mCamera).setNeedsSound(true));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.anddev.andengine.ui.IGameInterface#onLoadResources()
	 */
	public void onLoadResources() {
		mHandler = new Handler();
		gen = new Random();

		TextureRegionFactory.setAssetBasePath("gfx/Level1/");
		mLevel1BackTexture = new Texture(512, 512,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		mLevel1BackTextureRegion = TextureRegionFactory.createFromAsset(
				this.mLevel1BackTexture, this, "Level1Bk.png", 0, 0);
		mEngine.getTextureManager().loadTexture(this.mLevel1BackTexture);

		mObstacleBoxTexture = new BuildableTexture(512, 256,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		mBoxTextureRegion = TextureRegionFactory.createFromAsset(
				mObstacleBoxTexture, this, "Obstaclebox.png");
		mBulletTextureRegion = TextureRegionFactory.createFromAsset(
				mObstacleBoxTexture, this, "Bullet.png");
		mCrossTextureRegion = TextureRegionFactory.createFromAsset(
				mObstacleBoxTexture, this, "Cross.png");
		mHatchetTextureRegion = TextureRegionFactory.createFromAsset(
				mObstacleBoxTexture, this, "Hatchet.png");

		try {
			mObstacleBoxTexture.build(new BlackPawnTextureBuilder(2));
		} catch (TextureSourcePackingException e) {
			e.printStackTrace();
		}

		this.mEngine.getTextureManager().loadTexture(this.mObstacleBoxTexture);

		mPigTexture = new Texture(256, 256, TextureOptions.DEFAULT);

		mPigTiledTextureRegion = TextureRegionFactory.createTiledFromAsset(
				mPigTexture, this, "pig.png", 0, 0, 4, 4);
		mEngine.getTextureManager().loadTexture(this.mPigTexture);

		TextureRegionFactory.setAssetBasePath("gfx/particles/");
		mParticleTexture = new Texture(32, 32,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		mParticleTextureRegion = TextureRegionFactory.createFromAsset(
				this.mParticleTexture, this, "particle_fire.png", 0, 0);
		mEngine.getTextureManager().loadTexture(mParticleTexture);

		SoundFactory.setAssetBasePath("mfx/");
		try {
			this.mExploSound = SoundFactory.createSoundFromAsset(
					this.mEngine.getSoundManager(), getApplicationContext(),
					"exploSound.ogg");
			this.mGunshotSound = SoundFactory.createSoundFromAsset(
					this.mEngine.getSoundManager(), getApplicationContext(),
					"gunshot.ogg");
			this.mWhiffieSound = SoundFactory.createSoundFromAsset(
					this.getSoundManager(), getApplicationContext(),
					"whiffle.ogg");
		} catch (final IOException e) {
			Debug.e(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.anddev.andengine.ui.IGameInterface#onLoadScene()
	 */
	public Scene onLoadScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());
		final Scene scene = new Scene(1);

		final int centerX = (CAMERA_WIDTH - mLevel1BackTextureRegion.getWidth()) / 2;
		final int centerY = (CAMERA_HEIGHT - mLevel1BackTextureRegion
				.getHeight()) / 2;

		final Sprite background = new Sprite(centerX, centerY,
				mLevel1BackTextureRegion);
		scene.getLastChild().attachChild(background);
		final Sprite obstacleBox = new Sprite(20.0f,
				mBoxTextureRegion.getHeight() + 50f, mBoxTextureRegion);
		scene.getLastChild().attachChild(obstacleBox);
		bullet = new Sprite(20.0f, CAMERA_HEIGHT - 50f, mBulletTextureRegion) {
			@Override
			// pTouchAreaLocalX，pTouchAreaLocalY在这个sprite中的相当位置（左上角为（0，0））
			public boolean onAreaTouched(final TouchEvent pAreaTouchEvent,
					final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				switch (pAreaTouchEvent.getAction()) {
				case TouchEvent.ACTION_DOWN:
//					Toast.makeText(Level1Activity.this, "Sprite touch Down",
//							Toast.LENGTH_SHORT).show();
					break;
				case TouchEvent.ACTION_UP:
//					Toast.makeText(Level1Activity.this, "Sprite touch UP",
//							Toast.LENGTH_SHORT).show();
					fireBullet(pAreaTouchEvent.getX(), pAreaTouchEvent.getY());
					break;
				case TouchEvent.ACTION_MOVE:
					this.setPosition(pAreaTouchEvent.getX() - this.getWidth()
							/ 2, pAreaTouchEvent.getY() - this.getHeight() / 2);
					break;
				}
				return true;
			}
		};
		bullet.registerEntityModifier(new SequenceEntityModifier(
				new ParallelEntityModifier(new MoveYModifier(3, 0f,
						CAMERA_HEIGHT - 100f, EaseQuadOut.getInstance()),
						new AlphaModifier(3, 0f, 1f), new ScaleModifier(3,
								0.5f, 1.0f), new RotationModifier(3, 0, 360))));
		scene.getLastChild().attachChild(bullet);
		final Sprite cross = new Sprite(bullet.getInitialX() + 50f,
				CAMERA_HEIGHT - 100.0f, mCrossTextureRegion) {
			@Override
			public boolean onAreaTouched(final TouchEvent pAreaTouchEvent,
					final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				switch (pAreaTouchEvent.getAction()) {
				case TouchEvent.ACTION_DOWN:
//					Toast.makeText(Level1Activity.this, "Sprite touch Down",
//							Toast.LENGTH_SHORT).show();
					break;
				case TouchEvent.ACTION_UP:
//					Toast.makeText(Level1Activity.this, "Sprite touch UP",
//							Toast.LENGTH_SHORT).show();
					throwHatchet(pAreaTouchEvent.getX(), pAreaTouchEvent.getY());
					break;
				case TouchEvent.ACTION_MOVE:
					this.setPosition(pAreaTouchEvent.getX() - this.getWidth()
							/ 2, pAreaTouchEvent.getY() - this.getHeight() / 2);
					break;
				}
				return true;
			}
		};
		cross.registerEntityModifier(new SequenceEntityModifier(
				new ParallelEntityModifier(new MoveYModifier(4, 0.0f,
						CAMERA_HEIGHT - 100.0f, EaseQuadOut.getInstance()),
						new AlphaModifier(4, 0.0f, 1.0f), new ScaleModifier(4,
								0.f, 1.0f), new RotationModifier(2, 0, 360))));
		cross.registerEntityModifier(new AlphaModifier(10.0f, 0.0f, 1.0f));
		scene.getLastChild().attachChild(cross);
		hatchet = new Sprite(cross.getInitialX() + 50.0f,
				CAMERA_HEIGHT - 100.0f, mHatchetTextureRegion) {
			@Override
			public boolean onAreaTouched(final TouchEvent pAreaTouchEvent,
					final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				switch (pAreaTouchEvent.getAction()) {
				case TouchEvent.ACTION_DOWN:
					Toast.makeText(Level1Activity.this, "Sprite touch Down",
							Toast.LENGTH_SHORT).show();
					break;
				case TouchEvent.ACTION_UP:
					Toast.makeText(Level1Activity.this, "Sprite touch UP",
							Toast.LENGTH_SHORT).show();
					break;
				case TouchEvent.ACTION_MOVE:
					this.setPosition(pAreaTouchEvent.getX() - this.getWidth()
							/ 2, pAreaTouchEvent.getY() - this.getHeight() / 2);
					break;
				}
				return true;
			}
		};

		hatchet.registerEntityModifier(new SequenceEntityModifier(
				new ParallelEntityModifier(new MoveYModifier(5, 0.0f,
						CAMERA_HEIGHT - 100.0f, EaseQuadOut.getInstance()),
						new AlphaModifier(5, 0.0f, 1.0f), new ScaleModifier(5,
								0.5f, 1.0f)), new RotationModifier(2, 0, 360)));
		// hatchet.registerEntityModifier(new AlphaModifier(15f, 0.0f, 1.0f));
		scene.getLastChild().attachChild(hatchet);
		scene.registerEntityModifier(new AlphaModifier(10, 0.0f, 1.0f));

		// 注册触摸事件
		scene.setTouchAreaBindingEnabled(true);
		scene.registerTouchArea(bullet);
		scene.registerTouchArea(cross);
		scene.registerTouchArea(hatchet);

		nVamp = 0;
		mHandler.postDelayed(mStartVamp, 5000);

		particleEmitter = new CircleParticleEmitter(CAMERA_WIDTH * 0.5f,
				CAMERA_HEIGHT * 0.5f + 20, 40);
		particleSystem = new ParticleSystem(particleEmitter, 100, 100, 500,
				this.mParticleTextureRegion);

		particleSystem.addParticleInitializer(new ColorInitializer(1, 0, 0));
		particleSystem.addParticleInitializer(new AlphaInitializer(0));
		particleSystem.setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE);
		particleSystem.addParticleInitializer(new VelocityInitializer(-2, 2,
				-2, -2));
		particleSystem
				.addParticleInitializer(new RotationInitializer(0f, 360f));

		particleSystem
				.addParticleModifier(new org.anddev.andengine.entity.particle.modifier.ScaleModifier(
						1f, 2f, 0, 5));
		particleSystem
				.addParticleModifier(new org.anddev.andengine.entity.particle.modifier.ColorModifier(
						1, 1, 0, 0.5f, 0, 0, 0, 3));
		particleSystem
				.addParticleModifier(new org.anddev.andengine.entity.particle.modifier.ColorModifier(
						1, 1, 0.5f, 1, 0, 1, 2, 4));
		particleSystem
				.addParticleModifier(new org.anddev.andengine.entity.particle.modifier.AlphaModifier(
						0, 1, 0, 1));
		particleSystem
				.addParticleModifier(new org.anddev.andengine.entity.particle.modifier.AlphaModifier(
						1, 0, 3, 4));
		particleSystem.addParticleModifier(new ExpireModifier(2, 4));

		particleSystem.setParticlesSpawnEnabled(false);
		scene.getLastChild().attachChild(particleSystem);

		return scene;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.anddev.andengine.ui.IGameInterface#onLoadComplete()
	 */
	public void onLoadComplete() {
		// TODO Auto-generated method stub

	}

	private Runnable mStartVamp = new Runnable() {
		public void run() {
			int i = nVamp++;

			Scene scene = Level1Activity.this.mEngine.getScene();
			float startY = gen.nextFloat() * (CAMERA_HEIGHT - 50f);
			asprVamp[i] = new AnimatedSprite(CAMERA_WIDTH - 30f, startY,
					mPigTiledTextureRegion.clone()) {
				@Override
				public boolean onAreaTouched(final TouchEvent pAreaTouchEvent,
						final float pTouchAreaLocalX,
						final float pTouchAreaLocalY) {
					switch (pAreaTouchEvent.getAction()) {
					case TouchEvent.ACTION_DOWN:
						for (int j = 0; j < nVamp; j++) {
							if ((Math.abs(asprVamp[j].getX()
									+ (asprVamp[j].getWidth()) / 2)
									- pAreaTouchEvent.getX() < 10.0f)
									&& (Math.abs(asprVamp[j].getY()
											+ asprVamp[j].getHeight()
											- pAreaTouchEvent.getY()) < 10.0f)) {
								particleEmitter.setCenter(
										asprVamp[j].getX()
												+ (asprVamp[j].getWidth()) / 2,
										asprVamp[j].getY()
												+ asprVamp[j].getHeight() / 2);
								particleSystem.setParticlesSpawnEnabled(true);
								mHandler.postDelayed(mEndPESpawn, 3000);
								asprVamp[j].clearEntityModifiers();
								asprVamp[j]
										.registerEntityModifier(new AlphaModifier(
												1.0f, 1.0f, 0.0f));
								asprVamp[j].setPosition(CAMERA_WIDTH,
										gen.nextFloat() * CAMERA_HEIGHT);
								playSound(mExploSound);
								break;
							}
						}
					}
					return true;
				}
			};
			final long[] frameDurations = new long[4];
			Arrays.fill(frameDurations, 500);
			asprVamp[i].animate(frameDurations, 4, 7, true);
			asprVamp[i].registerEntityModifier(new SequenceEntityModifier(
					new AlphaModifier(5f, 0f, 1f), new MoveModifier(60f,
							asprVamp[i].getX(), 30f, asprVamp[i].getY(),
							CAMERA_HEIGHT / 2)));

			scene.getLastChild().attachChild(asprVamp[i]);
			if (nVamp < 10) {
				mHandler.postDelayed(mStartVamp, 5000);
			}
			scene.registerTouchArea(asprVamp[i]);
		}
	};

	private Runnable mEndPESpawn = new Runnable() {
		public void run() {
			particleSystem.setParticlesSpawnEnabled(false);
		}
	};

	@Override
	public void onPauseGame() {
		super.onPauseGame();
		mGunshotSound.pause();
		mExploSound.pause();
	}

	private void playSound(Sound mSound) {
		if (audioOptions.getBoolean("effectsOn", false)) {
			mSound.play();
		}
	}

	private void fireBullet(float pX, float pY) {
		this.bullet.registerEntityModifier(new SequenceEntityModifier(
				new IEntityModifierListener() {

					public void onModifierFinished(
							IModifier<IEntity> pModifier, IEntity pItem) {
						Level1Activity.this.runOnUiThread(new Runnable() {
							
							public void run() {
								bullet.setVisible(false);
								bullet.setPosition(0, 0);
							}
						});
					}
				}, new RotationModifier(0.5f, 0f, 90f), new MoveXModifier(0.5f,
						pX, CAMERA_WIDTH), new AlphaModifier(0.1f, 1.0f, 0f)));
		mHandler.postDelayed(mPlayGunshot, 500);
	}
	
	private void throwHatchet(float pX,float pY){
		hatchet.registerEntityModifier(new ParallelEntityModifier(new IEntityModifierListener() {
			
			public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
				Level1Activity.this.runOnUiThread(new Runnable() {
					
					public void run() {
						hatchet.setVisible(false);
						hatchet.setPosition(0, 0);
					}
				});
			}
		}, new RotationAtModifier(5f, 0f, 5f*360f, 20f, 20f),new MoveXModifier(5f, pX, CAMERA_WIDTH)));
		playSound(mWhiffieSound);
	}
	
	private Runnable mPlayGunshot = new Runnable() {
		
		public void run() {
			playSound(mGunshotSound);
		}
	};

}
