package com.niwj.control;

import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.apache.commons.io.FileUtils;
public class StorageUtil {

	
	/**
	 * 遍历 "system/etc/vold.fstab” 文件，获取全部的Android的挂载点信息
	 * 
	 * @return
	 */
	private static ArrayList<String> getDevMountList() {
//		String[] toSearch = FileUtils.readFile("/etc/vold.fstab").split(" ");
		String[] toSearch={""};
		try {
			toSearch = FileUtils.readFileToString(new File("/etc/vold.fstab")).split(" ");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ArrayList<String> out = new ArrayList<String>();
		for (int i = 0; i < toSearch.length; i++) {
			if (toSearch[i].contains("dev_mount")) {
				if (new File(toSearch[i + 2]).exists()) {
					out.add(toSearch[i + 2]);
				}
			}
		}
		return out;
	}
	/**
	 * 获取扩展SD卡存储目录
	 * 
	 * 如果有外接的SD卡，并且已挂载，则返回这个外置SD卡目录
	 * 否则：返回内置SD卡目录
	 * 
	 * @return
	 */
	public static String getExternalSdCardPath() {
		
//		if (SDCardUtils.isMounted()) {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			File sdCardFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
			return sdCardFile.getAbsolutePath();
		}

		String path = null;

		File sdCardFile = null;

		ArrayList<String> devMountList = getDevMountList();

		for (String devMount : devMountList) {
			File file = new File(devMount);

			if (file.isDirectory() && file.canWrite()) {
				path = file.getAbsolutePath();

				String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
				File testWritable = new File(path, "test_" + timeStamp);

				if (testWritable.mkdirs()) {
					testWritable.delete();
				} else {
					path = null;
				}
			}
		}

		if (path != null) {
			sdCardFile = new File(path);
			return sdCardFile.getAbsolutePath();
		}

		return null;
	}
}
