package Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class arquivo {

    public static void WriteFile (String filename,byte[] contentInBytes) {

        FileOutputStream fop = null;
        File file;

        try {

            file = new File(filename);
            fop = new FileOutputStream(file);

            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }
            fop.write(contentInBytes);
            fop.flush();
            fop.close();
            //System.out.println("Foram copiados os bytes.");
        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            try {
                if (fop != null) {
                    fop.close();

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void WriteFile (String filename,String contentStr) {

        FileOutputStream fop = null;
        File file;

        try {
            byte[] contentInBytes = contentStr.getBytes(StandardCharsets.UTF_8);

            file = new File(filename);
            fop = new FileOutputStream(file);

            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }
            fop.write(contentInBytes);
            fop.flush();
            fop.close();
            //System.out.println("Foram copiados os bytes.");
        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            try {
                if (fop != null) {
                    fop.close();

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void WriteFileAppend (String filename,byte[] contentInBytes) {

        FileOutputStream fop = null;
        File file;

        try {

            file = new File(filename);
            fop = new FileOutputStream(file,true);

            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }
            fop.write(contentInBytes);
            fop.flush();
            fop.close();
            //System.out.println("Foram copiados os bytes.");
        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            try {
                if (fop != null) {
                    fop.close();

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String ReadFile (String filename) {
        File file = new File(filename);
        FileInputStream fis = null;
        byte content[] = null;

        try {
            fis = new FileInputStream(file);

            int tam = fis.available();
            content = new byte[tam];

            fis.read(content);

        } catch (IOException e) {
            e.printStackTrace();
            return "";
        } finally {
            try {
                if (fis != null)
                    fis.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }
        return new String (content, StandardCharsets.UTF_8);
    }

    public static byte[] ReadFileBinary (String filename) {
        File file = new File(filename);
        FileInputStream fis = null;
        byte content[] = null;

        try {
            fis = new FileInputStream(file);

            int tam = fis.available();
            content = new byte[tam];

            fis.read(content);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (fis != null)
                    fis.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }
        return content;
    }
}
