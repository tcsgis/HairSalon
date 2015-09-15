/**     
 * @Title: ChCareWepApiServiceType.java  
 * @Package cn.changhong.chcare.core.webapi.server  
 * @Description: TODO  
 * @author guoyang2011@gmail.com    
 * @date 2014-9-27 下午2:07:16  
 * @version V1.0     
*/  
package cn.changhong.chcare.core.webapi.server;  
  
/**  
 * @ClassName: ChCareWepApiServiceType  
 * @Description: TODO  
 * @author guoyang2011@gmail.com  
 * @date 2014-9-27 下午2:07:16  
 *     
 */
public enum ChCareWepApiServiceType {
	
	//salon api
	//account
	Salon_Account_getVetufyCode("Salon_Account_getVetufyCode"),
	Salon_Account_VetufyCode("Salon_Account_VetufyCode"),
	Salon_Account_register("Salon_Account_register"),
	Salon_Account_resetSecret("Salon_Account_resetSecret"),
	Salon_Account_verifyOldSecret("Salon_Account_verifyOldSecret"),
	Salon_Account_changeSecret("Salon_Account_changeSecret"),
	Salon_Account_new_login("Salon_Account_new_login"),
	Salon_Account_logout("Salon_Account_logout"),
	Salon_Account_getUser("Salon_Account_getUser"),
	Salon_Account_updateSelfMg("Salon_Account_updateSelfMg"),
	Salon_Account_searchUser("Salon_Account_searchUser"),
	Salon_Account_getBannerUsers("Salon_Account_getBannerUsers"),
	Salon_Account_getBanners("Salon_Account_getBanners"),
	
	Salon_File_uploadFiles("Salon_File_uploadFiles"),
	Salon_File_getFile("Salon_File_getFile"),
	
	Salon_Admin_getUnchecked("Salon_Admin_getUnchecked"),
	Salon_Admin_check("Salon_Admin_check"),
	Salon_Admin_updateAdPics("Salon_Admin_updateAdPics"),
	Salon_Admin_updateMallPics("Salon_Admin_updateMallPics"),
	
	Salon_Salon_uploadMyBarber("Salon_Salon_uploadMyBarber"),
	Salon_Salon_deleteMyBarber("Salon_Salon_deleteMyBarber"),
	Salon_Salon_deleteFreeBarber("Salon_Salon_deleteFreeBarber"),
	Salon_Salon_getMyBarber("Salon_Salon_getMyBarber"),
	Salon_Salon_getMyBarberCount("Salon_Salon_getMyBarberCount"),
	Salon_Salon_getFreeBarber("Salon_Salon_getFreeBarber"),
	
	Salon_Barber_bindSalon("Salon_Barber_bindSalon"),
	Salon_Barber_unbindSalon("Salon_Barber_unbindSalon"),
	Salon_Barber_getBindSalons("Salon_Barber_getBindSalons"),
	
	Salon_Bid_getBids("Salon_Bid_getBids"),
	Salon_Bid_getCustomBid("Salon_Bid_getCustomBid"),
	Salon_Bid_publishBid("Salon_Bid_publishBid"),
	Salon_Bid_confirmBid("Salon_Bid_confirmBid"),
	Salon_Bid_cancelBid("Salon_Bid_cancelBid"),
	Salon_Bid_doBid("Salon_Bid_doBid"),
	Salon_Bid_getBid("Salon_Bid_getBid"),
	Salon_Bid_sendCoupon("Salon_Bid_sendCoupon"),
	Salon_Bid_getCustomCoupon("Salon_Bid_getCustomCoupon"),
	Salon_Bid_useCoupon("Salon_Bid_useCoupon"),
	Salon_Bid_getBarberBid("Salon_Bid_getBarberBid"),
	
	Salon_Order_createOrder("Salon_Order_createOrder"),
	Salon_Order_getMyOrders("Salon_Order_getMyOrders"),
	Salon_Order_cancelOrder("Salon_Order_cancelOrder"),
	Salon_Order_acceptOrder("Salon_Order_acceptOrder"),
	Salon_Order_rejectOrder("Salon_Order_rejectOrder"),
	Salon_Order_finishOrder("Salon_Order_finishOrder"),
	Salon_Order_shareOrder("Salon_Order_shareOrder"),
	
	
	
	
	
	
	WebApi_Family_applyJoinFamily_Service("applyJoinFamily"),
	WebApi_Family_changeUserFamilyMemberNickName_Service("changeUserFamilyMemberNickName"),
	WebApi_Family_createFamily_Service("createFamily"),
	WebApi_Family_destroyFamily_Service("destroyFamily"),
	WebApi_Family_getAllFamilyInfo_Service("getAllFamilyInfo"),
	WebApi_Family_getFamilyIcon_Service("getFamilyIcon"),
	WebApi_Family_getFamilyMembers_Service("getFamilyMembers"),
	WebApi_Family_inviteJoinFamily_Service("inviteJoinFamily"),
	WebApi_Family_joinFamilyAllowByMaster_Service("joinFamilyAllowByMaster"),
	WebApi_Family_removeUserByMaster_Service("removeUserByMaster"),
	WebApi_Family_searchFamilys_Service("searchFamilys"),
	WebApi_Family_updateFamilyInfo_Service("updateFamilyInfo"),
	WebApi_Family_uploadFamilyIcon_Service("uploadFamilyIcon"),
	WebApi_Family_deleteFamilyIcon_Service("deleteFamilyIcon"),
	WebApi_Family_addFamilyDate_Service("addFamilyDates"),
	WebApi_Family_deleteFamilyDate_Service("DeleteFamilyDates"),
	WebApi_Family_getFamilyDate_Service("getFamilyDates"),
	WebApi_Family_updateFamilyDate_Service("updateFamilyDates"),
	
	WebApi_FPhone_deleteBindFPhone_Service("deleteBindFPhone"),
	WebApi_FPhone_deleteBindFPhoneFence_Service("deleteBindFPhoneFence"),
	WebApi_FPhone_getAllBindFPhonePosition_Service("getAllBindFPhonePosition"),
	WebApi_FPhone_getAllBindFPhones_Service("getAllBindFPhones"),
	WebApi_FPhone_getBindFPhoneAllPhoneNum_Service("getBindFPhoneAllPhoneNum"),
	WebApi_FPhone_getBindFPhoneFence_Service("getBindFPhoneFence"),
	WebApi_FPhone_getBindFPhoneHistoryPositions_Service("getBindFPhoneHistoryPositions"),
	WebApi_FPhone_getBindFPhonePosition_Service("getBindFPhonePosition"),
	WebApi_FPhone_getBindFPhonePositionMode_Service("getBindFPhonePositionMode"),
	WebApi_FPhone_updateBindFPhone_Service("updateBindFPhone"),
	WebApi_FPhone_updateBindFPhoneFence_Service("updateBindFPhoneFence"),
	WebApi_FPhone_updateBindFPhoneNickInfo_Service("updateBindFPhoneNickInfo"),
	WebApi_FPhone_updateBindFPhonePositionMode_Service("updateBindFPhonePositionMode"),
	WebApi_FPhone_unBindPhone_Service("unBindPhone"),

	WebApi_PhotoWall_deleteFamilyPhoto_Service("deleteFamilyPhoto"),
	WebApi_PhotoWall_getFamilyPhotosInfo_Service("getFamilyPhotosInfo"),
	WebApi_PhotoWall_uploadFamilyPhoto_Service("uploadFamilyPhoto"),
	
	WebApi_Account_getUserIcon_Service("getUserIcon"),
	WebApi_Account_login_Service("login"),
	WebApi_Account_new_login_Service("new_login"),
	WebApi_Account_logout_Service("logout"),
	WebApi_Account_registerStage1_Service("registerStage1"),
	WebApi_Account_registerStage2_Service("registerStage2"),
	WebApi_Account_registerStage3_Service("registerStage3"),
	WebApi_Account_searchUserInfo_Service("searchUserInfo"),
	WebApi_Account_searchUsers_Service("searchUsers"),
	WebApi_Account_updateFamilyWealth_Service("updateFamilyWealth"),
	WebApi_Account_updateNewPassword_Service("updateNewPassword"),
	WebApi_Account_updateUserInfo_Service("updateUserInfo"),
	WebApi_Account_uploadUserPhoto_Service("uploadUserPhoto"),
	WebApi_Account_isSignIn_Service("isSignIn"),
	WebApi_Account_setNewPassword_Service("setNewPassword"),
	WebApi_Account_getUserInfoById_Service("getUserInfoById"),
	WebApi_Account_donateGoldCoin_Service("donateGoldCoin"),
	WebApi_Account_userTreasure_Service("userTreasure"),
	
	
	
	WebApi_Location_searchUserHistoryPosition_Service("searchUserHistoryPosition"),
	WebApi_Location_searchUsersLastLocation_Service("searchUsersLastLocation"),
	WebApi_Location_updateUserCurrentLocation_Service("updateUserCurrentLocation"),
	WebApi_Location_uploadShareMsgToThirdPlatformLog_Service("uploadShareMsgToThirdPlatformLog"),
	
	WebApi_OfflineMsg_getUserOfflineMessage_Service("getUserOfflineMessage"),
	WebApi_OfflineMsg_pollingMessage_Service("pollingMessage"),
	WebApi_OfflineMsg_markMessage_Service("markMessage"),
	
	WebApi_Health_addUserNewHealthInfo_Service("addUserNewHealthInfo"),
	WebApi_Health_searchUserHealthInfo_Service("searchUserHealthInfo"),
	WebApi_Health_searchUserHealthInfos_Service("searchUserHealthInfos"), 
	
	WebApi_FamilyMsgBoard_getFamilyNote_Service("getFamilyNote"), 
	WebApi_FamilyMsgBoard_uploadFamilyNote_Service("uploadFamilyNote"),
	WebApi_FamilyMsgBoard_uploadFamilyNoteComment_Service("uploadFamilyNoteComment"), 
	WebApi_FamilyMsgBoard_deleteFamilyNote_Service("deleteFamilyNote"),
	WebApi_FamilyMsgBoard_deleteFamilyNoteComment_Service("deleteFamilyNoteComment"),
	
	WebApi_FamilyDiary_addPersonalDiary("addPersonalDiary"),
	WebApi_FamilyDiary_getFamilyDiary("getFamilyDiary"),
	WebApi_FamilyDiary_deletePersonalDiary("deletePersonalDiary"),
	WebApi_FamilyDiary_getPersonalDiary("getPersonalDiary"),
	WebApi_FamilyDiary_addDiaryComment("addDiaryComment"),
	WebApi_FamilyDiary_getDiaryComment("getDiaryComments"),
	WebApi_FamilyDiary_getPictureByID("getPicFileStream"),
	WebApi_FamilyDiary_getDiaryByID("getDiary"),
	WebApi_FamilyDiary_getCommentsMentionedMe("getCommentsMentionedMe"),
	
	WebApi_Notify_getCommentsMentionedMe("getCommentsMentionedMe"),
	
	WebApi_LifeRange_publishNewThreadView("publishNewBBSView"),
	WebApi_LifeRange_DeleteThreadView("deleteBBSView"),
	WebApi_LifeRange_ReplyThread("addReplyView"),
	WebApi_LifeRange_SubReplyThread("addSubReplyThread"),
	WebApi_LifeRange_AddFavor("addFavor"),
	WebApi_LifeRange_DeleteFavor("deleteFavor"),
	WebApi_LifeRange_getFavor("getFavor"),
	WebApi_LifeRange_GetCategorys("getCategorys"),
	
	WebApi_AppManager_putFeedback_Service("AppManager_putFeedback"),
	WebApi_getAppLastVersionInfo_putFeedback_Service("getAppLastVersionInfo_putFeedback");
	
	
	 private String value;
	 private ChCareWepApiServiceType(String value){
		 this.value=value;
	 }
	 public String getValue(){
		 return value;
	 }
	
	
}
