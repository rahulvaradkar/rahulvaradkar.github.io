package io.swagger.api;

import java.io.*;
import java.util.*;

import javax.ws.rs.core.*;
import javax.ws.rs.*;

import org.glassfish.jersey.media.multipart.*;

import io.swagger.model.ErrorRequestObject;

import boardwalk.connection.BoardwalkConnection;
import boardwalk.rest.*;

import boardwalk.common.BoardwalkUtility;
import boardwalk.common.FileManager;

@Path("/deployPackage")
public class PackageDeploymentApi{

	@POST
	@Path("/file")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces({ "application/json" })

	public Response uploadFile(
        @FormDataParam("file")			InputStream					uploadedInputStream,
        @FormDataParam("file")			FormDataContentDisposition	fileDetail,
		@HeaderParam("Authorization")	String						authBase64String
	) {
		ErrorRequestObject ero = new ErrorRequestObject();
		ArrayList<ErrorRequestObject> eros = new ArrayList<>();

		System.out.println("authBase64String:" + authBase64String);

		if (authBase64String == null || authBase64String.isEmpty()){
			System.out.println("Missing Authorization in Header");

			ero.setError("Missing Authorization in Header");
			ero.setPath("Header:Authorization");
			ero.setProposedSolution("Authorization Header should contain user:pwd:nhPath as Base64 string");
			eros.add(ero);

			return Response.status(400).entity(eros).build();
		}

		// check if all form parameters are provided
		if (uploadedInputStream == null || fileDetail == null) {
			ero.setError("Invalid form data");
			ero.setPath("uploadedInputStream or fileDetail");
			eros.add(ero);

			return Response.status(400).entity(eros).build();
		}

		HashMap<String, String> responseHash = new HashMap<>();

		String deploymentPackageRepository = null;
		String uploadedFileLocation = null;

		try {
			//Get the Destination Folder location from Properties File
			deploymentPackageRepository = BoardwalkUtility.getBoardwalkProperty("deploymentPackageRepository");
			System.out.println("uploadFile - deploymentPackageRepository: " + deploymentPackageRepository);

			//Check if the Destination Folder is valid
			if (!FileManager.checkExistsFileOrFolder(deploymentPackageRepository))	{
				System.out.println("uploadFile - deploymentPackageRepository is an invalid property");

				ero.setError("Error");
				ero.setPath("Invalid deploymentPackageRepository");
				eros.add(ero);
				return Response.status(500).entity(eros).build();
			}

			//Save the selected file on the Server
 			uploadedFileLocation = deploymentPackageRepository + fileDetail.getFileName();
 			System.out.println("uploadFile - uploadedFileLocation " + uploadedFileLocation);
			FileManager.saveToFile(uploadedInputStream, uploadedFileLocation);

			responseHash.put("filePath", uploadedFileLocation);

			return Response.ok(responseHash).build();
		} catch (Exception e) {
			e.printStackTrace();

			ero.setError("Internal Server Error");
			ero.setPath("Internal Server Error");
			eros.add(ero);
			return Response.status(500).entity(eros).build();
		}
    }
}