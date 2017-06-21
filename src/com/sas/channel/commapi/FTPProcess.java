package com.sas.channel.commapi;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.log4j.Logger;

/**
 * FTP 上傳/下載 功能
 * 
 * @author Chris Wu
 * 
 */
public class FTPProcess {
	static Logger Log = Logger.getLogger(FTPProcess.class);
	
	/**
	 * 簡訊通路測試環境FTP帳號 密碼
	 * Server：10.8.220.47
	 * 帳號：HNCBUSER
	 * 密碼：FDre43..
	 */
	
	private String server = "10.8.220.47";
	private int port = 21;
	private String user = "HNCBUSER";
	private String pass = "FDre43..";

	FTPClient ftpClinet = new FTPClient();

	/**
	 * FTP Connection
	 * 
	 * @param channelType
	 * @return
	 * @throws IOException
	 */
	public FTPClient ConnectionFTP() throws IOException {
		Log.info("--------Connnection Ftp-----------");
		ftpClinet.connect(server, port);
		ftpClinet.login(user, pass);
		ftpClinet.enterLocalPassiveMode();
		ftpClinet.setFileType(FTP.BINARY_FILE_TYPE);
		return ftpClinet;
	}
	
	/**
	 * 上傳檔案
	 * @param loacalFile
	 * @param uploadFilePath
	 */
	public void FTPUploadFile(File loacalFile, String uploadFilePath) throws IOException {
		Log.info("==========FTP Upload Start===============");
		Log.debug("uploadFilePath : " + uploadFilePath);
		Log.debug("file name : " + loacalFile.getName());
		try {
			InputStream inputStream = new FileInputStream(loacalFile);
			ftpClinet.changeWorkingDirectory(uploadFilePath);
			Log.info("FTP working directory: " + ftpClinet.printWorkingDirectory());
			ftpClinet.setBufferSize(1024);
			boolean done = ftpClinet.storeFile(new String(loacalFile.getName().getBytes("MS950"),"iso-8859-1"), inputStream);
			inputStream.close();
			
			if (done) {
				Log.info("The file is uploaded successfully.");
			} else {
				Log.info("The file is uploaded fail.");
			}
		} catch (IOException ex) {
			Log.error("Error: " + ex.getMessage());
			ex.printStackTrace();
			throw ex;
		} 
		Log.info("========FTP Upload End===========");
	}
}
