package cn.changhong.chcare.core.webapi.server;

import java.io.File;
import java.io.InputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import cn.changhong.chcare.core.webapi.AbstractChCareWebApi;
import cn.changhong.chcare.core.webapi.bean.FamilyDateView;
import cn.changhong.chcare.core.webapi.bean.Location;
import cn.changhong.chcare.core.webapi.bean.ResponseBean;
import cn.changhong.chcare.core.webapi.handler.AsyncResponseCompletedHandler;
import cn.changhong.chcare.core.webapi.util.HttpRequestException;

/**
 * 与家庭相关的的接口定义
 * 
 * @ClassName: AFamilyService
 * @Description: TODO
 * @author guoyang2011@gmail.com
 * @date 2014-9-24 下午6:11:45
 * 
 */
public abstract class AFamilyService extends AbstractChCareWebApi implements
		IService {
	/**
	 * 创建新家庭 [2.1 创建家庭]
	 * 
	 * @Title: createFamily
	 * @Description: TODO
	 * @param @param familyName 家庭名称
	 * @param @param description 家庭签名
	 * @param @return
	 * @param @throws HttpRequestException
	 * @return {@code ResponseBean<Family>}
	 * @throws
	 */
	public abstract ResponseBean createFamily(String familyName,
			String description) throws HttpRequestException;

	/**
	 * 异步方式创建新家庭 [2.1 创建家庭]
	 * 
	 * @Title: createFamily
	 * @Description: TODO
	 * @param @param familyName 家庭名称
	 * @param @param description 家庭签名
	 * @param @param handler
	 * @param @return
	 * @return {@code Future<ResponseBean<Family>>}
	 * @throws
	 */
	public <T> Future<ResponseBean> createFamily(final String familyName,
			final String description,
			final AsyncResponseCompletedHandler<T> handler) {
		handler.onStart(familyName,description);
		Future<ResponseBean> future = executorProvider
				.doTask(new Callable<ResponseBean>() {

					@Override
					public ResponseBean call() {
						ResponseBean bean = null;
						try {
							bean = createFamily(familyName, description);
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.WebApi_Family_createFamily_Service);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.WebApi_Family_createFamily_Service);
						}
						return bean;
					}
				});
		return future;
	}

	/**
	 * 户主邀请成员加入家庭 [2.2.邀请加入家庭]
	 * 
	 * @Title: inviteJoinFamily
	 * @Description: TODO
	 * @param @param familyID 家庭ID
	 * @param @param username 被邀请用户名
	 * @param @return
	 * @param @throws HttpRequestException
	 * @return {@code ResponseBean<NULL>}
	 * @throws
	 */
	public abstract ResponseBean inviteJoinFamily(int familyID,
			String username, String reason) throws HttpRequestException;

	/**
	 * 异步方式户主邀请成员加入家庭 [2.2.邀请加入家庭]
	 * 
	 * @Title: inviteJoinFamily
	 * @Description: TODO
	 * @param @param familyID
	 * @param @param username
	 * @param @param handler
	 * @param @return
	 * @return {@code Future<ResponseBean<NUll>>}
	 * @throws
	 */
	public <T> Future<ResponseBean> inviteJoinFamily(final int familyID,
			final String username, final String reason,
			final AsyncResponseCompletedHandler<T> handler) {
		handler.onStart(familyID,username);
		Future<ResponseBean> future = executorProvider
				.doTask(new Callable<ResponseBean>() {

					@Override
					public ResponseBean call() {
						ResponseBean bean = null;
						try {
							bean = inviteJoinFamily(familyID, username, reason);
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.WebApi_Family_inviteJoinFamily_Service);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.WebApi_Family_inviteJoinFamily_Service);
						}
						return bean;
					}
				});
		return future;
	}

	/**
	 * 搜索已经存在的家庭 [2.3.搜索家庭]
	 * 
	 * @Title: searchFamilys
	 * @Description: TODO
	 * @param @param condition FamilyName或者 FamilyID
	 * @param @param index 上次查询返回的查询的最大ID，初始查询为0
	 * @param @param count 返回条数
	 * @param @return
	 * @param @throws HttpRequestException
	 * @return {@code ResponseBean<List<Family>>}
	 * @throws
	 */
	public abstract ResponseBean searchFamilys(String condition, int index,
			int count) throws HttpRequestException;

	/**
	 * 异步方式搜索已经存在的家庭 [2.3.搜索家庭]
	 * 
	 * @Title: searchFamilys
	 * @Description: TODO
	 * @param @param condition FamilyName或者 FamilyID
	 * @param @param index 上次查询返回的查询的最大ID，初始查询为0
	 * @param @param count 返回条数
	 * @param @param handler
	 * @param @return
	 * @return {@code Future<ResponseBean<List<Family>>>}
	 * @throws
	 */
	public <T> Future<ResponseBean> searchFamilys(final String condition,
			final int index, final int count,
			final AsyncResponseCompletedHandler<T> handler) {
		handler.onStart(condition,index,count);
		Future<ResponseBean> future = executorProvider
				.doTask(new Callable<ResponseBean>() {

					@Override
					public ResponseBean call() {
						ResponseBean bean = null;
						try {
							bean = searchFamilys(condition, index, count);
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.WebApi_Family_searchFamilys_Service);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.WebApi_Family_searchFamilys_Service);
						}
						return bean;
					}
				});
		return future;
	}

	/**
	 * 用户申请加入家庭 [2.4.申请加入家庭]
	 * 
	 * @Title: applyJoinFamily
	 * @Description: TODO
	 * @param @param familyID 家庭ID
	 * @param @param username 用户名
	 * @param @return
	 * @param @throws HttpRequestException
	 * @return {@code ResponseBean<NULL>}
	 * @throws
	 */
	public abstract ResponseBean applyJoinFamily(int familyID, String username,
			String reason) throws HttpRequestException;

	/**
	 * 异步方式申请加入家庭 [2.4.申请加入家庭]
	 * 
	 * @Title: applyJoinFamily
	 * @Description: TODO
	 * @param @param familyID 家庭ID
	 * @param @param username 用户名
	 * @param @param handler
	 * @param @return
	 * @return {@code Future<ResponseBean<NULL>>}
	 * @throws
	 */
	public <T> Future<ResponseBean> applyJoinFamily(final int familyID,
			final String username, final String reason,
			final AsyncResponseCompletedHandler<T> handler) {
		handler.onStart(familyID,username);
		Future<ResponseBean> future = executorProvider
				.doTask(new Callable<ResponseBean>() {

					@Override
					public ResponseBean call() {
						ResponseBean bean = null;
						try {
							bean = applyJoinFamily(familyID, username, reason);
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.WebApi_Family_applyJoinFamily_Service);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.WebApi_Family_applyJoinFamily_Service);
						}
						return bean;
					}
				});
		return future;
	}

	/**
	 * 8.户主同意(拒绝)账户进入家庭 用户同意(拒绝)加入家庭 8.1户主同意:FamilyID指定发生关系的家庭/
	 * UserID指定被当前家庭操作的用户/ Allow = true标志加入家庭的操作被允许/ Reason理由
	 * 8.2户主拒绝:FamilyID指定发生关系的家庭/ UserID指定被当前家庭操作的用户/ Allow = false标志加入家庭的操作被拒绝/
	 * Reason理由 8.3用户同意: FamilyID指定发生关系的家庭/ UserID指定当前用户/ Allow =
	 * true标志加入家庭的操作同意/ Reason理由 8.4用户拒绝FamilyID指定发生关系的家庭/ UserID指定当前用户/ Allow =
	 * true标志加入家庭的操作拒绝/ Reason理由 [2.5.户主同意/拒绝账户进入家庭 用户同意或者拒绝加入家庭]
	 * 
	 * @Title: joinFamilyAllowByMaster
	 * @Description: TODO
	 * @param @param familyID 家庭ID
	 * @param @param userId 用户ID
	 * @param @param nickName 用户昵称
	 * @param @param allow 是否同意
	 * @param @param reason 原因
	 * @param @return
	 * @param @throws HttpRequestException
	 * @return {@code ResponseBean<NULL>}
	 * @throws
	 */
	public abstract ResponseBean joinFamilyAllowByMaster(int familyID,
			int userId, String nickName, boolean allow, String reason)
			throws HttpRequestException;

	/**
	 * 异步方式实现 [2.5.户主同意/拒绝账户进入家庭 用户同意或者拒绝加入家庭] 8.户主同意(拒绝)账户进入家庭 用户同意(拒绝)加入家庭
	 * 8.1户主同意:FamilyID指定发生关系的家庭/ UserID指定被当前家庭操作的用户/ Allow = true标志加入家庭的操作被允许/
	 * Reason理由 8.2户主拒绝:FamilyID指定发生关系的家庭/ UserID指定被当前家庭操作的用户/ Allow =
	 * false标志加入家庭的操作被拒绝/ Reason理由 8.3用户同意: FamilyID指定发生关系的家庭/ UserID指定当前用户/
	 * Allow = true标志加入家庭的操作同意/ Reason理由 8.4用户拒绝FamilyID指定发生关系的家庭/ UserID指定当前用户/
	 * Allow = true标志加入家庭的操作拒绝/ Reason理由
	 * 
	 * @Title: joinFamilyAllowByMaster
	 * @Description: TODO
	 * @param @param familyID 家庭ID
	 * @param @param userId 用户ID
	 * @param @param nickName 用户昵称
	 * @param @param allow 是否同意
	 * @param @param reason 原因
	 * @param @param handler
	 * @param @return
	 * @return {@code Future<ResponseBean<NULL>>}
	 * @throws
	 */
	public <T> Future<ResponseBean> joinFamilyAllowByMaster(final int familyID,
			final int userId, final String nickName, final boolean allow,
			final String reason, final AsyncResponseCompletedHandler<T> handler) {
		handler.onStart(familyID,userId,nickName,allow,reason);
		Future<ResponseBean> future = executorProvider
				.doTask(new Callable<ResponseBean>() {

					@Override
					public ResponseBean call() {
						ResponseBean bean = null;
						try {
							bean = joinFamilyAllowByMaster(familyID, userId,
									nickName, allow, reason);
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.WebApi_Family_joinFamilyAllowByMaster_Service);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.WebApi_Family_joinFamilyAllowByMaster_Service);
						}
						return bean;
					}
				});
		return future;
	}

	/**
	 * 查询当前用户的家庭信息 [2.6.查询家庭信息]
	 * 
	 * @Title: getAllFamilyInfo
	 * @Description: TODO
	 * @param @return
	 * @param @throws HttpRequestException
	 * @return {@code ResponseBean<List<Family>>} 默认一个用户一个家庭，数组中返回只有一个对象
	 * @throws
	 */
	public abstract ResponseBean getAllFamilyInfo() throws HttpRequestException;

	/**
	 * 异步方式查询当前用户的家庭信息 [2.6.查询家庭信息]
	 * 
	 * @Title: getAllFamilyInfo
	 * @Description: TODO
	 * @param @param handler
	 * @param @return
	 * @return {@code Future<ResponseBean<List<Family>>>} 默认一个用户一个家庭，数组中返回只有一个对象
	 * @throws
	 */
	public <T> Future<ResponseBean> getAllFamilyInfo(
			final AsyncResponseCompletedHandler<T> handler) {

		handler.onStart();
		Future<ResponseBean> future = executorProvider
				.doTask(new Callable<ResponseBean>() {

					@Override
					public ResponseBean call() {
						ResponseBean bean = null;
						try {
							bean = getAllFamilyInfo();
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.WebApi_Family_getAllFamilyInfo_Service);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.WebApi_Family_getAllFamilyInfo_Service);
						}
						return bean;
					}
				});
		return future;
	}

	/**
	 * 当前用户对家庭成员昵称设置 [2.7.更改家庭用户备注名称]
	 * 
	 * @Title: changeUserFamilyMemberNickName
	 * @Description: TODO
	 * @param @param familyID 家庭ID
	 * @param @param userID 需要备注的用户ID
	 * @param @param nickname 昵称
	 * @param @return
	 * @param @throws HttpRequestException
	 * @return {@code ResponseBean<NULL>}
	 * @throws
	 */
	public abstract ResponseBean changeUserFamilyMemberNickName(int familyID,
			int userID, String nickname) throws HttpRequestException;

	/**
	 * 异步方式当前用户对家庭成员昵称设置 [2.7.更改家庭用户备注名称]
	 * 
	 * @Title: changeUserFamilyMemberNickName
	 * @Description: TODO
	 * @param @param familyID 家庭ID
	 * @param @param userID 需要备注的用户ID
	 * @param @param nickname 昵称
	 * @param @param handler
	 * @param @return
	 * @return {@code Future<ResponseBean<NULL>>}
	 * @throws
	 */
	public <T> Future<ResponseBean> changeUserFamilyMemberNickName(
			final int familyID, final int userID, final String nickname,
			final AsyncResponseCompletedHandler<T> handler) {
		handler.onStart(familyID,userID,nickname);
		Future<ResponseBean> future = executorProvider
				.doTask(new Callable<ResponseBean>() {

					@Override
					public ResponseBean call() {
						ResponseBean bean = null;
						try {
							bean = changeUserFamilyMemberNickName(familyID,
									userID, nickname);
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.WebApi_Family_changeUserFamilyMemberNickName_Service);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.WebApi_Family_changeUserFamilyMemberNickName_Service);
						}
						return bean;
					}
				});
		return future;
	}

	/**
	 * 查询家庭中所有成员的信息 [2.8.家庭成员列表查询 ]
	 * 
	 * @Title: getFamilyMembers
	 * @Description: TODO
	 * @param @param familyID 家庭ID
	 * @param @return
	 * @param @throws HttpRequestException
	 * @return {@code ResponseBean<List<FamilyMemberInfo>>}
	 * @throws
	 */
	public abstract ResponseBean getFamilyMembers(int familyID)
			throws HttpRequestException;

	/**
	 * 异步方式查询家庭中所有成员的信息 2.8. 家庭成员列表查询
	 * 
	 * @Title: getFamilyMembers
	 * @Description: TODO
	 * @param @param familyID 家庭ID
	 * @param @param handler
	 * @param @return
	 * @return {@code Future<ResponseBean<List<FamilyMemberInfo>>>}
	 * @throws
	 */
	public <T> Future<ResponseBean> getFamilyMembers(final int familyID,
			final AsyncResponseCompletedHandler<T> handler) {
		
		handler.onStart(familyID);
		Future<ResponseBean> future = executorProvider
				.doTask(new Callable<ResponseBean>() {

					@Override
					public ResponseBean call() {
						ResponseBean bean = null;
						try {
							bean = getFamilyMembers(familyID);
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.WebApi_Family_getFamilyMembers_Service);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.WebApi_Family_getFamilyMembers_Service);
						}
						return bean;
					}
				});
		return future;
	}

	/**
	 * 户主将某成员移除家庭 [2.10.移除家庭成员]
	 * 
	 * @Title: removeUserByMaster
	 * @Description: TODO
	 * @param @param userID 用户ID
	 * @param @param familyID 家庭ID
	 * @param @return
	 * @param @throws HttpRequestException
	 * @return {@code  ResponseBean<NULL>}
	 * @throws
	 */
	public abstract ResponseBean removeUserByMaster(int userID, int familyID)
			throws HttpRequestException;

	/**
	 * 异步方式户主将某成员移除家庭 [2.10.移除家庭成员]
	 * 说明:当家主移除自己时等价于“注销家庭”，有且只有户主可以移除除自身外的家庭成员（踢人），成员均可移除自己（退出）。
	 * 
	 * @Title: removeUserByMaster
	 * @Description: TODO
	 * @param @param userID 用户ID
	 * @param @param familyID 家庭ID
	 * @param @param handler
	 * @param @return
	 * @return {@code Future<ResponseBean<NULL>>}
	 * @throws
	 */
	public <T> Future<ResponseBean> removeUserByMaster(final int userID,
			final int familyID, final AsyncResponseCompletedHandler<T> handler) {
		handler.onStart(userID,familyID);
		Future<ResponseBean> future = executorProvider
				.doTask(new Callable<ResponseBean>() {

					@Override
					public ResponseBean call() {
						ResponseBean bean = null;
						try {
							bean = removeUserByMaster(userID, familyID);
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.WebApi_Family_removeUserByMaster_Service);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.WebApi_Family_removeUserByMaster_Service);
						}
						return bean;
					}
				});
		return future;
	}

	/**
	 * 当前用户更新家庭信息 [2.11.更新家庭信息]
	 * 
	 * @Title: updateFamilyInfo
	 * @Description: TODO
	 * @param @param familyID 家庭ID
	 * @param @param userID 用户ID
	 * @param @param familyName 家庭名称
	 * @param @param familySign 家庭签名
	 * @param @param Location 家庭地址
	 * @param @return
	 * @param @throws HttpRequestException
	 * @return {@code ResponseBean<NULL>}
	 * @throws
	 */
	public abstract ResponseBean updateFamilyInfo(int familyID, int userID,
			String familyName, String familySign, Location houseAddr)
			throws HttpRequestException;

	/**
	 * 异步方式更新家庭信息 [2.11.更新家庭信息]
	 * 
	 * @Title: updateFamilyInfo
	 * @Description: TODO
	 * @param @param familyID 家庭ID
	 * @param @param userID 用户ID
	 * @param @param familyName 家庭名称
	 * @param @param familySign 家庭签名
	 * @param @param Location 家庭地址
	 * @param @param handler
	 * @param @return
	 * @return {@code Future<ResponseBean<NULL>>}
	 * @throws
	 */
	public <T> Future<ResponseBean> updateFamilyInfo(final int familyID,
			final int userID, final String familyName, final String familySign,
			final Location houseAddr,
			final AsyncResponseCompletedHandler<T> handler) {
		handler.onStart(familyID,userID,familyName,familySign,houseAddr);
		Future<ResponseBean> future = executorProvider
				.doTask(new Callable<ResponseBean>() {

					@Override
					public ResponseBean call() {
						ResponseBean bean = null;
						try {
							bean = updateFamilyInfo(familyID, userID,
									familyName, familySign, houseAddr);
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.WebApi_Family_updateFamilyInfo_Service);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.WebApi_Family_updateFamilyInfo_Service);
						}
						return bean;
					}
				});
		return future;
	}

	/**
	 * 注销当前家庭 [2.12.注销家庭]
	 * 
	 * @Title: destroyFamily
	 * @Description: TODO
	 * @param @param familyID
	 * @param @return
	 * @param @throws HttpRequestException
	 * @return {@code ResponseBean<NULL>}
	 * @throws
	 */
	public abstract ResponseBean<?> destroyFamily(int familyID)
			throws HttpRequestException;

	/**
	 * 异步方式注销当前家庭 [2.12.注销家庭]
	 * 
	 * @Title: destroyFamily
	 * @Description: TODO
	 * @param @param familyID 家庭ID
	 * @param @param handler
	 * @param @return
	 * @return {@code Future<ResponseBean<NULL>>}
	 * @throws
	 */
	public <T> Future<ResponseBean<?>> destroyFamily(final int familyID,
			final AsyncResponseCompletedHandler<T> handler) {
		handler.onStart(familyID);
		Future<ResponseBean<?>> future = executorProvider
				.doTask(new Callable<ResponseBean<?>>() {

					@Override
					public ResponseBean call() {
						ResponseBean bean = null;
						try {
							bean = destroyFamily(familyID);
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.WebApi_Family_destroyFamily_Service);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.WebApi_Family_destroyFamily_Service);
						}
						return bean;
					}
				});
		return future;
	}

	/**
	 * 上传家庭头像 [2.13.家庭头像上传(新)]
	 * 
	 * @Title: uploadFamilyIcon
	 * @Description: TODO
	 * @param @param familyID 家庭ID
	 * @param @param instream 头像流
	 * @param @param filename 文件名
	 * @param @return
	 * @param @throws HttpRequestException
	 * @return {@code ResponseBean<NULL>}
	 * @throws
	 */
	public abstract ResponseBean<?> uploadFamilyIcon(int familyID,
			InputStream instream, String filename) throws HttpRequestException;

	public abstract ResponseBean<?> uploadFamilyIcon(int familyID, File file)
			throws HttpRequestException;

	public <T> Future<ResponseBean<?>> uploadFamilyIcon(final int familyID,
			final File file, final AsyncResponseCompletedHandler<T> handler) {
		handler.onStart(familyID,file);
		Future<ResponseBean<?>> future = executorProvider
				.doTask(new Callable<ResponseBean<?>>() {

					@Override
					public ResponseBean call() {
						ResponseBean bean = null;
						try {
							bean = uploadFamilyIcon(familyID, file);
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.WebApi_Family_uploadFamilyIcon_Service);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.WebApi_Family_uploadFamilyIcon_Service);
						}
						return bean;
					}
				});
		return future;
	}

	/**
	 * 异步方式上传家庭头像 [2.13.家庭头像上传(新)]
	 * 
	 * @Title: uploadFamilyIcon
	 * @Description: TODO
	 * @param @param familyID 家庭ID
	 * @param @param instream 头像流
	 * @param @param filename 文件名
	 * @param @param handler
	 * @param @return
	 * @return {@code Future<ResponseBean<NULL>>}
	 * @throws
	 */
	public <T> Future<ResponseBean<?>> uploadFamilyIcon(final int familyID,
			final InputStream instream, final String filename,
			final AsyncResponseCompletedHandler<T> handler) {
		handler.onStart(familyID);
		Future<ResponseBean<?>> future = executorProvider
				.doTask(new Callable<ResponseBean<?>>() {
					@Override
					public ResponseBean call() {
						ResponseBean bean = null;
						try {
							bean = uploadFamilyIcon(familyID, instream,
									filename);
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.WebApi_Family_uploadFamilyIcon_Service);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.WebApi_Family_uploadFamilyIcon_Service);
						}
						return bean;
					}
				});
		return future;
	}

	/**
	 * 获取家庭头像 [2.14.家庭头像下载(新)]
	 * 
	 * @Title: getFamilyIcon
	 * @Description: TODO
	 * @param @param familyID
	 * @param @return
	 * @param @throws HttpRequestException
	 * @return {@code ResponseBean<CHCareFileInStream> }
	 * @throws
	 */
	public abstract ResponseBean<?> getFamilyIcon(int familyID)
			throws HttpRequestException;

	/**
	 * 异步方式获取家庭头像 [2.14.家庭头像下载(新)]
	 * 
	 * @Title: getFamilyIcon
	 * @Description: TODO
	 * @param @param familyID
	 * @param @param handler
	 * @param @return
	 * @return {@code Future<ResponseBean<CHCareFileInStream>> }
	 * @throws
	 */
	public <T> Future<ResponseBean<?>> getFamilyIcon(final int familyID,
			final AsyncResponseCompletedHandler<T> handler) {
		handler.onStart(familyID);
		Future<ResponseBean<?>> future = executorProvider
				.doTask(new Callable<ResponseBean<?>>() {

					@Override
					public ResponseBean call() {
						ResponseBean bean = null;
						try {
							bean = getFamilyIcon(familyID);
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.WebApi_Family_getFamilyIcon_Service);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.WebApi_Family_getFamilyIcon_Service);
						}
						return bean;
					}
				});
		return future;
	}

	/**
	 * 删除家庭头像 [2.15. 删除家庭头像(新)]
	 * 
	 * @Title: deleteFamilyIcon
	 * @Description: TODO
	 * @param @param familyID
	 * @param @param handler
	 * @param @return
	 * @return {@code ResponseBean<NULL>}
	 * @throws
	 */
	public abstract ResponseBean<?> deleteFamilyIcon(int familyID)
			throws HttpRequestException;

	/**
	 * 异步方式删除家庭头像 [2.15. 删除家庭头像(新)]
	 * 
	 * @Title: deleteFamilyIcon
	 * @Description: TODO
	 * @param @param familyID
	 * @param @param handler
	 * @param @return
	 * @return {@code Future<ResponseBean<NULL>> }
	 * @throws
	 */
	public <T> Future<ResponseBean<?>> deleteFamilyIcon(final int familyID,
			final AsyncResponseCompletedHandler<T> handler) {
		handler.onStart(familyID);
		Future<ResponseBean<?>> future = executorProvider
				.doTask(new Callable<ResponseBean<?>>() {
					@Override
					public ResponseBean<?> call() {
						ResponseBean bean = null;
						try {
							bean = deleteFamilyIcon(familyID);
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.WebApi_Family_deleteFamilyIcon_Service);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.WebApi_Family_deleteFamilyIcon_Service);
						}
						return bean;
					}
				});
		return future;
	}

	/**
	 * 添加纪念日 [0.4.0]
	 * 
	 * @Title: addFamilyDates
	 * @Description: TODO
	 * @param FamilyDateView
	 * @param handler
	 * @return {@code ResponseBean<int>} 纪念日ID
	 * @throws
	 */
	public abstract ResponseBean<?> addFamilyDates(FamilyDateView famiDate)
			throws HttpRequestException;

	/**
	 * 异步添加纪念日 [0.4.0]
	 * 
	 * @Title: addFamilyDates
	 * @Description: TODO
	 * @param FamilyDateView
	 * @param handler
	 * @return {@code Future<ResponseBean<int>>} 纪念日ID
	 * @throws
	 */
	public <T> Future<ResponseBean<?>> addFamilyDates(
			final FamilyDateView famiDate,
			final AsyncResponseCompletedHandler<T> handler) {
		handler.onStart(famiDate);
		Future<ResponseBean<?>> future = executorProvider
				.doTask(new Callable<ResponseBean<?>>() {

					@Override
					public ResponseBean<?> call() throws Exception {
						// TODO Auto-generated method stub
						ResponseBean bean = null;
						try {
							bean = addFamilyDates(famiDate);
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.WebApi_Family_addFamilyDate_Service);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.WebApi_Family_addFamilyDate_Service);
						}
						return bean;
					}
				});
		return future;

	}

/**
	 * 删除纪念日 [0.4.0]
	 * 
	 * @Title: addFamilyDates
	 * @Description: TODO
	 * @param  id 纪念日id
	 * @param  handler
	 * @return {@code ResponseBean<NULL> 0成功，－1数据库错误，－7没有权限(你不是该家庭成员或该家庭不存在/已解散)
	 * @throws
	 */
	public abstract ResponseBean<?> DeleteFamilyDates(int id)
			throws HttpRequestException;

/**
	 * 异步删除纪念日 [0.4.0]
	 * 
	 * @Title: addFamilyDates
	 * @Description: TODO
	 * @param  id 纪念日id
	 * @param  handler
	 * @return {@code Future<ResponseBean<NULL>> 0成功，－1数据库错误，－7没有权限(你不是该家庭成员或该家庭不存在/已解散)
	 * @throws
	 */
	public <T> Future<ResponseBean<?>> DeleteFamilyDates(final int id,
			final AsyncResponseCompletedHandler<T> handler) {
		handler.onStart(id);
		Future<ResponseBean<?>> future = executorProvider
				.doTask(new Callable<ResponseBean<?>>() {
					@Override
					public ResponseBean<?> call() throws Exception {
						// TODO Auto-generated method stub
						ResponseBean bean = null;
						try {
							bean = DeleteFamilyDates(id);
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.WebApi_Family_deleteFamilyDate_Service);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.WebApi_Family_deleteFamilyDate_Service);
						}
						return bean;
					}
				});
		return future;

	}

	/**
	 * 获取纪念日 [0.4.0]
	 * 
	 * @Title: getFamilyDates
	 * @Description: TODO
	 * @param fid
	 *            家庭id
	 * @return {@code ResponseBean<List<FamilyDateView>>}
	 * @throws
	 */
	public abstract ResponseBean<?> getFamilyDates(int fid)
			throws HttpRequestException;

	/**
	 * 异步获取纪念日 [0.4.0]
	 * 
	 * @Title: getFamilyDates
	 * @Description: TODO
	 * @param fid
	 *            家庭id
	 * @param handler
	 * @return {@code Future<ResponseBean<List<FamilyDateView>>>}
	 * @throws
	 */
	public <T> Future<ResponseBean<?>> getFamilyDates(final int fid,
			final AsyncResponseCompletedHandler<T> handler) {
		handler.onStart(fid);
		Future<ResponseBean<?>> future = executorProvider
				.doTask(new Callable<ResponseBean<?>>() {

					@Override
					public ResponseBean<?> call() throws Exception {
						// TODO Auto-generated method stub
						ResponseBean bean = null;
						try {
							bean = getFamilyDates(fid);
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.WebApi_Family_getFamilyDate_Service);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.WebApi_Family_getFamilyDate_Service);
						}
						return bean;
					}
				});
		return future;

	}

	/**
	 * 修改纪念日 [0.4.0]
	 * 
	 * @Title: updateFamilyDates
	 * @Description: TODO
	 * @param FamilyDateView
	 *            正文中传递的FID, UID, Type会被忽略, 非自定义类型的Name会被忽略
	 * @return {@code ResponseBean<Null>}
	 * @throws
	 */
	public abstract ResponseBean<?> updateFamilyDates(FamilyDateView famiDate)
			throws HttpRequestException;

	/**
	 * 异步修改纪念日 [0.4.0]
	 * 
	 * @Title: updateFamilyDates
	 * @Description: TODO
	 * @param FamilyDateView
	 *            正文中传递的FID, UID, Type会被忽略, 非自定义类型的Name会被忽略
	 * @param handler
	 * @return {@code Future<ResponseBean<NULL>>}
	 * @throws
	 */
	public <T> Future<ResponseBean<?>> updateFamilyDates(
			final FamilyDateView famiDate,
			final AsyncResponseCompletedHandler<T> handler) {
		handler.onStart(famiDate);
		Future<ResponseBean<?>> future = executorProvider
				.doTask(new Callable<ResponseBean<?>>() {

					@Override
					public ResponseBean<?> call() throws Exception {
						// TODO Auto-generated method stub
						ResponseBean bean = null;
						try {
							bean = updateFamilyDates(famiDate);
							handler.onCompleted(
									bean,
									ChCareWepApiServiceType.WebApi_Family_updateFamilyDate_Service);
						} catch (HttpRequestException e) {
							handler.onThrowable(
									e,
									ChCareWepApiServiceType.WebApi_Family_updateFamilyDate_Service);
						}
						return bean;
					}
				});
		return future;

	}
}
