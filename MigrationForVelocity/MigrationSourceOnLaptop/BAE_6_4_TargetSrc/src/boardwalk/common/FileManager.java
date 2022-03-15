package boardwalk.common;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.*;

public class FileManager
{

	/**
	 * Check if a folder exists or not
	 */
	public static boolean checkExistsFileOrFolder(String dirName) throws SecurityException {
		boolean exists = false;
		
		if (dirName == null || dirName.trim().isEmpty()) {
			return exists;
		}
		
		File theDir = new File(dirName);

		if (theDir.exists()) {
    		exists = true;
    	}

		return exists;
	}

    /**
     * Creates a folder to desired location if it not already exists
     * dirName - full path to the folder
     * SecurityException - in case you don't have permission to create the folder
     */
	/*
	public static void createFolderIfNotExists(String dirName) throws SecurityException {
    	File theDir = new File(dirName);
    	if (!theDir.exists()) {
    		theDir.mkdir();
    	}
    }
	*/

    /**
     * Utility method to save InputStream data to target location/file
     * inStream - InputStream to be saved
     * target - full path to destination file
     */
	public static void saveToFile(InputStream inStream, String target) throws IOException {
		OutputStream out = null;
		int read = 0;
		byte[] bytes = new byte[1024];

		out = new FileOutputStream(new File(target));
		while ((read = inStream.read(bytes)) != -1) {
			out.write(bytes, 0, read);
		}
		out.flush();
		out.close();
	}
	
	public static void saveToFile(List<String> lines, String target) throws Exception {
		BufferedWriter writer = null;
		 try {
			if (target == null || target.trim().isEmpty()) {
				throw new Exception("File Path can not be null or empty.");
			}
		
			writer = new BufferedWriter(new FileWriter(target));
		    
		    for (String line : lines) {
		    	writer.write(line);
		    	writer.newLine();
		    }
		    writer.flush();
		} catch(Exception e) {
			System.out.println("ERROR: "+ e);
			throw e;
		} finally {
			writer.close();
		}
	}
	
	public static InputStream getInputStreamFromList(List<String> lines) throws Exception {
		try {
			 ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

			 for (String line : lines) {
				 byteArrayOutputStream.write(line.getBytes());
				 String newLine = System.getProperty("line.separator");
				 byteArrayOutputStream.write(newLine.getBytes());
			 }
	 
			 return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
		} catch(Exception e) {
			System.out.println("ERROR: "+ e);
			throw e;
		}
	}
	
	public static void copyFile(String sourceFilePath , String targetLocation) throws Exception {
       try {
    	   if (sourceFilePath == null || sourceFilePath.trim().isEmpty() || targetLocation == null || targetLocation.trim().isEmpty()) {
    		   throw new Exception("Invalid Source Or Target Location");
    	   }
    	   
    	   File sourceFile = new File(sourceFilePath); 
    	   File targetFileDirectory = new File(targetLocation);
    	   
    	   if (sourceFile.exists() && sourceFile.isFile()) {
    		   /*
    		   //Check target location exists or not
    		   if (!targetFileDirectory.exists()) {
    			   targetFileDirectory.mkdir();
               }
               
			   if (!targetLocation.endsWith("/")) {
    			   targetLocation = targetLocation + "/";
    		   }
    		   */
    		   
               InputStream in = new FileInputStream(sourceFile);
               OutputStream out = new FileOutputStream(targetLocation);

               byte[] buf = new byte[1024];
               int len;
               while ((len = in.read(buf)) > 0) {
                   out.write(buf, 0, len);
               }
               in.close();
               out.close();
           }
       } catch (Exception e) {
    	   System.out.println("ERROR: "+ e);
    	   throw e;
       }
    }
};