package com.mukund.booknetwork.book.file;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Date;


@Service
@RequiredArgsConstructor
public class FileStorageService {

    @Value("${application.file.uploads.photos-output-path}")
    private String fileUploadPath;



    public String saveFile(
         @NonNull MultipartFile sourceFile,
         @NonNull   Integer bookId,
         @NonNull   Integer userId) {
      final String fileUploadSubPath="users"+ File.separator + userId;
      return  uploadFile(sourceFile,fileUploadSubPath);

    }

    private String uploadFile(
            @NonNull MultipartFile sourceFile,
           @NonNull String fileUploadSubPath) {
        final String finalUploadPath=fileUploadPath+ File.separator +fileUploadSubPath;
        File targetFolder=new File(finalUploadPath);
        if(!targetFolder.exists()){
            boolean folderCreated=targetFolder.mkdirs();
            if(!folderCreated){
                System.out.println("Failed to create the Target Folder for file upload");
                return null;
            }

        }
        final String fileExtension=getFileExtension(sourceFile.getOriginalFilename());
        String targetFilePath=finalUploadPath  + File.separator+ System.currentTimeMillis() +"." +fileExtension;
      Path targetPath= Paths.get(targetFilePath);
      try{
          Files.write(targetPath,sourceFile.getBytes());
          System.out.println("file uploaded successfully at "+targetPath);
          return targetFilePath;
      }
      catch( IOException e){
          System.out.println("File could not be uploaded" );
          System.out.println(e);


      }



        return null;
    }

    private String getFileExtension(String originalFilename) {
        if(originalFilename==null || originalFilename.isEmpty()){
            return "";
        }
        int lastDotIndex=originalFilename.lastIndexOf(".");
        if(lastDotIndex==-1)
            return "";
        return originalFilename.substring(lastDotIndex+1).toLowerCase();
    }
}
