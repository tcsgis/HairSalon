/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.changhong.util.cache;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;

import com.changhong.common.AndroidVersionCheckUtils;
import com.changhong.common.AsyncTask;
import com.changhong.util.CHLogger;

/**
 * @Title CHFileCache
 * @Package com.changhong.util.cache
 * @Description 文件的缓存操作类,包括内存缓存，与磁盘缓存
 * @version V1.0
 */
public class CHFileCache
{
	// 默认内存储存储大小
	private static final int DEFAULT_MEM_CACHE_SIZE = 1024 * 1024 * 5; // 5MB
	// 默认磁盘存储大小
	public static final int DEFAULT_DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB
	// 当压缩图片到磁盘的是默认格式
	private static final int DEFAULT_COMPRESS_QUALITY = 70;
	private static final int DISK_CACHE_INDEX = 0;
	// 这些常量帮助轻松切换不同的缓存
	private static final boolean DEFAULT_MEM_CACHE_ENABLED = true; // 默认内存缓存启用
	private static final boolean DEFAULT_DISK_CACHE_ENABLED = true; // 默认磁盘缓存启用
	private static final boolean DEFAULT_CLEAR_DISK_CACHE_ON_START = false;// 默认的清晰的磁盘缓存开始
	private static final boolean DEFAULT_INIT_DISK_CACHE_ON_CREATE = false; // 默认的初始化的磁盘高速缓存开始
	private CHCacheParams mCacheParams;
	private boolean mDiskCacheStarting = true;
	private LruCache<String, byte[]> mMemoryCache;
	private DiskLruCache mDiskLruCache;
	private final Object mDiskCacheLock = new Object();

	/**
	 * 创建一个新的CHFileCache对象使用指定的参数。
	 * 
	 * @param cacheParams
	 *            缓存参数用来初始化缓存
	 */
	public CHFileCache(CHCacheParams cacheParams)
	{
		init(cacheParams);
	}

	/**
	 * 创建一个缓存
	 * 
	 * @param context
	 *            上下文信息
	 * @param uniqueName
	 *            这个标示名字会被添加到生成緩存文件夹
	 * 
	 */
	public CHFileCache(Context context, String uniqueName)
	{
		init(new CHCacheParams(context, uniqueName));
	}

	private void init(CHCacheParams cacheParams)
	{
		mCacheParams = cacheParams;
		if (mCacheParams.memoryCacheEnabled)
		{
			mMemoryCache = new LruCache<String, byte[]>(
					mCacheParams.memCacheSize)
			{
				@Override
				protected int sizeOf(String key, byte[] buffer)
				{
					return getSize(key, buffer);
				}

			};
		}

		// 如果默认的磁盘缓存没有初始化，那就需要初始化，在一个单独的线程由于磁盘访问。
		if (mCacheParams.initDiskCacheOnCreate && hasNotInited())
		{
			AsyncTask.execute(new Runnable() {
				@Override
				public void run() {
					// Set up disk cache
					initDiskCache();
				}
			});
		}
	}

	public boolean hasNotInited(){
		final DiskLruCache cache = mDiskLruCache;
		return (cache == null || cache.isClosed());
	}

	/**
	 * 初始化磁盘缓存
	 */
	public void initDiskCache()
	{
		// Set up disk cache
		synchronized (mDiskCacheLock)
		{
			if (hasNotInited())
			{
				File diskCacheDir = mCacheParams.diskCacheDir;
				if (mCacheParams.diskCacheEnabled && diskCacheDir != null)
				{
					if (!diskCacheDir.exists())
					{
						diskCacheDir.mkdirs();
					}
					long usableSpace = 0;
					if (AndroidVersionCheckUtils.hasGingerbread())
					{
						usableSpace = CHExternalOverFroyoUtils
								.getUsableSpace(diskCacheDir);
					} else
					{
						usableSpace = CHExternalUnderFroyoUtils
								.getUsableSpace(diskCacheDir);
					}
					if (usableSpace > mCacheParams.diskCacheSize)
					{
						try
						{

							mDiskLruCache = DiskLruCache.open(diskCacheDir, 1,
									1, DEFAULT_DISK_CACHE_SIZE);
							// mCacheParams.initDiskCacheOnCreate = true;
						} catch (final IOException e)
						{
							mCacheParams.diskCacheDir = null;
							CHLogger.e(CHFileCache.this, "initDiskCache - " + e);
						}
					}
				}
			}
			mDiskCacheStarting = false;
			mDiskCacheLock.notifyAll();
		}
	}

	/**
	 * 添加 byte[]类型数据到内存缓存和磁盘缓存
	 * 
	 * @param data
	 *            byte[]的惟一标识符来存储,一般是URL
	 * @param buffer
	 *            需要添加到缓存的数据
	 */
	public void addBufferToCache(String data, byte[] buffer)
	{
		if (data == null || buffer == null)
		{
			return;
		}

		// 添加到内存缓存
		if (mMemoryCache != null && mMemoryCache.get(data) == null)
		{
			mMemoryCache.put(data, buffer);
		}

		synchronized (mDiskCacheLock)
		{
			// 添加到磁盘缓存
			if (mDiskLruCache != null)
			{
				String key = "";
				if (AndroidVersionCheckUtils.hasGingerbread())
				{
					key = CHExternalOverFroyoUtils.hashKeyForDisk(data);
				} else
				{
					key = CHExternalUnderFroyoUtils.hashKeyForDisk(data);
				}
				OutputStream out = null;
				try
				{
					DiskLruCache.Snapshot snapshot = mDiskLruCache.get(key);
					if (snapshot == null)
					{
						final DiskLruCache.Editor editor = mDiskLruCache
								.edit(key);
						if (editor != null)
						{
							out = editor.newOutputStream(DISK_CACHE_INDEX);
							out.write(buffer, 0, buffer.length);
							editor.commit();
							out.close();
						}
					} else
					{
						snapshot.getInputStream(DISK_CACHE_INDEX).close();
					}
				} catch (final IOException e)
				{
					CHLogger.e(CHFileCache.this, "addBufferToCache - " + e);
				} catch (Exception e)
				{
					CHLogger.e(CHFileCache.this, "addBufferToCache - " + e);
				} finally
				{
					if (out != null)
					{
						try
						{
							out.close();
						} catch (IOException e)
						{
						}
					}
				}
			}
		}
	}

	/**
	 * 从内存缓存获取数据，如果内存缓存没有，它会从磁盘缓存获取填充到内存缓存
	 * 
	 * @param data
	 *            byte[]的惟一标识符来存储,一般是URL
	 * @return 返回byte[]类型的一个数据
	 */
	public byte[] getBufferFromMemCache(String data)
	{
		byte[] memValue = null;
		try
		{
			if (mMemoryCache != null)
			{
				memValue = mMemoryCache.get(data);
			}
			/*
			 * if (memValue == null) { memValue =
			 * getInputStreamFromDiskCache(data); // 添加数据到内存缓存 if (memValue !=
			 * null && mMemoryCache != null && mMemoryCache.get(data) == null) {
			 * mMemoryCache.put(data, memValue); } }
			 */
			//
		} catch (Exception e)
		{
			// TODO: handle exception
			e.printStackTrace();
			CHLogger.e(CHFileCache.this, "获取缓存数据失败！");
		}
		return memValue;
	}

	/**
	 * 从磁盘缓存中获取数据(若内存缓存中已存在则直接返回)
	 * 
	 * @param data
	 *            独特的标识符项目得到
	 * @return 如果在缓存中找到相应的数据，则返回数据,否则为null
	 */
	public byte[] getBufferFromDiskCache(String data)
	{
		String key = "";
		if (AndroidVersionCheckUtils.hasGingerbread())
		{
			key = CHExternalOverFroyoUtils.hashKeyForDisk(data);
		} else
		{
			key = CHExternalUnderFroyoUtils.hashKeyForDisk(data);
		}

//		if (mDiskCacheStarting) {
//			synchronized (mDiskCacheLock)
//			{
//				while (mDiskCacheStarting)
//				{
//					try
//					{
//						mDiskCacheLock.wait();
//					} catch (InterruptedException e)
//					{
//					}
//				}
//			}
//		}
		if (mDiskCacheStarting) {//hasNotInited()
			synchronized (mDiskCacheLock) {
				if (mDiskCacheStarting) {
					initDiskCache();
				}
			}
		}

		if (mDiskLruCache != null)
		{
			DiskLruCache.Snapshot snapshot = null;
			try
			{
				snapshot = mDiskLruCache.get(key);
				if (snapshot != null)
				{
					byte[] buffer = null;
					//检测缓存
					if (mMemoryCache == null ||
							(buffer = mMemoryCache.get(data)) == null) {
						synchronized (mDiskLruCache.getLock(key)) {
							if (mMemoryCache == null ||
									(buffer = mMemoryCache.get(data)) == null) {
								InputStream fileInputStream = snapshot
										.getInputStream(DISK_CACHE_INDEX);

								buffer = readStream(fileInputStream);
								// 添加数据到内存缓存
								if (buffer != null && mMemoryCache != null
										&& mMemoryCache.get(data) == null)
								{
									mMemoryCache.put(data, buffer);
								}
							}
						}
					}
					return buffer;
				}
			} catch (final IOException e)
			{

				CHLogger.e(CHFileCache.this, "getBufferFromDiskCache - "
						+ e);
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				CHLogger.e(CHFileCache.this, "getBufferFromDiskCache - "
						+ e);
			} finally
			{
				if (snapshot != null)
					snapshot.close();
			}
		}
		return null;
	}

	/*
	 * 得到图片字节流 数组大小
	 */
	public static byte[] readStream(InputStream inStream) throws Exception
	{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1)
		{
			outStream.write(buffer, 0, len);
		}
		outStream.close();
		return outStream.toByteArray();
	}

	/**
	 * 位图的惟一标识符来存储清除内存和磁盘缓存都CHFileCache与此相关 对象。 注意,这包括对磁盘访问,所以这是不应当在主线程或
	 * UI线程上执行的。
	 */
	public void clearCache()
	{
		if (mMemoryCache != null)
		{
			mMemoryCache.evictAll();
		}

		synchronized (mDiskCacheLock)
		{
			mDiskCacheStarting = true;
			if (mDiskLruCache != null && !mDiskLruCache.isClosed())
			{
				try
				{
					mDiskLruCache.delete();

				} catch (IOException e)
				{
					CHLogger.e(CHFileCache.this, "clearCache - " + e);
				}
				mDiskLruCache = null;
				initDiskCache();
			}
		}
	}

	/**
	 * 位图的惟一标识符来存储清除内存和磁盘缓存指定文件。 注意,这包括对磁盘访问,所以这是不应当在主线程或
	 * UI线程上执行的。
	 */
	public void removeCacheFile(String key)
	{
		if (mMemoryCache != null)
		{
			mMemoryCache.remove(key);
		}

		synchronized (mDiskCacheLock)
		{
			//mDiskCacheStarting = true;
			if (mDiskLruCache != null)
			{
				try
				{
					mDiskLruCache.remove(key);
				} catch (IOException e)
				{
					CHLogger.e(CHFileCache.this, "removeCacheFile - " + e);
				}
			}
		}
	}
	
	/**
	 * 磁盘缓存刷新TAFileCache与此相关的对象。注意,这包括对磁盘访问,所以这是不应当在主线程或 UI线程上执行的。
	 */
	public void flush()
	{
		synchronized (mDiskCacheLock)
		{
			if (mDiskLruCache != null)
			{
				try
				{
					mDiskLruCache.flush();
				} catch (IOException e)
				{
					CHLogger.e(CHFileCache.this, "flush - " + e);
				}
			}
		}
	}

	/**
	 * 关闭磁盘缓存与此相关TAFileCache对象。注意,这包括对磁盘访问,所以这是不应当在主线程或 UI线程上执行的。
	 */
	public void close()
	{
		synchronized (mDiskCacheLock)
		{
			if (mDiskLruCache != null)
			{
				try
				{
					if (!mDiskLruCache.isClosed())
					{
						mDiskLruCache.close();
						mDiskLruCache = null;
						/*
						 * if (BuildConfig.DEBUG) { Log.d(TAG,
						 * "Disk cache closed"); }
						 */
					}
				} catch (IOException e)
				{
					CHLogger.e(CHFileCache.this, "close" + e);
				}
			}
		}
	}

	/**
	 * 从获得byte[]的长度
	 * 
	 * @param key
	 * @param value
	 * @return bytes类型的长度
	 */
	private int getSize(String key, byte[] value)
	{
		// TODO Auto-generated method stub
		return value.length;
	}

	/**
	 * @Title TACacheParams
	 * @Package com.changhong.util.cache
	 * @Description 缓存的参数类
	 * @author 白猫
	 * @date 2013-1-20
	 * @version V1.0
	 */
	public static class CHCacheParams
	{
		public int memCacheSize = DEFAULT_MEM_CACHE_SIZE;
		public int diskCacheSize = DEFAULT_DISK_CACHE_SIZE;
		public File diskCacheDir;
		public int compressQuality = DEFAULT_COMPRESS_QUALITY;
		public boolean memoryCacheEnabled = DEFAULT_MEM_CACHE_ENABLED;
		public boolean diskCacheEnabled = DEFAULT_DISK_CACHE_ENABLED;
		public boolean clearDiskCacheOnStart = DEFAULT_CLEAR_DISK_CACHE_ON_START;
		public boolean initDiskCacheOnCreate = DEFAULT_INIT_DISK_CACHE_ON_CREATE;

		/**
		 * 初始化磁盘参数
		 * 
		 * @param context
		 *            上下文
		 * @param uniqueName
		 *            缓存文件包名
		 */
		public CHCacheParams(Context context, String uniqueName)
		{
			if (AndroidVersionCheckUtils.hasGingerbread())
			{
				diskCacheDir = CHExternalOverFroyoUtils.getDiskCacheDir(
						context, uniqueName);
			} else
			{
				diskCacheDir = CHExternalUnderFroyoUtils.getDiskCacheDir(
						context, uniqueName);
			}
		}

		/**
		 * 初始化磁盘参数
		 * 
		 * @param diskCacheDir
		 *            缓存文件包
		 */
		public CHCacheParams(File diskCacheDir)
		{
			this.diskCacheDir = diskCacheDir;
		}

		/**
		 * 设置缓存的大小
		 * 
		 * @param context
		 *            上下文
		 * @param percent
		 *            设置分配缓存为本设备的百度比，以0.01f计算
		 * 
		 */
		public void setMemCacheSizePercent(Context context, float percent)
		{
			if (percent < 0.05f || percent > 0.8f)
			{
				throw new IllegalArgumentException(
						"setMemCacheSizePercent - percent must be "
								+ "between 0.05 and 0.8 (inclusive)");
			}
			memCacheSize = Math.round(percent * getMemoryClass(context) * 1024
					* 1024);
		}

		private static int getMemoryClass(Context context)
		{
			if (AndroidVersionCheckUtils.hasGingerbread())
			{
				return CHExternalOverFroyoUtils.getMemoryClass(context);
			} else
			{
				return CHExternalUnderFroyoUtils.getMemoryClass(context);
			}

		}
	}

}
