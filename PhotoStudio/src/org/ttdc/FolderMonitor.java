package org.ttdc;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class FolderMonitor {
	List<Filehandle> currentFileHandles = new ArrayList<>();
	private int REFRESH_IN_SECONDS = 2;
	private final String path;
	private final ScheduledExecutorService scheduler;
	public FolderMonitor(String path) {
		this.path = path;
		scheduler = Executors.newScheduledThreadPool(1);
		
		System.err.println("Firing up a path monitor for path: " + path);
		
		scheduler.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				try {
					loadPath();
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}, 0, REFRESH_IN_SECONDS, TimeUnit.SECONDS);
	}
	
	boolean loadPath() throws IOException{
		Path start = Paths.get(path);
		List<Filehandle> fileHandles = new ArrayList<Filehandle>();

		Files.walkFileTree(start, new MyFileVisitor(fileHandles));
		
		Collections.sort(fileHandles);
		
		boolean updated = false;
		synchronized (currentFileHandles) {
			if(currentFileHandles.size() != fileHandles.size()){
				System.err.println("Loaded "+(fileHandles.size() - currentFileHandles.size())+" file(s).");
				currentFileHandles = fileHandles;
				updated = true;
			}
		}
		return updated;
	}
	
	private static class Filehandle implements Comparable<Filehandle>{
		private final String name;
		private final long timestamp;
		
		Filehandle(String name, long timestamp){
			this.name = name;
			this.timestamp = timestamp;
		}
		
		public String getName() {
			return name;
		}
		public long getTimestamp() {
			return timestamp;
		}
		
		@Override
		public int compareTo(Filehandle o) {
			Long us = o.getTimestamp();
			Long them = timestamp;
			return them.compareTo(us);
		}
	} 
	
	public static class MyFileVisitor extends SimpleFileVisitor<Path> {
		private final List<Filehandle> files;

		public MyFileVisitor(List<Filehandle> files) {
			this.files = files;
		}

		@Override
		public FileVisitResult visitFile(Path filePath,
				BasicFileAttributes attrs) throws IOException {
//			lines.add(filePath.getFileName() + " ; "
//					+ sdf.format(attrs.creationTime().toMillis()));
			files.add(new Filehandle(filePath.getFileName().toString(), attrs.creationTime().toMillis()));
			return FileVisitResult.CONTINUE;

		}
	}
	
	public List<String> getAllFiles(){
		List<String> files = new ArrayList<>();
		synchronized (currentFileHandles) {
			for(Filehandle handle : currentFileHandles){
				files.add(handle.getName());
			}
		}
		return files;
	}
	
	public List<String> getAllFilesSince(String file){
		List<String> files = new ArrayList<>();
		synchronized (currentFileHandles) {
			//If the start point is the end, return an empty list
			if(file.equals(getLatest())){
				return files;
			}
			
			//Get a list with all files
			for(Filehandle handle : currentFileHandles){
				files.add(handle.getName());
			}
			
			//If the file that they want start from exists split and return everything after that file
			if(files.contains(file)){
				files = files.subList(files.indexOf(file)+1, files.size());
			}
		}
		return files;
	}
	
	public String getLatest(){
		synchronized (currentFileHandles) {
			if(currentFileHandles.size() > 0){
				return currentFileHandles.get(currentFileHandles.size() - 1).getName();
			}
			else{
				return null;
			}
		}
	}
	
	public String getPath(){
		return path;
	}
	
}	
