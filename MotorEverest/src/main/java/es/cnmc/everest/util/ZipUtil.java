package es.cnmc.everest.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Util for zipping files.
 *
 * @author amiguel
 */
public class ZipUtil {

    private static final Logger log = LoggerFactory.getLogger(ZipUtil.class);

    /**
     * Size of the buffer to read/write data.
     */
    private static final int BUFFER_SIZE = 4096;


    public static List<String> unzip(String zipFilePath, String destDirectory) throws IOException {
        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
        return unzip(zipIn, destDirectory);
    }

    public static List<String> unzip(InputStream io, String destDirectory) throws IOException {
        ZipInputStream zipIn = new ZipInputStream(io);
        return unzip(zipIn, destDirectory);
    }

    /**
     * Extracts a zip file specified by the zipFilePath to a directory specified by
     * destDirectory (will be created if does not exists)
     *
     * @param zipIn
     * @param destDirectory
     * @throws IOException
     */
    private static List<String> unzip(ZipInputStream zipIn, String destDirectory) throws IOException {


        List<String> list = new ArrayList();
        File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            destDir.mkdir();
        }

        ZipEntry entry = zipIn.getNextEntry();
        // iterates over entries in the zip file
        while (entry != null) {
            String filePath = destDirectory + File.separator + entry.getName();
            if (!entry.isDirectory()) {
                // if the entry is a file, extracts it
                extractFile(zipIn, filePath);
            } else {
                // if the entry is a directory, make the directory
                File dir = new File(filePath);
                dir.mkdir();
            }
            list.add(filePath);
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();

        return list;

    }

    /**
     * Extracts a zip entry (file entry).
     *
     * @param zipIn
     * @param filePath
     * @throws IOException
     */
    private static void extractFile(ZipInputStream zipIn, String filePath) throws IOException {

        BufferedOutputStream bos = null;

        try {
            bos = new BufferedOutputStream(new FileOutputStream(filePath));
            byte[] bytesIn = new byte[BUFFER_SIZE];
            int read = 0;
            while ((read = zipIn.read(bytesIn)) != -1) {
                bos.write(bytesIn, 0, read);
            }
        } finally{
            if (bos != null) bos.close();
        }


    }

    /**
     * Returns the entries inside the zip file.
     * @param fileName
     * @return
     * @throws IOException
     */
    public static List<String> list(String fileName) throws IOException {

        List<String> list = new ArrayList<>();

        ZipFile zipFile = new ZipFile(fileName);
        Enumeration zipEntries = zipFile.entries();
        while (zipEntries.hasMoreElements()) {
            String name = ((ZipEntry) zipEntries.nextElement()).getName();
            list.add(name);
        }
        zipFile.close();

        return list;

    }

    /**
     * Create a zip file by an input stream.
     *
     * @param io
     * @param entryName
     */
    public static OutputStream zip(InputStream io, String entryName) throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);

        try {
            ZipEntry zipEntry = new ZipEntry(entryName);
            zos.putNextEntry(zipEntry);

            byte[] buf = new byte[1024];
            int bytesRead;

            // Read the input file by chucks of 1024 bytes
            // and write the read bytes to the zip stream
            while ((bytesRead = io.read(buf)) > 0) {
                zos.write(buf, 0, bytesRead);
            }

            // close ZipEntry to store the stream to the file
            zos.closeEntry();
            log.debug("Regular file :" + entryName + " zipped");
        }finally {
            zos.close();
            baos.close();

        }

        return baos;

    }

}
