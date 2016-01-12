package com.example.guo;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Handler;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;


/**
 * Created by G on 2015/12/23.
 */
public class MyApplication extends Application {

    public static Context context;
    public static Handler handler;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        initImageLoader(context);
        handler = new Handler();
    }

    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
//		  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();//不会在内存中缓存多个大小的图片
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());//为了保证图片名称唯一
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        //内存缓存大小默认是：app可用内存的1/8
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs(); // Remove for release app

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
//		ImageLoader.getInstance().init( ImageLoaderConfiguration.createDefault(this));
    }

    /* public  void initImageLoader() {
         ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                 .memoryCacheExtraOptions(480, 800) // default = device screen dimensions
                  .threadPoolSize(3) // default
                 .threadPriority(Thread.NORM_PRIORITY - 1) // default
                 .tasksProcessingOrder(QueueProcessingType.FIFO) // default
                 .denyCacheImageMultipleSizesInMemory()
                 .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                 .memoryCacheSize(2 * 1024 * 1024)
                 .memoryCacheSizePercentage(13) // default
                 .diskCache(new UnlimitedDiskCache(getFilesDir())) // default
                 .diskCacheSize(50 * 1024 * 1024)
                 .diskCacheFileCount(100)
                 .diskCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
                 .imageDownloader(new BaseImageDownloader(context)) // default
                 .imageDecoder(new BaseImageDecoder(false)) // default
                 .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
                 .writeDebugLogs()
                 .build();
         ImageLoader.getInstance().init(config);
     }
 */
    public static Context getContext() {
        return context;
    }

    public static Handler getMainHandler() {
        return handler;
    }
}
