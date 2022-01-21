package com.krunal.choosedirectory.models;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.webkit.MimeTypeMap;
import androidx.core.content.FileProvider;
import com.krunal.choosedirectory.BuildConfig;
import com.krunal.choosedirectory.ClsGlobal.SpaceFormatter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
public class FileInfo
{
    private final File file;
    private String cachedName = null;
    private String cachedPath = null;
    private String cachedMimeType = null;
    private String cachedExtension = null;
    private String cachedSize = null;
    private Boolean cachedIsImage = null;
    private Boolean cachedIsPdf = null;
    private Boolean cachedIsAudio = null;
    private Boolean cachedIsVideo = null;
    private Boolean cachedIsDirectory = null;
    private Integer cachedNumberOfChildren = null;
    private SoftReference<Bitmap> cachedBitmap;
    private boolean isSelected = false;
    public FileInfo(File file)
    {
        System.out.println("YAO [" + Thread.currentThread().getStackTrace()[2].getClassName() + "|" + Thread.currentThread().getStackTrace()[2].getMethodName() + "|" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "]");

        this.file = file;
        this.cachedBitmap = new SoftReference<>(null);
    }
    public List<FileInfo> files()
    {
        System.out.println("YAO [" + Thread.currentThread().getStackTrace()[2].getClassName() + "|" + Thread.currentThread().getStackTrace()[2].getMethodName() + "|" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "]");

        List<FileInfo> result = new ArrayList<>();

        if (isDirectory()) {
            for (File currentFile : children()) {
                if (currentFile != null) {
                    FileInfo fileInfo = new FileInfo(currentFile);
                    result.addAll(fileInfo.files());
                }
            }
        } else {
            result.add(this);
        }

        return result;
    }
    public boolean exists()
    {
        System.out.println("YAO [" + Thread.currentThread().getStackTrace()[2].getClassName() + "|" + Thread.currentThread().getStackTrace()[2].getMethodName() + "|" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "]");

        return file.exists();
    }
    public boolean rename(String newName)
    {
        System.out.println("YAO [" + Thread.currentThread().getStackTrace()[2].getClassName() + "|" + Thread.currentThread().getStackTrace()[2].getMethodName() + "|" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "]");

        File newFile = new File(file.getParentFile(), newName);
        return !newFile.exists() && file.renameTo(newFile);
    }
    public boolean copy(FileInfo target, boolean delete)
    {
        System.out.println("YAO [" + Thread.currentThread().getStackTrace()[2].getClassName() + "|" + Thread.currentThread().getStackTrace()[2].getMethodName() + "|" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "]");

        if (isDirectory()) {
            File newTargetFolder = new File(target.file, file.getName());
            boolean allCopied = (newTargetFolder.exists() || newTargetFolder.mkdirs());

            for (File currentFile : children()) {
                if (currentFile != null) {
                    FileInfo fileInfo = new FileInfo(currentFile);
                    File newTarget = new File(target.file, file.getName());
                    allCopied &= (newTarget.exists() || newTarget.mkdirs()) && fileInfo.copy(new FileInfo(newTarget), delete);
                }
            }

            if (delete && allCopied) {
                delete ();
            }

            return allCopied;
        } else {
            boolean copied = copy(file, target.file);

            if (delete && copied) {
                delete ();
            }

            return copied;
        }
    }
    private boolean copy(File source, File destination)
    {
        System.out.println("YAO [" + Thread.currentThread().getStackTrace()[2].getClassName() + "|" + Thread.currentThread().getStackTrace()[2].getMethodName() + "|" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "]");

        File target = new File(destination, source.getName());
        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            inputStream = new FileInputStream(source);
            outputStream = new FileOutputStream(target);
            byte[] buffer = new byte[1024];
            int length;

            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.flush();
            return true;
        } catch (Exception e) {
            //            CrashUtils.report(e);
            return false;
        }

        finally {
            close(inputStream);
            close(outputStream);
        }
    }
    private void close(Closeable closeable)
    {
        System.out.println("YAO [" + Thread.currentThread().getStackTrace()[2].getClassName() + "|" + Thread.currentThread().getStackTrace()[2].getMethodName() + "|" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "]");

        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (Exception e) {
            //            CrashUtils.report(e);
        }
    }
    public boolean delete ()
    {
        System.out.println("YAO [" + Thread.currentThread().getStackTrace()[2].getClassName() + "|" + Thread.currentThread().getStackTrace()[2].getMethodName() + "|" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "]");

        if (isDirectory()) {
            for (File currentFile : children()) {
                if (currentFile != null) {
                    FileInfo fileInfo = new FileInfo(currentFile);
                    fileInfo.delete();
                }
            }
        }

        return file.delete();
    }
    public boolean hasFiles()
    {
        System.out.println("YAO [" + Thread.currentThread().getStackTrace()[2].getClassName() + "|" + Thread.currentThread().getStackTrace()[2].getMethodName() + "|" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "]");

        if (isDirectory()) {
            for (File currentFile : children()) {
                if (currentFile != null) {
                    FileInfo fileInfo = new FileInfo(currentFile);

                    if (fileInfo.hasFiles()) {
                        return true;
                    }
                }
            }

            return false;
        } else {
            return true;
        }
    }
    public File parent()
    {
        System.out.println("YAO [" + Thread.currentThread().getStackTrace()[2].getClassName() + "|" + Thread.currentThread().getStackTrace()[2].getMethodName() + "|" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "]");

        return file.getParentFile();
    }
    public String name()
    {
        System.out.println("YAO [" + Thread.currentThread().getStackTrace()[2].getClassName() + "|" + Thread.currentThread().getStackTrace()[2].getMethodName() + "|" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "]");

        if (cachedName == null) {
            cachedName = file.getName();
        }

        return cachedName;
    }
    public Uri uri(Context context)
    {
        System.out.println("YAO [" + Thread.currentThread().getStackTrace()[2].getClassName() + "|" + Thread.currentThread().getStackTrace()[2].getMethodName() + "|" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "]");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                return FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
            } catch (Exception e) {
                return Uri.fromFile(file);
            }
        } else {
            return Uri.fromFile(file);
        }
    }
    public String path()
    {
        System.out.println("YAO [" + Thread.currentThread().getStackTrace()[2].getClassName() + "|" + Thread.currentThread().getStackTrace()[2].getMethodName() + "|" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "]");

        if (cachedPath == null) {
            cachedPath = file.getAbsolutePath();
        }

        return cachedPath;
    }
    public String mimeType()
    {
        System.out.println("YAO [" + Thread.currentThread().getStackTrace()[2].getClassName() + "|" + Thread.currentThread().getStackTrace()[2].getMethodName() + "|" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "]");

        if (cachedMimeType == null) {
            cachedMimeType = mimeTypeV1();

            if (cachedMimeType == null) {
                cachedMimeType = mimeTypeV2();

                if (cachedMimeType == null) {
                    cachedMimeType = "*/*";
                }
            }
        }

        return cachedMimeType;
    }
    private String mimeTypeV1()
    {
        System.out.println("YAO [" + Thread.currentThread().getStackTrace()[2].getClassName() + "|" + Thread.currentThread().getStackTrace()[2].getMethodName() + "|" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "]");

        try {
            return URLConnection.guessContentTypeFromName(file.getAbsolutePath());
        } catch (Exception e) {
            return null;
        }
    }
    private String mimeTypeV2()
    {
        System.out.println("YAO [" + Thread.currentThread().getStackTrace()[2].getClassName() + "|" + Thread.currentThread().getStackTrace()[2].getMethodName() + "|" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "]");

        try {
            String extension = MimeTypeMap.getFileExtensionFromUrl(file.getAbsolutePath());
            return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        } catch (Exception e) {
            return null;
        }
    }
    public boolean isImage()
    {
        System.out.println("YAO [" + Thread.currentThread().getStackTrace()[2].getClassName() + "|" + Thread.currentThread().getStackTrace()[2].getMethodName() + "|" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "]");

        if (cachedIsImage == null) {
            String mimeType = mimeType();
            cachedIsImage = (mimeType != null) && mimeType.startsWith("image/");
        }

        return cachedIsImage;
    }
    public boolean isPdf()
    {
        System.out.println("YAO [" + Thread.currentThread().getStackTrace()[2].getClassName() + "|" + Thread.currentThread().getStackTrace()[2].getMethodName() + "|" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "]");

        if (cachedIsPdf == null) {
            String mimeType = mimeType();
            cachedIsPdf = (mimeType != null) && mimeType.startsWith("application/pdf");
        }

        return cachedIsPdf;
    }
    public boolean isAudio()
    {
        System.out.println("YAO [" + Thread.currentThread().getStackTrace()[2].getClassName() + "|" + Thread.currentThread().getStackTrace()[2].getMethodName() + "|" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "]");

        if (cachedIsAudio == null) {
            String mimeType = mimeType();
            cachedIsAudio = (mimeType != null) && mimeType.startsWith("audio/");
        }

        return cachedIsAudio;
    }
    public boolean isVideo()
    {
        System.out.println("YAO [" + Thread.currentThread().getStackTrace()[2].getClassName() + "|" + Thread.currentThread().getStackTrace()[2].getMethodName() + "|" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "]");

        if (cachedIsVideo == null) {
            String mimeType = mimeType();
            cachedIsVideo = (mimeType != null) && mimeType.startsWith("video");
        }

        return cachedIsVideo;
    }
    public boolean isDirectory()
    {
        System.out.println("YAO [" + Thread.currentThread().getStackTrace()[2].getClassName() + "|" + Thread.currentThread().getStackTrace()[2].getMethodName() + "|" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "]");

        if (cachedIsDirectory == null) {
            cachedIsDirectory = file.isDirectory();
        }

        return cachedIsDirectory;
    }
    public int numberOfChildren()
    {
        System.out.println("YAO [" + Thread.currentThread().getStackTrace()[2].getClassName() + "|" + Thread.currentThread().getStackTrace()[2].getMethodName() + "|" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "]");

        if (cachedNumberOfChildren == null) {
            cachedNumberOfChildren = children().length;
        }

        return cachedNumberOfChildren;
    }
    private File[] children()
    {
        System.out.println("YAO [" + Thread.currentThread().getStackTrace()[2].getClassName() + "|" + Thread.currentThread().getStackTrace()[2].getMethodName() + "|" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "]");

        File[] children = file.listFiles();
        return (children != null) ? children : new File[0];
    }
    public String extension()
    {
        System.out.println("YAO [" + Thread.currentThread().getStackTrace()[2].getClassName() + "|" + Thread.currentThread().getStackTrace()[2].getMethodName() + "|" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "]");

        if (cachedExtension == null) {
            cachedExtension = "";
            String name = name();
            int index = name.lastIndexOf(".");

            if (index > -1) {
                String extension = name.substring(index + 1);

                if (extension.length() <= 4) {
                    cachedExtension = extension.toUpperCase();
                }
            }
        }

        return cachedExtension;
    }
    public String size()
    {
        System.out.println("YAO [" + Thread.currentThread().getStackTrace()[2].getClassName() + "|" + Thread.currentThread().getStackTrace()[2].getMethodName() + "|" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "]");

        if (cachedSize == null) {
            SpaceFormatter spaceFormatter = new SpaceFormatter();
            cachedSize = spaceFormatter.format(file.length());
        }

        return cachedSize;
    }
    public boolean hasCachedBitmap()
    {
        System.out.println("YAO [" + Thread.currentThread().getStackTrace()[2].getClassName() + "|" + Thread.currentThread().getStackTrace()[2].getMethodName() + "|" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "]");

        return (cachedBitmap.get() != null);
    }
    public Bitmap bitmap(int maxSize)
    {
        System.out.println("YAO [" + Thread.currentThread().getStackTrace()[2].getClassName() + "|" + Thread.currentThread().getStackTrace()[2].getMethodName() + "|" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "]");

        Bitmap bitmap = cachedBitmap.get();

        if (bitmap == null) {
            String path = path();
            // decode with inJustDecodeBounds=true to check dimensions
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);
            // calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, maxSize, maxSize);
            // decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeFile(path, options);
            cachedBitmap = new SoftReference<>(bitmap);
        }

        return bitmap;
    }
    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight)
    {
        System.out.println("YAO [" + Thread.currentThread().getStackTrace()[2].getClassName() + "|" + Thread.currentThread().getStackTrace()[2].getMethodName() + "|" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "]");

        // raw height and width of image
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            int halfHeight = height / 2;
            int halfWidth = width / 2;

            // calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
    public boolean toggleSelection()
    {
        System.out.println("YAO [" + Thread.currentThread().getStackTrace()[2].getClassName() + "|" + Thread.currentThread().getStackTrace()[2].getMethodName() + "|" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "]");

        isSelected = !isSelected;
        return isSelected;
    }
    public void select(boolean value)
    {
        System.out.println("YAO [" + Thread.currentThread().getStackTrace()[2].getClassName() + "|" + Thread.currentThread().getStackTrace()[2].getMethodName() + "|" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "]");

        isSelected = value;
    }
    public boolean isSelected()
    {
        System.out.println("YAO [" + Thread.currentThread().getStackTrace()[2].getClassName() + "|" + Thread.currentThread().getStackTrace()[2].getMethodName() + "|" + Thread.currentThread().getStackTrace()[2].getFileName() + ":" + Thread.currentThread().getStackTrace()[2].getLineNumber() + "]");

        return isSelected;
    }
}
