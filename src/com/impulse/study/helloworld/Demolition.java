package com.impulse.study.helloworld;

import android.widget.Toast;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.background.modifier.ColorBackgroundModifier;
import org.andengine.entity.shape.IAreaShape;
import org.andengine.entity.shape.Shape;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.input.sensor.acceleration.AccelerationData;
import org.andengine.input.sensor.acceleration.IAccelerationListener;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.Texture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;

/**
 * Created with IntelliJ IDEA.
 * User: zhaohaifeng
 * Date: 13-1-26
 * Time: 下午8:21
 * To change this template use File | Settings | File Templates.
 */
public class Demolition extends SimpleBaseGameActivity implements IAccelerationListener, IOnSceneTouchListener {

    private static final int CAMERA_WIDTH = 480;
    private static final int CAMERA_HEIGHT = 320;

    private static final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f);
    private static final int MAX_BODIES = 50;

    private BitmapTextureAtlas mBitmapTextureAtlas;
    private TextureRegion mTStoneTextureRegion;
    private TextureRegion mMatHeadTextureRegion;
    private PhysicsWorld mPhysicsWorld;
    private int mBodyCount = 0;

    @Override
    public void onAccelerationAccuracyChanged(AccelerationData pAccelerationData) {

    }

    @Override
    public void onAccelerationChanged(AccelerationData pAccelerationData) {
        final Vector2 gravity = Vector2Pool.obtain(pAccelerationData.getX(), pAccelerationData.getY());
        mPhysicsWorld.setGravity(gravity);
        Vector2Pool.recycle(gravity);
    }

    @Override
    public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
        if (mPhysicsWorld != null) {
            if (pSceneTouchEvent.isActionDown()) {
                addBody(pSceneTouchEvent.getX(), pSceneTouchEvent.getY());
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onCreateResources() {
        mBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 64, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/demo/");
        mTStoneTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlas, this.getApplicationContext(), "face_box.png", 0, 0);
        mMatHeadTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mBitmapTextureAtlas, this.getApplicationContext(), "face_circle_tiled.png", 0, 0);
        this.getTextureManager().loadTexture(mBitmapTextureAtlas);
        this.enableAccelerationSensor(this);
    }

    @Override
    protected Scene onCreateScene() {
        this.mEngine.registerUpdateHandler(new FPSLogger());

        final Scene scene = new Scene();
        scene.setBackground(new Background(0, 0, 0));
        scene.setOnSceneTouchListener(this);


        final IAreaShape ground = new Rectangle(0, CAMERA_HEIGHT - 2, CAMERA_WIDTH, 2,this.getVertexBufferObjectManager());
        final IAreaShape roof = new Rectangle(0, 0, CAMERA_WIDTH, 2, this.getVertexBufferObjectManager());
        final IAreaShape left = new Rectangle(0, 0, 2, CAMERA_HEIGHT, this.getVertexBufferObjectManager());
        final IAreaShape right = new Rectangle(CAMERA_WIDTH - 2, 0, 2, CAMERA_HEIGHT, this.getVertexBufferObjectManager());

        this.mPhysicsWorld = new PhysicsWorld(new Vector2(1f,0f),true);
        final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);
        PhysicsFactory.createBoxBody(this.mPhysicsWorld, ground, BodyDef.BodyType.StaticBody, wallFixtureDef);
        PhysicsFactory.createBoxBody(this.mPhysicsWorld, roof, BodyDef.BodyType.StaticBody, wallFixtureDef);
        PhysicsFactory.createBoxBody(mPhysicsWorld, left, BodyDef.BodyType.StaticBody, wallFixtureDef);
        PhysicsFactory.createBoxBody(this.mPhysicsWorld, right, BodyDef.BodyType.StaticBody, wallFixtureDef);

        scene.attachChild(ground);
        scene.attachChild(roof);
        scene.attachChild(left);
        scene.attachChild(right);
        scene.registerUpdateHandler(mPhysicsWorld);
        return scene;
    }

    @Override
    public EngineOptions onCreateEngineOptions() {
        Toast.makeText(this, "Touch the screen to add objects", Toast.LENGTH_LONG).show();
        final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
        return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new FillResolutionPolicy(), camera);
    }

    private void addBody(float x, float y) {
        final Scene scene = this.mEngine.getScene();
        if (mBodyCount >= MAX_BODIES) {
            return;
        }
        mBodyCount ++;
        final Sprite matSprite;
        final Body body;

        if (mBodyCount % 2 == 0) {
            matSprite = new Sprite(x, y, mTStoneTextureRegion,this.getVertexBufferObjectManager());
            body = PhysicsFactory.createBoxBody(mPhysicsWorld, matSprite, BodyDef.BodyType.DynamicBody, FIXTURE_DEF);
        }else {
            matSprite = new Sprite(x, y, mMatHeadTextureRegion, this.getVertexBufferObjectManager());
            body = PhysicsFactory.createBoxBody(this.mPhysicsWorld, matSprite, BodyDef.BodyType.DynamicBody, FIXTURE_DEF);
        }
        scene.attachChild(matSprite);
        mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(matSprite,body,true,true));
    }
}
