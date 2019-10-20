
package francium.tech.objectdetector;

import android.renderscript.RenderScript;
import android.util.Log;
import org.json.JSONException;
import org.tensorflow.lite.Interpreter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import java.util.Map;

public class YoloDetector {

    private final Interpreter tfLite;
    RenderScript rs;

    private final List<Double> anchors;
    private ByteBuffer input;
    private int[] intValues;
    private float[][][][] output;

    private final int INP_IMG_WIDTH;
    private final int INP_IMG_HEIGHT;
    private final int NUM_BOXES_PER_BLOCK;
    private final int NUM_CLASSES;
    private final int gridWidth;
    private final int gridHeight;
    private final int blockSize;

    private final int MAX_RESULTS;
    private final double THRESHOLD;
    private final double OVERLAP_THRESHOLD;


    /** Tag for the {@link Log}. */
    private static final String TAG = "YoloDetector";

    YoloDetector(RenderScript rs, ByteBuffer modalData, Map meta) throws IOException, JSONException {

        tfLite = new Interpreter(modalData);

        /** Initialize Input Buffer based on meta as Byte Buffer**/
        blockSize = (int) meta.get("blockSize");

        Map net = (Map) meta.get("net");
        INP_IMG_WIDTH = (int) net.get("width");
        INP_IMG_HEIGHT = (int) net.get("height");
        NUM_CLASSES = (int) meta.get("classes");
        NUM_BOXES_PER_BLOCK = (int) meta.get("num");
        MAX_RESULTS = (int) meta.get("max_result");
        THRESHOLD= (double) meta.get("threshold");
        OVERLAP_THRESHOLD =  (double) meta.get("overlap_threshold");

        input = ByteBuffer.allocateDirect(
                        4 * (int) net.get("batch") * INP_IMG_WIDTH * INP_IMG_HEIGHT * (int) net.get("channels"));
        input.order(ByteOrder.nativeOrder());
        intValues = new int[INP_IMG_WIDTH * INP_IMG_HEIGHT];

        gridWidth = INP_IMG_WIDTH / blockSize;
        gridHeight = INP_IMG_HEIGHT / blockSize;
        System.out.println(gridWidth);
        System.out.println(gridHeight);
        List<Integer> outputDim = (List<Integer>) meta.get("out_size");
        output = new float[1][outputDim.get(0)][outputDim.get(1)][outputDim.get(2)];

        /** Get Meta Data for post processing **/
        anchors = (List<Double>) meta.get("anchors");
        this.rs=rs;
        Log.d(TAG, "Created a Tensorflow Lite Yolo Detector.");
    }

}