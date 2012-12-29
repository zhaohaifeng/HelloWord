/**
 * 
 */
package com.impulse.study.helloworld;

import java.util.Random;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXLayer;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXLoader;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXLoader.ITMXTilePropertiesListener;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXProperties;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXTile;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXTileProperty;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXTiledMap;
import org.anddev.andengine.entity.layer.tiled.tmx.util.exception.TMXLoadException;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.ui.activity.BaseGameActivity;
import org.anddev.andengine.util.Debug;

import android.os.Handler;

/**
 * @author zhaohaifeng
 * 
 */
public class WAVAcitivity extends BaseGameActivity {

	private static final int CAMERA_WIDTH = 480;
	private static final int CAMERA_HEIGHT = 320;
	private String tag = "WAVActivity";

	private Handler mHandler;
	protected Camera mCamera;
	protected Scene mMainScene;

	private TMXTiledMap mWAVTMXMap;
	private TMXLayer tmxLayer;
	private TMXTile tmxTile;

	private int[] barriers = new int[150];
	private int barrierPtr = 0;
	private int mBarrierGID = -1;
	private int mOpenBarrierGID = 32;

	private Random gen;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.anddev.andengine.ui.IGameInterface#onLoadEngine()
	 */
	public Engine onLoadEngine() {
		mHandler = new Handler();
		gen = new Random();
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
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.anddev.andengine.ui.IGameInterface#onLoadScene()
	 */
	public Scene onLoadScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		final Scene scene = new Scene(1);
		try {
			final TMXLoader tmxLoader = new TMXLoader(this,
					this.mEngine.getTextureManager(),
					TextureOptions.BILINEAR_PREMULTIPLYALPHA,
					new ITMXTilePropertiesListener() {
						public void onTMXTileWithPropertiesCreated(
								TMXTiledMap pTMXTiledMap,
								TMXLayer pTMXLayer,
								TMXTile pTMXTile,
								TMXProperties<TMXTileProperty> pTMXTileProperties) {
							if (pTMXTileProperties.containsTMXProperty(
									"barrier", "true")) {
								barriers[barrierPtr++] = pTMXTile.getTileRow()
										* 15 + pTMXTile.getTileColumn();
								if (mBarrierGID < 0) {
									mBarrierGID = pTMXTile.getGlobalTileID();
								}
							}
						}

					});
			this.mWAVTMXMap = tmxLoader.loadFromAsset(this,
					"gfx/WAV/WAVMap.tmx");
		} catch (final TMXLoadException e) {
			Debug.e(e);
		}
		tmxLayer = this.mWAVTMXMap.getTMXLayers().get(0);
		scene.getFirstChild().attachChild(tmxLayer);
		scene.setOnSceneTouchListener(new IOnSceneTouchListener() {

			public boolean onSceneTouchEvent(Scene pScene,
					TouchEvent pSceneTouchEvent) {
				switch (pSceneTouchEvent.getAction()) {
				case TouchEvent.ACTION_DOWN:
					tmxTile = tmxLayer.getTMXTileAt(pSceneTouchEvent.getX(),
							pSceneTouchEvent.getY());
					if ((tmxTile != null)
							&& tmxTile.getGlobalTileID() == mOpenBarrierGID) {
						tmxTile.setGlobalTileID(mWAVTMXMap, mBarrierGID);
					}
					break;
				}
				return true;
			}
		});
		
		mHandler.postDelayed(openBarrier, gen.nextInt(2000));
		return scene;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.anddev.andengine.ui.IGameInterface#onLoadComplete()
	 */
	public void onLoadComplete() {
		
	}
	
	private Runnable openBarrier = new Runnable() {
		
		public void run() {
			int operThis = gen.nextInt(barrierPtr);
			int tileRow = barriers[operThis]/15;
			int tileCol = barriers[operThis]%15;
			tmxTile = tmxLayer.getTMXTileAt(tileCol * 33 + 16, tileRow*33 + 16);
			tmxTile.setGlobalTileID(mWAVTMXMap, mOpenBarrierGID);
			mHandler.postDelayed(openBarrier, 4000);
		}
	};

}
