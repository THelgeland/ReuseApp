package dk.reuseapp.reuseapp;

import android.hardware.Camera;
import android.os.Environment;

import java.io.File;

/**
 * Utility file for providing static methods that are to be used in several different classes.
 *
 * @author Torkil Helgeland
 */
public class Util {
    /**
     * Opens a camera
     * @param id an id specifying which camera to use - we always use the back camera 0, but using
     *           this method instead of the argument-less one is apparently best practice for API 19
     * @return the Camera object
     */
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

    /**
     * Method for getting the upload post image file
     * @return the image File object
     */
    public static File getTempImageFile() {

        File tempFolder = new File(Environment.getExternalStorageDirectory() +
                File.separator + "TEMP");
        if (!tempFolder.exists()) {
            tempFolder.mkdirs();
        }
        File output = new File(tempFolder, "tempPicture.jpg");
        return output;
    }

    /**
     * Method for getting the location of the upload text file
     * @return
     */
    public static File getTempTextFile() {

        File tempFolder = new File(Environment.getExternalStorageDirectory() +
                File.separator + "TEMP");
        if (!tempFolder.exists()) {
            tempFolder.mkdirs();
        }
        File output = new File(tempFolder, "tempText.txt");
        return output;
    }
}
