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
package com.changhong.util.db;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;

import com.changhong.util.CHLogger;
import com.changhong.util.db.SQLiteDB.TADBParams;
import com.changhong.util.db.SQLiteDB.TADBUpdateListener;
import com.llw.salon.BuildConfig;

import android.content.Context;

/**
 * @Title TASQLiteDatabasePool
 * @Package com.changhong.util.db
 * @Description 数据库管理池
 * @version V1.0
 */
public class SQLiteDBPool
{
	private String testTable = "Sqlite_master"; // 测试连接是否可用的测试表名，默认Sqlite_master为测试表
	private int initialSQLiteDatabase = 2; // 连接池的初始大小
	private int incrementalSQLiteDatabase = 2;// 连接池自动增加的大小
	private int maxSQLiteDatabase = 10; // 连接池最大的大小
	private Vector<PooledSQLiteDatabase> pSQLiteDatabases; // 存放连接池中数据库连接的向量
	private Context context;
	private TADBParams params;
	private TADBUpdateListener mDBUpdateListener; // 升级时监听器
	private boolean isWrite;
	private static HashMap<String, SQLiteDBPool> poolMap = new HashMap<String, SQLiteDBPool>();

	/**
	 * 单例模式
	 * 
	 * @param context
	 *            上下文
	 * @param params
	 *            数据库配置参数
	 * @param isWrite
	 *            打开数据库如果是 isWrite为true,则磁盘满时抛出错误
	 */
	public synchronized static SQLiteDBPool getInstance(
			Context context, TADBParams params, boolean isWrite)
	{
		String dbName = params.getDbName().trim();
		SQLiteDBPool pool = poolMap.get(dbName);
		if (pool == null)
		{
			pool = new SQLiteDBPool(context, params, isWrite);
			poolMap.put(dbName.trim(), pool);
		}
		return pool;
	}

	/**
	 * 单例模式
	 * 
	 * @param context
	 *            上下文
	 */
	public static SQLiteDBPool getInstance(Context context)
	{
		TADBParams params = new TADBParams();
		return getInstance(context, params, false);
	}

	/**
	 * 单例模式
	 * 
	 * @param context
	 *            上下文
	 * @param dbName
	 *            数据库名字
	 * @param dbVersion
	 *            版本
	 * @param isWrite
	 *            打开数据库如果是 isWrite为true,则磁盘满时抛出错误
	 */
	public static SQLiteDBPool getInstance(Context context,
			String dbName, int dbVersion, boolean isWrite)
	{
		TADBParams params = new TADBParams(dbName, dbVersion);
		return getInstance(context, params, isWrite);
	}

	/**
	 * 构造函数
	 * 
	 * @param context
	 *            上下文
	 * @param params
	 *            数据库配置参数
	 * @param isWrite
	 *            打开数据库如果是 isWrite为true,则磁盘满时抛出错误
	 */
	public SQLiteDBPool(Context context, TADBParams params,
			boolean isWrite)
	{
		// TODO Auto-generated constructor stub
		this.context = context;
		this.params = params;
		this.isWrite = isWrite;
	}

	/**
	 * 设置升级的的监听器
	 * 
	 * @param dbUpdateListener
	 */
	public void setOnDbUpdateListener(TADBUpdateListener dbUpdateListener)
	{
		this.mDBUpdateListener = dbUpdateListener;
	}

	/**
	 * 
	 * 返回连接池的初始大小
	 * 
	 * @return 初始连接池中可获得的连接数量
	 */
	public int getInitialSQLiteDatabase()
	{
		return initialSQLiteDatabase;
	}

	/**
	 * 设置连接池的初始大小
	 * 
	 * @param initialSQLiteDatabase 用于设置初始连接池中连接的数量
	 */
	public void setInitialSQLiteDatabase(int initialSQLiteDatabase)
	{
		this.initialSQLiteDatabase = initialSQLiteDatabase;
	}

	/**
	 * 返回连接池自动增加的大小 、
	 * 
	 * @return 连接池自动增加的大小
	 */
	public int getIncrementalSQLiteDatabase()
	{
		return incrementalSQLiteDatabase;
	}

	/**
	 * 设置连接池自动增加的大小
	 * 
	 * @param incrementalSQLiteDatabase 连接池自动增加的大小
	 */
	public void setIncrementalSQLiteDatabase(int incrementalSQLiteDatabase)
	{
		this.incrementalSQLiteDatabase = incrementalSQLiteDatabase;
	}

	/**
	 * 
	 * 返回连接池中最大的可用连接数量
	 * 
	 * @return 连接池中最大的可用连接数量
	 */
	public int getMaxSQLiteDatabase()
	{
		return maxSQLiteDatabase;
	}

	/**
	 * 设置连接池中最大可用的连接数量
	 * 
	 * @param maxSQLiteDatabase 设置连接池中最大可用的连接数量值
	 */
	public void setMaxSQLiteDatabase(int maxSQLiteDatabase)
	{
		this.maxSQLiteDatabase = maxSQLiteDatabase;
	}

	/**
	 * 设置测试表的名字
	 * 
	 * @param testTable
	 *            String 测试表的名字
	 */

	public void setTestTable(String testTable)
	{
		this.testTable = testTable;
	}

	/**
	 * 获取测试数据库表的名字
	 * 
	 * @return 测试数据库表的名字
	 */

	public String getTestTable()
	{
		return this.testTable;
	}

	/**
	 * 
	 * 创建一个数据库连接池，连接池中的可用连接的数量采用类成员 initialSQLiteDatabase 中设置的值
	 */
	public synchronized void createPool()
	{
		// 确保连接池没有创建
		// 如果连接池己经创建了，保存连接的向量 sqLiteDatabases 不会为空
		if (pSQLiteDatabases != null)
		{
			return; // 如果己经创建，则返回
		}
		// 创建保存连接的向量 , 初始时有 0 个元素
		pSQLiteDatabases = new Vector<PooledSQLiteDatabase>();
		// 根据 initialConnections 中设置的值，创建连接。
		createSQLiteDatabase(this.initialSQLiteDatabase);
		CHLogger.i(SQLiteDBPool.this, " 数据库连接池创建成功！ ");
	}

	/**
	 * 创建由 numSQLiteDatabase 指定数目的数据库连接 , 并把这些连接 放入 numSQLiteDatabase 向量中
	 * 
	 * @param numSQLiteDatabase
	 *            要创建的数据库连接的数目
	 */

	private void createSQLiteDatabase(int numSQLiteDatabase)
	{

		// 循环创建指定数目的数据库连接
		for (int x = 0; x < numSQLiteDatabase; x++)
		{
			// 是否连接池中的数据库连接的数量己经达到最大？最大值由类成员 maxSQLiteDatabase
			// 指出，如果 maxSQLiteDatabase 为 0 或负数，表示连接数量没有限制。
			// 如果连接数己经达到最大，即退出。
			if (this.maxSQLiteDatabase > 0
					&& this.pSQLiteDatabases.size() >= this.maxSQLiteDatabase)
			{
				CHLogger.i(this, "数据库连接数已达上限");
				break;
			}
			try
			{
				// 增加一个TASQLiteDatabase到连接池中
				pSQLiteDatabases.addElement(new PooledSQLiteDatabase(
						newSQLiteDatabase()));
				CHLogger.i(SQLiteDBPool.this, "数据库连接己创建 ......");
			} catch (Exception e)
			{
				CHLogger.i(SQLiteDBPool.this,
						" 创建数据库连接失败！ " + e.getMessage());
			}
		}
	}

	/**
	 * 创建一个新的数据库连接并返回它
	 * 
	 * @return 返回一个新创建的数据库连接
	 */

	private SQLiteDB newSQLiteDatabase()
	{

		// 创建一个数据库连接
		SQLiteDB sqliteDatabase = new SQLiteDB(context, params);
		sqliteDatabase.openDatabase(mDBUpdateListener, isWrite);
		return sqliteDatabase; // 返回创建的新的数据库连接
	}

	/**
	 * 通过调用 getFreeSQLiteDatabase() 函数返回一个可用的数据库连接 ,
	 * 如果当前没有可用的数据库连接，并且更多的数据库连接不能创 建（如连接池大小的限制），此函数等待一会再尝试获取。
	 * 
	 * @return 返回一个可用的数据库连接对象
	 */

	public synchronized SQLiteDB getSQLiteDatabase()
	{
		// 确保连接池己被创建
		if (pSQLiteDatabases == null)
		{
			CHLogger.w(this, "数据库连接池未创建，无法获取连接 !");
			return null; // 连接池还没创建，则返回 null
		}

		if (true) {
			//最多尝试40次以获取可用连接
			SQLiteDB sqliteDatabase = null;
			for (int i = 0; i < 40; ++i) {
				// 获得一个可用的数据库连接, 无可用连接时会根据上限进行创建
				sqliteDatabase = getFreeSQLiteDatabase();
				if (sqliteDatabase != null) {
					break;
				} else {
					final String msg = "无法获取可用数据库连接";
					CHLogger.w(this, msg);
					if (BuildConfig.DEBUG) {
						try {
							throw new Exception(msg);
						} catch (Exception e){
							e.printStackTrace();
						}
					}
					// 等一会再试
					wait(10);
				}
			}
			return sqliteDatabase;// 返回获得的可用的连接
		} else {

			SQLiteDB sqliteDatabase = getFreeSQLiteDatabase(); // 获得一个可用的数据库连接

			// 如果目前没有可以使用的连接，即所有的连接都在使用中
			while (sqliteDatabase == null)
			{
				//FIXME:黑白屏就是卡在这里了
				// 等一会再试
				wait(250);
				sqliteDatabase = getFreeSQLiteDatabase(); // 重新再试，直到获得可用的连接，如果
				// getFreeConnection() 返回的为 null
				// 则表明创建一批连接后也不可获得可用连接
			}
			return sqliteDatabase;// 返回获得的可用的连接
		}
	}

	/**
	 * 本函数从连接池向量 pSQLiteDatabases 中返回一个可用的的数据库连接，如果 当前没有可用的数据库连接，本函数则根据
	 * incrementalSQLiteDatabase 设置 的值创建几个数据库连接，并放入连接池中。 如果创建后，所有的连接仍都在使用中，则返回
	 * null
	 * 
	 * @return 返回一个可用的数据库连接
	 */

	private SQLiteDB getFreeSQLiteDatabase()
	{
		// 从连接池中获得一个可用的数据库连接
		SQLiteDB sqLiteDatabase = findFreeSQLiteDatabase();
		if (sqLiteDatabase == null)
		{
			// 如果目前连接池中没有可用的连接
			// 创建一些连接
			createSQLiteDatabase(incrementalSQLiteDatabase);
			// 重新从池中查找是否有可用连接
			sqLiteDatabase = findFreeSQLiteDatabase();
			if (sqLiteDatabase == null)
			{
				// 如果创建连接后仍获得不到可用的连接，则返回 null
				return null;
			}
		}
		return sqLiteDatabase;
	}

	/**
	 * 查找连接池中所有的连接，查找一个可用的数据库连接， 如果没有可用的连接，返回 null
	 * 
	 * @return 返回一个可用的数据库连接
	 */

	private SQLiteDB findFreeSQLiteDatabase()
	{
		SQLiteDB sqliteDatabase = null;
		PooledSQLiteDatabase pSQLiteDatabase = null;

		// 获得连接池向量中所有的对象
		Enumeration<PooledSQLiteDatabase> enumerate = pSQLiteDatabases
				.elements();

		// 遍历所有的对象，看是否有可用的连接
		while (enumerate.hasMoreElements())
		{
			pSQLiteDatabase = (PooledSQLiteDatabase) enumerate.nextElement();

			if (!pSQLiteDatabase.isBusy())
			{
				// 加锁
				synchronized (pSQLiteDatabase) {
					if (!pSQLiteDatabase.isBusy()) {
						// 如果此对象不忙，则获得它的数据库连接并把它设为忙
						sqliteDatabase = pSQLiteDatabase.getSqliteDatabase();
						pSQLiteDatabase.setBusy(true);
						// 测试此连接是否可用
						if (!testSQLiteDatabase(sqliteDatabase)) {
							// 如果此连接不可再用了，则创建一个新的连接， // 并替换此不可用的连接对象，如果创建失败，返回 null
							sqliteDatabase = newSQLiteDatabase();
							pSQLiteDatabase.setSqliteDatabase(sqliteDatabase);
						}
						break;
						// 己经找到一个可用的连接，退出
					}
				}
			}
		}

		/*int i = 0;
		for (PooledSQLiteDatabase pd : pSQLiteDatabases) {
			if (pd.isBusy())
				++i;
		}
		CHLogger.w(this, i + "↑/" + pSQLiteDatabases.size());
		if (BuildConfig.DEBUG)
		try {
			//Thread.currentThread().getStackTrace()[2:]
			throw new Exception("findFreeSQLiteDatabase");
		} catch (Exception e){
			e.printStackTrace();
		}*/
		return sqliteDatabase;// 返回找到到的可用连接
	}

	/**
	 * 测试一个连接是否可用，如果不可用，关掉它并返回 false 否则可用返回 true
	 * 
	 * @param sqliteDatabase
	 *            需要测试的数据库连接
	 * @return 返回 true 表示此连接可用， false 表示不可用
	 */
	private boolean testSQLiteDatabase(SQLiteDB sqliteDatabase)
	{

		if (sqliteDatabase != null)
		{
			return sqliteDatabase.testSQLiteDatabase();
		}
		// 连接可用，返回 true
		return false;
	}

	/**
	 * 此函数返回一个数据库连接到连接池中，并把此连接置为空闲。 所有使用连接池获得的数据库连接均应在不使用此连接时返回它。
	 * 
	 * @param sqLiteDatabase 需返回到连接池中的连接对象
	 */

	public void releaseSQLiteDatabase(SQLiteDB sqLiteDatabase)
	{
		if (sqLiteDatabase == null)
		{
			//ignore
			return;
		}

		boolean clean = false;
		// 确保连接池存在，如果连接没有创建（不存在），直接返回
		if (pSQLiteDatabases != null)
		{
			PooledSQLiteDatabase pSqLiteDatabase = null;

			Enumeration<PooledSQLiteDatabase> enumerate = pSQLiteDatabases
					.elements();

			// 遍历连接池中的所有连接，找到这个要返回的连接对象
			while (enumerate.hasMoreElements())
			{
				pSqLiteDatabase = (PooledSQLiteDatabase) enumerate.nextElement();

				// 先找到连接池中的要返回的连接对象
				if (sqLiteDatabase == pSqLiteDatabase.getSqliteDatabase())
				{
					if (pSqLiteDatabase.isBusy()) {
						synchronized (pSqLiteDatabase) {
							// 找到了 , 设置此连接为空闲状态
							pSqLiteDatabase.setBusy(false);
							sqLiteDatabase.free();
						}
						clean = true;
						break;
					}
				}
			}
		} else {
			CHLogger.d(SQLiteDBPool.this, " 连接池不存在，无法返回此连接到连接池中 !");
		}
		if (!clean) {
			//找不到连接池以归还, 直接释放连接
			sqLiteDatabase.close();
		}

		/*int i = 0;
		for (PooledSQLiteDatabase pd : pSQLiteDatabases) {
			if (pd.isBusy())
				++i;
		}
		CHLogger.w(this, i + "↓/" + pSQLiteDatabases.size());
		if (BuildConfig.DEBUG)
		try {
			//Thread.currentThread().getStackTrace()[2:]
			throw new Exception("releaseSQLiteDatabase");
		} catch (Exception e){
			e.printStackTrace();
		}*/
	}

	/**
	 * 刷新连接池中所有的连接对象
	 * 
	 */

	public synchronized void refreshSQLiteDatabase()
	{

		// 确保连接池己创新存在
		if (pSQLiteDatabases == null)
		{
			CHLogger.d(SQLiteDBPool.this, " 连接池不存在，无法刷新 !");
			return;
		}

		PooledSQLiteDatabase pSqLiteDatabase = null;
		Enumeration<PooledSQLiteDatabase> enumerate = pSQLiteDatabases
				.elements();
		while (enumerate.hasMoreElements())
		{

			// 获得一个连接对象
			pSqLiteDatabase = (PooledSQLiteDatabase) enumerate.nextElement();

			synchronized (pSqLiteDatabase) {
				// 如果对象忙则等 5 秒 ,5 秒后直接刷新
				if (pSqLiteDatabase.isBusy())
				{
					wait(5000); // 等 5 秒
				}

				// 关闭此连接，用一个新的连接代替它。
				closeSQLiteDatabase(pSqLiteDatabase.getSqliteDatabase());
				pSqLiteDatabase.setSqliteDatabase(newSQLiteDatabase());
				pSqLiteDatabase.setBusy(false);
			}
		}
	}

	/**
	 * 关闭连接池中所有的连接，并清空连接池。
	 */

	public synchronized void closeSQLiteDatabase()
	{

		// 确保连接池存在，如果不存在，返回
		if (pSQLiteDatabases == null)
		{
			CHLogger.d(SQLiteDBPool.this, "连接池不存在，无法关闭 !");
			return;
		}
		PooledSQLiteDatabase pSqLiteDatabase = null;
		Enumeration<PooledSQLiteDatabase> enumerate = pSQLiteDatabases
				.elements();
		while (enumerate.hasMoreElements())
		{
			// 获得一个连接对象
			pSqLiteDatabase = (PooledSQLiteDatabase) enumerate.nextElement();

			// 如果忙，等 5 秒
			if (pSqLiteDatabase.isBusy())
			{
				wait(5000); // 等 5 秒
			}
			// 5 秒后直接关闭它
			closeSQLiteDatabase(pSqLiteDatabase.getSqliteDatabase());
			// 从连接池向量中删除它
			pSQLiteDatabases.removeElement(pSqLiteDatabase);
		}
		// 置连接池为空
		pSQLiteDatabases = null;
	}

	/**
	 * 关闭一个数据库连接
	 * 
	 * @param sqlLiteDatabase 需要关闭的数据库连接
	 */

	private void closeSQLiteDatabase(SQLiteDB sqlLiteDatabase)
	{
		sqlLiteDatabase.close();
	}

	/**
	 * 使程序等待给定的毫秒数
	 * 
	 * @param mSeconds 给定的毫秒数
	 */

	private void wait(int mSeconds)
	{
		try
		{
			Thread.sleep(mSeconds);
		} catch (InterruptedException e)
		{
		}
	}

	/**
	 * 内部使用的用于保存连接池中连接对象的类 此类中有两个成员，一个是数据库的连接，另一个是指示此连接是否 正在使用的标志。
	 */

	class PooledSQLiteDatabase
	{
		SQLiteDB sqliteDatabase;// 数据库连接
		boolean busy; // 此连接是否正在使用的标志，默认没有正在使用

		// 构造函数，根据一个 TASQLiteDatabase 构告一个 PooledSQLiteDatabase 对象
		public PooledSQLiteDatabase(SQLiteDB sqliteDatabase)
		{
			this.sqliteDatabase = sqliteDatabase;
		}

		// 返回此对象中的连接
		public SQLiteDB getSqliteDatabase()
		{
			return sqliteDatabase;
		}

		// 设置此对象的，连接
		public void setSqliteDatabase(SQLiteDB sqliteDatabase)
		{
			this.sqliteDatabase = sqliteDatabase;
		}

		// 获得对象连接是否忙
		public boolean isBusy()
		{
			return busy;
		}

		// 设置对象的连接正在忙
		public void setBusy(boolean busy)
		{
			this.busy = busy;
		}
	}
}
