package org.crawler.core.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.log4j.Logger;
import org.crawler.core.utils.FileDownloadConfig;
import org.crawler.core.utils.HttpClientManager;

public class HttpRequestImpl {

	private Logger logger = Logger.getLogger(HttpRequestImpl.class);
	
	private int connectTimeOut;
	private int socketTimeOut;
	private int maxRetryTime;
	private long requestBytesSize;
	
	private CloseableHttpClient httpClient = HttpClientManager.getHttpClient();
	
	public HttpRequestImpl(FileDownloadConfig config) {
		connectTimeOut = config.getConnectTimeout();
		socketTimeOut = config.getSocketTimeout();
		maxRetryTime = config.getMaxRetryCount();
		requestBytesSize = config.getRequestBytesSize();
	}
	
	public void downloadPartFile(int id, String url, File file, long start, long end){
		RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile(file, "rws");
		} catch (FileNotFoundException e) {
			logger.error("file not found ..."+file);
			throw new IllegalArgumentException();
		}
		int retry = 0;
		long pos = start;
		while(pos < end ) {
			long end_index = pos + requestBytesSize;
			if(end_index>end) {
				end_index = end;
			}
			boolean success = false;
			success = requestByRange(url,raf,pos,end_index);
			if(success){
				pos += requestBytesSize;
				retry = 0;
			}else{
				if(retry < maxRetryTime){
					retry++;
					logger.warn("线程:" + id +",url:"+url+",range:"+pos+","+end_index+" 下载失败,重试"+retry+"次");
				}else{
					logger.warn("线程:" + id +",url:"+url+",range:"+pos+","+end_index+" 下载失败,放弃重试!");
				}
			}
		}
	}

	private boolean requestByRange(String url, RandomAccessFile raf, long start,
			long end) {
		HttpGet httpGet = new HttpGet();
		httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.152 Safari/537.36");
		httpGet.setHeader("Range","bytes="+start + "-"+end);
		
		RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(connectTimeOut)
				.setSocketTimeout(socketTimeOut).build();
		httpGet.setConfig(requestConfig);
		
		CloseableHttpResponse response = null;
		
		try {
			response =  httpClient.execute(httpGet);
			int code = response.getStatusLine().getStatusCode();
			if(HttpStatus.SC_OK==code || HttpStatus.SC_PARTIAL_CONTENT ==code) {
				HttpEntity entity = response.getEntity();
				if(null != entity) {
					InputStream in = entity.getContent();
					raf.seek(start);
					byte[] buffer = new byte[1024];
					int len;
					while((len=in.read(buffer))!=-1){
						raf.write(buffer,0,len);
					}
					return true;
				}else {
					logger.warn("response entity is null,url:"+url);
				}
			}else{
					logger.warn("response entity is null,url:"+url);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			IOUtils.closeQuietly(response);
		}
		
		return false;
	}

	public long getFileSize(String url) {
		int retry = 0 ;
		long filesize = 0;
		while(retry < maxRetryTime) {
			filesize = getContentLength(url);
			if(filesize>0){
				break;
			}else{
				retry++;
				logger.warn("get File Size failed,retry:"+retry);
			}
		}
		logger.info(filesize);
		return filesize;
	}

	private long getContentLength(String url) {
 		HttpGet httpGet = new HttpGet(url);
		httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.152 Safari/537.36");
		
		RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(connectTimeOut)
				.setSocketTimeout(socketTimeOut).build();
		httpGet.setConfig(requestConfig);
		
		CloseableHttpResponse response = null;
		
		try {
			response =httpClient.execute(httpGet);
			
			int code = response.getStatusLine().getStatusCode();
			if(HttpStatus.SC_OK==code) {
				HttpEntity entity = response.getEntity();
				if(null!=entity){
					return entity.getContentLength();
				}
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			IOUtils.closeQuietly(response);
		}
		
		return -1;
	}
	
	public void close() {
		if(null!=httpClient) {
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			httpClient = null;
		}
	}
}
