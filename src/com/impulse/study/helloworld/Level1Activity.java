/**
 * 
 */
package com.impulse.study.helloworld;

import java.util.Arrays;
import java.util.Random;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.modifier.AlphaModifier;
import org.anddev.andengine.entity.modifier.MoveModifier;
import org.anddev.andengine.entity.modifier.MoveYModifier;
import org.anddev.andengine.entity.modifier.ParallelEntityModifier;
import org.anddev.andengine.entity.modifier.RotationModifier;
import org.anddev.andengine.entity.modifier.ScaleModifier;
import org.anddev.andengine.entity.modifier.SequenceEntityModifier;
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
import org.anddev.andengine.util.modifier.ease.EaseQuadOut;

import android.os.Handler;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.anddev.andengine.ui.IGameInterface#onLoadEngine()
	 */
	public Engine onLoadEngine() {
		this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		return new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE,
				new FillResolutionPolicy(), this.mCamera));
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
		final Sprite bullet = new Sprite(20.0f, CAMERA_HEIGHT - 50f,
				mBulletTextureRegion){
			@Override
			//pTouchAreaLocalX，pTouchAreaLocalY在这个sprite中的相当位置（左上角为（0，0））
			public boolean onAreaTouched(final TouchEvent pAreaTouchEvent,final float pTouchAreaLocalX,final float pTouchAreaLocalY){
				switch(pAreaTouchEvent.getAction()){
				case TouchEvent.ACTION_DOWN:
					Toast.makeText(Level1Activity.this, "Sprite touch Down", Toast.LENGTH_SHORT).show();
					break;
				case TouchEvent.ACTION_UP:
					Toast.makeText(Level1Activity.this, "Sprite touch UP", Toast.LENGTH_SHORT).show();
					break;
				case TouchEvent.ACTION_MOVE:
					this.setPosition(pAreaTouchEvent.getX()-this.getWidth()/2, pAreaTouchEvent.getY()-this.getHeight()/2);
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
				CAMERA_HEIGHT - 100.0f, mCrossTextureRegion){
			@Override
			public boolean onAreaTouched(final TouchEvent pAreaTouchEvent,final float pTouchAreaLocalX,final float pTouchAreaLocalY){
				switch(pAreaTouchEvent.getAction()){
				case TouchEvent.ACTION_DOWN:
					Toast.makeText(Level1Activity.this, "Sprite touch Down", Toast.LENGTH_SHORT).show();
					break;
				case TouchEvent.ACTION_UP:
					Toast.makeText(Level1Activity.this, "Sprite touch UP", Toast.LENGTH_SHORT).show();
					break;
				case TouchEvent.ACTION_MOVE:
					this.setPosition(pAreaTouchEvent.getX()-this.getWidth()/2, pAreaTouchEvent.getY()-this.getHeight()/2);
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
		final Sprite hatchet = new Sprite(cross.getInitialX() + 50.0f,
				CAMERA_HEIGHT - 100.0f, mHatchetTextureRegion){
			@Override
			public boolean onAreaTouched(final TouchEvent pAreaTouchEvent,final float pTouchAreaLocalX,final float pTouchAreaLocalY){
				switch(pAreaTouchEvent.getAction()){
				case TouchEvent.ACTION_DOWN:
					Toast.makeText(Level1Activity.this, "Sprite touch Down", Toast.LENGTH_SHORT).show();
					break;
				case TouchEvent.ACTION_UP:
					Toast.makeText(Level1Activity.this, "Sprite touch UP", Toast.LENGTH_SHORT).show();
					break;
				case TouchEvent.ACTION_MOVE:
					this.setPosition(pAreaTouchEvent.getX()-this.getWidth()/2, pAreaTouchEvent.getY()-this.getHeight()/2);
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
		//hatchet.registerEntityModifier(new AlphaModifier(15f, 0.0f, 1.0f));
		scene.getLastChild().attachChild(hatchet);
		scene.registerEntityModifier(new AlphaModifier(10, 0.0f, 1.0f));
		
		//注册触摸事件
		scene.setTouchAreaBindingEnabled(true);
		scene.registerTouchArea(bullet);
		scene.registerTouchArea(cross);
		scene.registerTouchArea(hatchet);

		nVamp = 0;
		mHandler.postDelayed(mStartVamp, 5000);
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
					mPigTiledTextureRegion.clone());
			final long[] frameDurations = new long[4];
			Arrays.fill(frameDurations, 500);
			asprVamp[i].animate(frameDurations, 4, 7, true);
			asprVamp[i].registerEntityModifier(new SequenceEntityModifier(
					new AlphaModifier(5f, 0f, 1f), new MoveModifier(60f,
							asprVamp[i].getX(), 30f, asprVamp[i].getY(),
							CAMERA_HEIGHT / 2)));
			
			scene.getLastChild().attachChild(asprVamp[i]);
			if(nVamp < 10){
				mHandler.postDelayed(mStartVamp, 5000);
			}
		}
	};

}
