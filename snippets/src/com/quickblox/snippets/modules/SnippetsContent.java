package com.quickblox.snippets.modules;

import android.content.Context;
import com.quickblox.core.QBCallbackImpl;
import com.quickblox.core.result.QBStringResult;
import com.quickblox.core.result.Result;
import com.quickblox.internal.core.helper.ContentType;
import com.quickblox.internal.core.helper.FileHelper;
import com.quickblox.internal.core.request.QBPagedRequestBuilder;
import com.quickblox.module.content.QBContent;
import com.quickblox.module.content.model.QBFile;
import com.quickblox.module.content.result.*;
import com.quickblox.snippets.R;
import com.quickblox.snippets.Snippet;
import com.quickblox.snippets.Snippets;

import java.io.File;
import java.io.InputStream;

/**
 * User: Oleg Soroka
 * Date: 11.10.12
 * Time: 13:03
 */
public class SnippetsContent extends Snippets {

    public SnippetsContent(Context context) {
        super(context);

        snippets.add(uploadFileTask);
        snippets.add(downloadFileTask);
        snippets.add(getFiles);
        snippets.add(getTaggedList);
        snippets.add(getFileWithId);
        snippets.add(createFile);
        snippets.add(uploadFile);
        snippets.add(declareFileUpload);
        snippets.add(updateFile);
        snippets.add(getFileObjectAccess);
        snippets.add(downloadFileWithUID);
        snippets.add(deleteFile);
        snippets.add(incrementRefCount);
        snippets.add(getFileDownloadLink);
        snippets.add(downloadFile);


    }


    String uid = null;
    int fileID;
    File file = null;
    String params;
    QBFile qbfile;
    int fileSize = 0;

    Snippet getFileDownloadLink = new Snippet("get file download link TASK") {
        @Override
        public void execute() {
            if (fileID != 0) {
                QBContent.getFileDownloadLink(fileID, new QBCallbackImpl() {
                    @Override
                    public void onComplete(Result result) {

                        QBStringResult qbStringResult = ((QBStringResult) result);
                        if (result.isSuccess()) {
                            System.out.println(">>> download link" + qbStringResult.toString());
                        } else {
                            handleErrors(result);
                        }
                    }

                });
            }
        }
    };


    Snippet incrementRefCount = new Snippet("increment ref count") {
        @Override
        public void execute() {
            if (fileID != 0) {
                QBContent.incrementRefCount(fileID, new QBCallbackImpl() {
                    @Override
                    public void onComplete(Result result) {
                        if (result.isSuccess()) {
                            System.out.println(">>> count of ref increment successfully" + result.toString());
                        } else {
                            handleErrors(result);
                        }
                    }
                });
            }
        }
    };

    Snippet deleteFile = new Snippet("delete file") {
        @Override
        public void execute() {
            if (fileID != 0) {
                QBContent.deleteFile(fileID, new QBCallbackImpl() {
                    @Override
                    public void onComplete(Result result) {

                        if (result.isSuccess()) {
                            fileID = 0;
                            System.out.println(">>> file deleted successfully");
                        } else {
                            handleErrors(result);
                        }
                    }

                });
            }
        }
    };

    Snippet downloadFileWithUID = new Snippet("download file with UID") {
        @Override
        public void execute() {
            if (uid != null) {
                QBContent.downloadFile(uid, new QBCallbackImpl() {
                    @Override
                    public void onComplete(Result result) {
                        QBFileDownloadResult downloadResult = (QBFileDownloadResult) result;
                        if (result.isSuccess()) {
                            System.out.println(">>> file downloaded successfully" + downloadResult.getContent().toString());
                        } else {
                            handleErrors(result);
                        }
                    }

                });
            }
        }
    };

    Snippet getFileObjectAccess = new Snippet("get file object access") {
        @Override
        public void execute() {
            if (fileID != 0) {
                QBContent.getFileObjectAccess(fileID, new QBCallbackImpl() {
                    @Override
                    public void onComplete(Result result) {

                        QBFileObjectAccessResult objectAccessResult = (QBFileObjectAccessResult) result;
                        if (result.isSuccess()) {
                            System.out.println(">>> FileObjectAccess" + objectAccessResult.getFileObjectAccess().toString());
                        } else {
                            handleErrors(result);
                        }
                    }
                });
            }
        }
    };

    Snippet updateFile = new Snippet("update file") {
        @Override
        public void execute() {
            if (fileID != 0) {
                QBFile qbfile = new QBFile();
                qbfile.setId(fileID);
                qbfile.setName("newName");
                QBContent.updateFile(qbfile, new QBCallbackImpl() {
                    @Override
                    public void onComplete(Result result) {
                        QBFileResult fileResult = (QBFileResult) result;
                        if (result.isSuccess()) {
                            System.out.println(">>> File:" + fileResult.getFile().toString());
                        } else {
                            handleErrors(result);
                        }
                    }
                });
            }
        }
    };

    Snippet createFile = new Snippet("create file") {
        @Override
        public void execute() {
            int fileId = R.raw.sample_file;
            InputStream is = context.getResources().openRawResource(fileId);
            file = FileHelper.getFileInputStream(is, "sample_file.txt", "qb_snippets");
            qbfile = new QBFile();
            boolean publicAccess = true;
            String contentType = ContentType.getContentType(file);
            qbfile.setName(file.getName());
            qbfile.setPublic(publicAccess);
            qbfile.setContentType(contentType);
            QBContent.createFile(qbfile, new QBCallbackImpl() {
                @Override
                public void onComplete(Result result) {

                    QBFileResult fileResult = (QBFileResult) result;
                    if (result.isSuccess()) {
                        System.out.println(">>> file created successfully");
                        params = ((QBFileResult) result).getFile().getFileObjectAccess().getParams();
                        System.out.println(">>> File" + fileResult.getFile().toString());
                    } else {
                        file = null;
                        handleErrors(result);
                    }
                }
            });

        }
    };

    Snippet uploadFile = new Snippet("upload file") {
        @Override
        public void execute() {
            if (file != null) {
                QBContent.uploadFile(file, params, new QBCallbackImpl() {
                    @Override
                    public void onComplete(Result result) {

                        if (result.isSuccess()) {
                            QBFileUploadResult uploadResult = (QBFileUploadResult) result;
                            String downloadUrl = uploadResult.getAmazonPostResponse().getLocation();
                            qbfile.setDownloadUrl(downloadUrl);
                            fileID = qbfile.getId();
                            fileSize = (int) file.length();
                            System.out.println(">>> AmazonPostResponse" + uploadResult.getAmazonPostResponse());
                        } else {
                            handleErrors(result);
                        }
                    }

                });
            }
        }
    };

    Snippet declareFileUpload = new Snippet("declare file upload") {
        @Override
        public void execute() {
            if (fileID != 0) {
                QBContent.declareFileUploaded(fileID, fileSize, new QBCallbackImpl() {
                    @Override
                    public void onComplete(Result result) {

                        Result declareFileUploadedResult = result;
                        if (result.isSuccess()) {
                            System.out.println(">>> declare file uploaded was successful" + result.toString());
                        } else {
                            handleErrors(result);
                        }
                    }
                });
            }
        }
    };

    Snippet getFileWithId = new Snippet("get file with id") {
        @Override
        public void execute() {
            if (fileID != 0) {
                QBContent.getFile(fileID, new QBCallbackImpl() {
                    @Override
                    public void onComplete(Result result) {

                        QBFileResult fileResult = (QBFileResult) result;
                        if (result.isSuccess()) {
                            System.out.println(">>> file size -" + fileResult.getFile().getSize());
                        } else {
                            handleErrors(result);
                        }
                    }

                });
            }
        }
    };

    Snippet uploadFileTask = new Snippet("upload file task") {
        @Override
        public void execute() {

            int fileId = R.raw.sample_file;
            InputStream is = context.getResources().openRawResource(fileId);

            // You should add permission to your AndroidManifest.xml file
            // <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
            // to allow read data from InputStream to File
            File file = FileHelper.getFileInputStream(is, "sample_file.txt", "qb_snippets");

            Boolean fileIsPublic = true;

            QBContent.uploadFileTask(file, fileIsPublic, new QBCallbackImpl() {
                @Override
                public void onComplete(Result result) {

                    if (result.isSuccess()) {
                        QBFileUploadTaskResult fileUploadTaskResultResult = (QBFileUploadTaskResult) result;
                        QBFile qbFile = fileUploadTaskResultResult.getFile();
                        String downloadUrl = qbFile.getDownloadUrl();

                        System.out.println(">>> file has been successfully uploaded, " +
                                "there is link to download below:");
                        System.out.println(">>> " + downloadUrl);
                        fileID = qbFile.getId();
                        uid = qbFile.getUid();
                        System.out.println(">>> QBFile:" + fileUploadTaskResultResult.getFile().toString());
                    } else {
                        handleErrors(result);
                    }
                }
            });
        }
    };

    Snippet downloadFile = new Snippet("download file") {
        @Override
        public void execute() {
            if (uid == null) {
                System.out.println("Upload file to storage before downloading.");
            } else {
                QBContent.downloadFile(uid, new QBCallbackImpl() {
                    @Override
                    public void onComplete(Result result) {

                        if (result.isSuccess()) {
                            QBFileDownloadResult fileDownloadResult = (QBFileDownloadResult) result;
                            byte[] content = fileDownloadResult.getContent();       // that's downloaded file content
                            InputStream is = fileDownloadResult.getContentStream(); // that's downloaded file content
                            System.out.println(">>> File content:" + fileDownloadResult.getContent().toString());
                        } else {
                            handleErrors(result);
                        }
                    }

                });
            }
        }
    };

    Snippet getFiles = new Snippet("get files with pagination") {
        @Override
        public void execute() {
            QBPagedRequestBuilder requestBuilder = new QBPagedRequestBuilder(20, 1);
            QBContent.getFiles(requestBuilder, new QBCallbackImpl() {
                @Override
                public void onComplete(Result result) {

                    QBFilePagedResult qbFilePagedResult = (QBFilePagedResult) result;
                    if (result.isSuccess()) {
                        System.out.println(">>> File list:" + qbFilePagedResult.getFiles().toString());
                    } else {
                        handleErrors(result);
                    }
                }

            });
        }
    };

    Snippet getTaggedList = new Snippet("get tagged list") {
        @Override
        public void execute() {
            QBPagedRequestBuilder requestBuilder = new QBPagedRequestBuilder(20, 1);
            QBContent.getTaggedList(requestBuilder, new QBCallbackImpl() {
                @Override
                public void onComplete(Result result) {

                    QBFilePagedResult qbFilePagedResult = (QBFilePagedResult) result;
                    if (result.isSuccess()) {
                        System.out.println(">>> File list:" + qbFilePagedResult.getFiles().toString());
                    } else {
                        handleErrors(result);
                    }
                }

            });
        }
    };


    Snippet downloadFileTask = new Snippet("download file Task") {
        @Override
        public void execute() {
            if (fileID != 0) {
                QBContent.downloadFileTask(fileID, new QBCallbackImpl() {
                    @Override
                    public void onComplete(Result result) {

                        QBFileDownloadResult qbFileDownloadResult = (QBFileDownloadResult) result;
                        if (result.isSuccess()) {
                            System.out.println(">>> file downloaded successful" + qbFileDownloadResult.getContent().toString());
                        } else {
                            handleErrors(result);
                        }
                    }

                });
            }
        }
    };
}