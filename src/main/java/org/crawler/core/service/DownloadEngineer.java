package org.crawler.core.service;

import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.crawler.core.utils.FileDownloadConfig;

public class DownloadEngineer {

	private Logger logger = Logger.getLogger(DownloadEngineer.class);
	
	private FileDownloadConfig config;
	private Executors pool;
	
	
}
