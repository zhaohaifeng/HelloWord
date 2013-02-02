/**
 * 
 */
package com.impulse.study.helloworld;

import java.util.Random;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.extension.tmx.TMXLayer;
import org.andengine.extension.tmx.TMXLoader;
import org.andengine.extension.tmx.TMXLoader.ITMXTilePropertiesListener;
import org.andengine.extension.tmx.TMXProperties;
import org.andengine.extension.tmx.TMXTile;
import org.andengine.extension.tmx.TMXTileProperty;
import org.andengine.extension.tmx.TMXTiledMap;
import org.andengine.extension.tmx.util.exception.TMXLoadException;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.util.FPSLogger;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.debug.Debug;

import android.os.Handler;

/**
 * @author zhaohaifeng
 * 
 */
public class WAVAcitivity extends SimpleBaseGameActivity {

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

    @Override
    public EngineOptions onCreateEngineOptions() {
        mHandler = new Handler();
        gen = new Random();
        this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

        return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED,
                new FillResolutionPolicy(), this.mCamera);
    }

    @Override
    protected void onCreateResources() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected Scene onCreateScene() {
        this.mEngine.registerUpdateHandler(new FPSLogger());

        final Scene scene = new Scene();
        try {
            final TMXLoader tmxLoader = new TMXLoader(this.getAssets(),
                    this.mEngine.getTextureManager(),
                    TextureOptions.BILINEAR_PREMULTIPLYALPHA,
                    this.getVertexBufferObjectManager(),
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
            this.mWAVTMXMap = tmxLoader.loadFromAsset(
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
}
