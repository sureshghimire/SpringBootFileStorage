package restAPI.blobStorage.util;

import java.io.ByteArrayOutputStream;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class ImageUtils {
    public static byte[] compressImage(byte[] data){
        Deflater deflater = new Deflater();
        deflater.setLevel(Deflater.BEST_COMPRESSION);
        deflater.setInput(data);
        deflater.finish();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] tmp = new byte[4*1024];
        while (!deflater.finished()){
            int size = deflater.deflate(tmp);
            outputStream.write(tmp, 0, size);
        }
        try {
            outputStream.close();
        }catch (Exception ignored){

        }
        return outputStream.toByteArray();
    }

    public static byte[] decompressImage(byte[] date){
        Inflater inflater = new Inflater();
        inflater.setInput(date);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(date.length);

        byte[] tmp = new byte[4*1024];
        try {
            while (!inflater.finished()) {
                int count = inflater.inflate(tmp);
                outputStream.write(tmp, 0, count);

            }
            outputStream.close();
        }catch (Exception ignore){

        }
        return  outputStream.toByteArray();
    }
}
