package com.sas.channel.business;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.log4j.Logger;

import com.sas.channel.bean.SMSbean;
import com.sas.channel.commapi.FTPProcess;
import com.sas.channel.commapi.FileProcess;

public class SMSMain {
	static Logger log = Logger.getLogger(SMSMain.class);

	// 實體檔案路徑
	private static String smsPath = "D:\\SAS_CM\\NHFHC\\Channel\\SMS\\EDW\\";
	private static String smsPathFTP = "\\upload\\";
	private static String smsPathBackup = "D:\\SAS_CM\\NHFHC\\Channel\\SMS\\Backup\\";
	
	private static  FTPClient ftpClinet = new FTPClient();
	private static  FTPProcess ftpProcess = new FTPProcess();

	public static void main(String args[]) {		
		log.info("------------------------Start SMSMain -------------------------------");
		// 掃描SMS資料夾
		try {
			List<File> localFileList = checkLocalFileExists(smsPath);
			for (File localFile : localFileList) {
				FileReader fr = new FileReader(localFile);
				BufferedReader br = new BufferedReader(fr);
				String line = "";
				List<SMSbean> smsBeanList = new ArrayList<SMSbean>();
				int index = 0;
				while ((line = br.readLine()) != null) {
					if (index != 0) {
						SMSbean be = getDetail(line);
						smsBeanList.add(be);
					}
					index++;
				}
				br.close();
				fr.close();
				
				String smmServerType = smsBeanList.get(0).getServicetype();
				
				DateFormat yyyyMMdd = new SimpleDateFormat("yyyyMMdd");
				Calendar cal = Calendar.getInstance();
				String yyyymmdd = yyyyMMdd.format(cal.getTime());
				String fileName = yyyymmdd + "_" + smmServerType + ".txt";
				// 產出SMS文字檔
				smsOutput(smsBeanList, smsPath+"/"+fileName);
				// 上傳至SMS FTP
				ftpClinet = ftpProcess.ConnectionFTP();
				File smsFile = new File(smsPath+"/"+fileName); 
				ftpProcess.FTPUploadFile(smsFile, smsPathFTP);

				// 進行備份
				new FileProcess().copyfile(smsPath + localFile.getName(), smsPathBackup + localFile.getName(), true);
				log.info("檔案備份至: " + smsPathBackup + localFile.getName());	
				new FileProcess().copyfile(smsPath + fileName, smsPathBackup + fileName, true);
				log.info("檔案備份至: " + smsPathBackup + fileName);	
			}
		} catch (IOException ex) {
			log.error("Error: " + ex.getMessage());
			ex.printStackTrace();
		} catch (Exception e) {
			log.error("Error: " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				if (ftpClinet.isConnected()) {
					ftpClinet.logout();
					ftpClinet.disconnect();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		log.info("------------------------End SMSMain -------------------------------");
	}
	
	/**
	 * 檢核local端的檔存是否存在
	 * @param path
	 * @return map: txt(File), result(boolean)
	 * @throws IOException 
	 */
	private static List<File> checkLocalFileExists(String path) throws IOException {
		log.info("=========Check Local File Exits Start============");
		List<File> localFileList = new ArrayList<File>();
		File localFileDirectory = new File(path+"/");
		File[] listOfFiles = localFileDirectory.listFiles();
		
		if (null != listOfFiles && listOfFiles.length > 0) {
			for (File localFile : listOfFiles) {
				if(!localFile.isDirectory() && localFile.getName().indexOf("_DONE") > 0) {
					localFileList.add(localFile);
				}
			}
		}
		log.info("Need to be processing EDW done file:"+localFileList.size());
	
		log.info("=========Check Local File Exits End============");
		return localFileList;
	}
	
	/**
	 * 將資料列拆開為Channel資料
	 * @param dataLine
	 * @return
	 */
	public static SMSbean getDetail(String dataLine) {
		SMSbean be = new SMSbean();
		
		String[] data = dataLine.split(",");
		
		be.setPhone_no(data[1]);		// 手機號碼
		be.setCustomerid(data[2]);		// 客戶編號
		be.setMessage(data[3]);			// 簡訊內容
		be.setSenddate(data[4]);		// 發送日期(YYYYMMDD)
		be.setSendTime(data[5]);		// 發送時間(HHMMSS)
		be.setSysid(data[6]);			// 簡訊來源系統代碼
		be.setServicetype(data[7]);		// 業務性質代碼
//		be.setProcessedFlg(data[8]);	// 預設為0,名單成功轉成文字檔後更新為1
		
		return be;
	}
	
	/**
	 * 產出SMS上傳檔案
	 * @param smsBeanList
	 * @param outputFilePath
	 */
	public static void smsOutput(List<SMSbean> smsBeanList, String outputFilePath) throws Exception{
		try {			
//			File outFile = new File(outputFilePath);
//			BufferedWriter fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile)));			

			FileOutputStream fos = new FileOutputStream(outputFilePath);
			final byte[] bom = new byte[] {(byte)0xEF, (byte)0xBB, (byte)0xBF};
			fos.write(bom);
			Writer out = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));
			
			String header = "";
			Integer index = 0;
			
			for (SMSbean be : smsBeanList) {
				if ("".equals(header)) {
					header+=be.getSenddate()+be.getSendTime()+be.getSysid()+be.getServicetype();
//					fw.write(header);
//					fw.newLine();
					out.append(header);
					out.append("\r\n");
				}
//				fw.write(be.getPhone_no()+"\t"+be.getCustomerid()+"\t"+be.getMessage());
//				fw.newLine();
				out.append(be.getPhone_no()+"\t"+be.getCustomerid()+"\t"+be.getMessage());
				out.append("\r\n");
				index++;
			}
//			fw.write("#"+String.format("%7s", index.toString()).replaceAll(" ", "0"));
//			fw.newLine();
			out.append("#"+String.format("%7s", index.toString()).replaceAll(" ", "0"));
			out.append("\r\n");
			String fileName = outputFilePath.substring(outputFilePath.lastIndexOf("/"), outputFilePath.length());
//			fw.write(fileName);
//			fw.newLine();
			out.append(fileName);
			out.append("\r\n");

//			fw.flush();
//			fw.close();
			out.flush();
			out.close();
			fos.flush();
			fos.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info(ex);
			throw ex;
		}		
	}
}