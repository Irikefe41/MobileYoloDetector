package com.example.object_detector;

import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.renderscript.RenderScript;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Map;

import io.flutter.app.FlutterActivity;
import io.flutter.plugins.GeneratedPluginRegistrant;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

public class MainActivity extends FlutterActivity {
  
  private static final String CHANNEL = "IrikefeML/yoloAPP";
  private static YoloDetector detector;
  private static boolean modalLoaded = false;
  private RenderScript rs;

    @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    GeneratedPluginRegistrant.registerWith(this);
    rs = RenderScript.create(this);
    new MethodChannel(getFlutterView(), CHANNEL).setMethodCallHandler(
        new MethodCallHandler() {
          @Override
          public void onMethodCall(MethodCall call, Result result) {
            if (call.method.equals("loadModel")) {
                String modalPath = call.argument("modal_path");
                Map metaData = call.argument("meta_data");
               loadModel(modalPath,metaData,result);
            } 
          }
    });
  }

  protected void loadModel(final String modalPath, final Map metaData, final Result result) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    String modalPathKey = getFlutterView().getLookupKeyForAsset(modalPath);
                    ByteBuffer modalData = loadModalFile(getApplicationContext().getAssets().openFd(modalPathKey));
                    detector = new YoloDetector(rs,modalData, metaData);
                    modalLoaded=true;
                    result.success("Modal Loaded Sucessfully");
                } catch (Exception e) {
                    e.printStackTrace();
                    result.error("Modal failed to loaded", e.getMessage(), null);
                }
            }
        }).start();
  }

  public ByteBuffer loadModalFile(AssetFileDescriptor fileDescriptor) throws IOException {
      FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
      FileChannel fileChannel = inputStream.getChannel();
      long startOffset = fileDescriptor.getStartOffset();
      long declaredLength = fileDescriptor.getDeclaredLength();
      return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
  }

}