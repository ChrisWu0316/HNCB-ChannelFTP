package com.sas.channel.bean;

/**
 * SMS Bean
 * @author Chris
 *
 */
public class SMSbean {
	private String phone_no;		// 手機號碼
	private String customerid;		// 客戶編號
	private String message;			// 簡訊內容
	private String senddate;		// 發送日期(YYYYMMDD)
	private String sendTime;		// 發送時間(HHMMSS)
	private String sysid;			// 簡訊來源系統代碼
	private String servicetype;		// 業務性質代碼
	private String processedFlg;	// 預設為0,名單成功轉成文字檔後更新為1
	
	public String getPhone_no() {
		return phone_no;
	}
	public void setPhone_no(String phone_no) {
		this.phone_no = phone_no;
	}
	public String getCustomerid() {
		return customerid;
	}
	public void setCustomerid(String customerid) {
		this.customerid = customerid;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getSenddate() {
		return senddate;
	}
	public void setSenddate(String senddate) {
		this.senddate = senddate;
	}
	public String getSendTime() {
		return sendTime;
	}
	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}
	public String getSysid() {
		return sysid;
	}
	public void setSysid(String sysid) {
		this.sysid = sysid;
	}
	public String getServicetype() {
		return servicetype;
	}
	public void setServicetype(String servicetype) {
		this.servicetype = servicetype;
	}
	public String getProcessedFlg() {
		return processedFlg;
	}
	public void setProcessedFlg(String processedFlg) {
		this.processedFlg = processedFlg;
	}
	
	
	
}
