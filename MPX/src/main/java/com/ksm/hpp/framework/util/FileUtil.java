package com.ksm.hpp.framework.util;

import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ksm.hpp.service.com.BaseService;

public class FileUtil {
	
	protected static final Log log = LogFactory.getLog(FileUtil.class);
	
	/**
	 * @메소드명: saveFileOri
	 * @작성자: 김상민
	 * @생성일: 2022. 12. 14. 오후 4:26:53
	 * @설명: 물리적인 공간에 파일 저장
	 */
	public static void saveFileOri(InputStream fi, OutputStream fo) {
		try {
			int read = 0;
			byte[] bytes = new byte[1024];
			
			while((read = fi.read(bytes)) != -1) {
				fo.write(bytes, 0, read);
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		} finally {
			try { if(fi != null) fi.close(); } catch(Exception ignore) {};
			try { if(fo != null) fo.close(); } catch(Exception ignore) {};
		}
	}
}

	