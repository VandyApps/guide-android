
package edu.vanderbilt.vm.guide.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.widget.ImageView;

/**
 * Handles asynchronous image downloading. Most of this code comes from
 * http://android
 * -developers.blogspot.com/2010/07/multithreading-for-performance.html
 */
public class ImageDownloader {
    private static final Logger logger = LoggerFactory.getLogger("util.ImageDownloader");

    /**
     * Download an image at the given url and show it in the imageView
     * asynchronously. This method spawns an AsyncTask. Only call this method if
     * you don't need to potentially cancel the AsyncTask. Note that not
     * cancelling the AsyncTask could be very bad since AsyncTask has a static
     * thread pool of only 10 threads, so if the threads aren't terminated in a
     * timely fashion, no more AsyncTasks can be executed.
     * 
     * @param url The url to download the image from
     * @param imageView The ImageView to update
     */
    public static void download(String url, ImageView imageView) {
        BitmapDownloaderTask task = new BitmapDownloaderTask(imageView);
        task.execute(url);
    }

    public static class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap> {
        private String url;

        private final WeakReference<ImageView> imageViewReference;

        public BitmapDownloaderTask(ImageView imageView) {
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        @Override
        // Actual download method, run in the task thread
        protected Bitmap doInBackground(String... params) {
            // params comes from the execute() call: params[0] is the url.
            return downloadBitmap(params[0]);
        }

        @Override
        // Once the image is downloaded, associates it to the imageView
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }

            if (imageViewReference != null) {
                ImageView imageView = imageViewReference.get();
                if (imageView != null && bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }

    static Bitmap downloadBitmap(String url) {
        logger.debug("Attempting to download image at URL {}", url);
        final AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
        final HttpGet getRequest = new HttpGet(url);

        try {
            HttpResponse response = client.execute(getRequest);
            logger.trace("Finished download attempt");
            final int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                logger.warn("Error {} while retrieving bitmap from {}", statusCode, url);
                return null;
            }

            final HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream inputStream = null;
                try {
                    inputStream = entity.getContent();
                    final Bitmap bitmap = BitmapFactory.decodeStream(new FlushedInputStream(
                            inputStream));
                    logger.trace("Returning a bitmap");
                    return bitmap;
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    entity.consumeContent();
                }
            }
        } catch (Exception e) {
            // Could provide a more explicit error message for IOException or
            // IllegalStateException
            getRequest.abort();
            logger.warn("Error while retrieving bitmap from {}", url, e);
        } finally {
            if (client != null) {
                client.close();
            }
        }
        logger.trace("Returning null");
        return null;
    }

    static class FlushedInputStream extends FilterInputStream {
        public FlushedInputStream(InputStream inputStream) {
            super(inputStream);
        }

        @Override
        public long skip(long n) throws IOException {
            long totalBytesSkipped = 0L;
            while (totalBytesSkipped < n) {
                long bytesSkipped = in.skip(n - totalBytesSkipped);
                if (bytesSkipped == 0L) {
                    int bite = read();
                    if (bite < 0) {
                        break; // we reached EOF
                    } else {
                        bytesSkipped = 1; // we read one byte
                    }
                }
                totalBytesSkipped += bytesSkipped;
            }
            return totalBytesSkipped;
        }
    }
}
