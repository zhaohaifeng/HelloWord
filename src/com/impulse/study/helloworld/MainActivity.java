package com.impulse.study.helloworld;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.text.ChangeableText;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Menu;

public class MainActivity extends BaseGameActivity {
    private static final int CAMERA_WIDTH = 320;
    private static final int CAMERA_HEIGHT = 480;
    private Camera andCamera;
    private Texture myFontTexture;
    private Font myFont;
    public void onLoadComplete() {
    }
    public Engine onLoadEngine() {
        // 构建摄像机
        this.andCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
        // 构建Engine，全屏显示，手机方向为竖屏，按比例拉伸
        return new Engine(new EngineOptions(true, ScreenOrientation.PORTRAIT,
                new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT),
                this.andCamera));
    }
    public void onLoadResources() {
        // 构建一个纹理用以显示文字
        this.myFontTexture = new Texture(256, 256, TextureOptions.DEFAULT);
        // 构建字体
        this.myFont = new Font(this.myFontTexture, Typeface.create(
                Typeface.DEFAULT, Typeface.BOLD), 32, true, Color.WHITE);
        // 注入相关纹理及字体
        this.mEngine.getTextureManager().loadTexture(this.myFontTexture);
        this.mEngine.getFontManager().loadFont(this.myFont);
    }
    public Scene onLoadScene() {
        // 构建场景，允许的最大Layer数量为1
        final Scene scene = new Scene(1);
        // 使用可以变更内容的ChangeableText显示FPS(它的父类Text不允许改变显示内容)，位置在15,5,
        // 字体为myFont中所规定的，最多允许显示5个字符(设置能显示几个字符，实际就能显示几个，
        // AndEngine不能自动扩充，不填以初始化时输入的字符数计算……)
        final ChangeableText text = new ChangeableText(5, 5, this.myFont,
                "0.0", 5);
        // 注册FPS监听
        this.mEngine.registerUpdateHandler(new FPSLogger() {
            protected void onHandleAverageDurationElapsed(final float pFPS) {
                super.onHandleAverageDurationElapsed(pFPS);
                // 传递内容到ChangeableText
                text.setText("" + pFPS);
            }
        });
        // 将ChangeableText注入场景
        scene.attachChild(text);
        // 构建场景，可容纳图层数为1
        return scene;
    }
}
