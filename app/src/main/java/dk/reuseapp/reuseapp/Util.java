package dk.reuseapp.reuseapp;

import android.hardware.Camera;
import android.os.Environment;

import java.io.File;

public class Util {
    public static Camera getCamera(int id) {
        Camera camera = null;
        try {
            camera = Camera.open();
        }
        catch (Exception e) {
            //TODO: figure out the flow for this case (could be that camera is used elsewhere
        }
        return camera;
    }

    public static File getOutPutMediaFile() {

        File tempFolder = new File(Environment.getExternalStorageDirectory() +
                File.separator + "TEMP");
        if (!tempFolder.exists()) {
            tempFolder.mkdirs();
        }
        File output = new File(tempFolder, "tempPicture.jpg");
        return output;
    }
}
