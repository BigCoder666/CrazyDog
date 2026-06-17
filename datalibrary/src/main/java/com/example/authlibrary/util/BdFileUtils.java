package com.example.authlibrary.util;

import android.content.Context;
import android.util.Log;

import com.example.authlibrary.CodeDetail;
import com.example.authlibrary.data.SituationData;
import com.example.datalibrary.R;
import com.example.datalibrary.utils.LogUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class BdFileUtils {
    private static final String TAG = "BdAuthFileUtils";


    public static SituationData readAuthFile(Context context , String filePath) {
        LogUtils.d(TAG,  "readAuthFile  filePath = " + filePath);
        File iniFile = new File(filePath + File.separator + "license.ini");
        File keyFile = new File(filePath + File.separator + "license.key");
        SituationData situationData = new SituationData();
        // 读取ini和key文件
        if (keyFile.exists() && iniFile.exists() && keyFile.isFile() && iniFile.isFile()) {
            return readAuthFile(context , keyFile , iniFile);
            // 读取zip文件
        } else {
            File zipFile = new File(filePath + File.separator + "License.zip");
            if (zipFile.exists() && zipFile.isFile()) {
                SituationData zipData = unZip(context , zipFile.getAbsolutePath(), filePath);
                if (zipData.getCode() == CodeDetail.SUCCESS) {
                    return readAuthFile(context , filePath);
                }
                situationData.setMassage(zipData.getMassage());
                situationData.setCode(zipData.getCode());
                return zipData;
            }
            LogUtils.d(TAG,  "zip is null" );
            situationData.setMassage(context.getResources().getString(R.string.error_file_not_found) + filePath + context.getResources().getString(R.string.directory));
            situationData.setCode(CodeDetail.FILE_NOT_ERROR);
            return situationData;
        }
    }
    public static SituationData readAuthFile(Context context , File keyFile , File iniFile) {
        LogUtils.d(TAG,  "readAuthFile keyFile = " + keyFile + " iniFile = " + iniFile );
        SituationData situationData = new SituationData();
        SituationData iniData = readIniFile(context , iniFile);
        SituationData keyData = readkeyFile(context , keyFile);
        // ini文件校验
        if (iniData.getCode() != CodeDetail.SUCCESS) {
            LogUtils.d(TAG,  "iniData.getCode error : " + iniData.getCode());
            situationData.setMassage(iniData.getMassage());
            situationData.setCode(iniData.getCode());
            return situationData;
        }
        // key文件校验
        if (keyData.getCode() != CodeDetail.SUCCESS) {
            LogUtils.d(TAG,  "keyData.getCode error : " + keyData.getCode());
            situationData.setMassage(keyData.getMassage());
            situationData.setCode(keyData.getCode());
            return situationData;
        }
        situationData.setCode(CodeDetail.SUCCESS);
        situationData.setKey(keyData.getKey());
        situationData.setValue(iniData.getValue());
//            saveAuthFile(keyData.getKey() , iniData.getValue() , context.getCacheDir().getAbsolutePath());
        return situationData;
    }

    public static void saveAuthFile(Context context , String key, String[] value, String path) {
        LogUtils.d(TAG,  "saveAuthFile key = " + key + " value = " + Arrays.toString(value) + " path = " + path);
        File iniFile = new File(path + File.separator + "license.ini");
        File keyFile = new File(path + File.separator + "license.key");
        if (iniFile.exists()){
            LogUtils.d(TAG,  "iniFile.delete : " + iniFile.delete() );
        }
        if (keyFile.exists()){
            LogUtils.d(TAG,  "keyFile.delete : " + keyFile.delete() );
        }
        StringBuilder iniValue = new StringBuilder();
        if (value != null){
            for (int i = 0 , k = value.length ; i < k ; i++){
                if (i == k - 1 ){
                    iniValue.append(value[i]);
                }else {
                    iniValue.append(value[i]).append("\n");
                }
            }
        }
        saveAuthFile(context , key , keyFile.getAbsolutePath());
        saveAuthFile(context , iniValue.toString(), iniFile.getAbsolutePath());
    }

    public static void saveAuthFile(Context context , String value, String path) {
        LogUtils.d(TAG,  "saveAuthFile value = " + value + " path = " + path );
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path); // 输出流创建文件时必须保证父路径存在
            byte[] buf = value.getBytes();
            fos.write(buf, 0, buf.length);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            LogUtils.e(TAG , path + context.getResources().getString(R.string.log_file_save_failed));
        }
    }

    /**
     * 解压文件
     *
     * @throws
     */
    public static SituationData unZip(Context context , String zipFilePath, String targetDir) {
        LogUtils.d(TAG,  "unZip zipFilePath = " + zipFilePath + " targetDir = " + targetDir );
        SituationData zipSituationData = new SituationData();
        File destDir = new File(targetDir);
        if (!destDir.exists()) {
            LogUtils.d(TAG,  "destDir.mkdirs = " + destDir.mkdirs());
        }
        File file = new File(zipFilePath);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
            String targetBasePath = destDir.getAbsolutePath();
            zipSituationData = extractZip(context , zis, targetBasePath);
        } catch (FileNotFoundException e) {
            LogUtils.e(TAG,  "error FileNotFoundException " + e.getMessage());
            zipSituationData.setCode(CodeDetail.FILE_ZIP_CREATE_ERROR);
            zipSituationData.setMassage(context.getResources().getString(R.string.error_file_input_stream_create_failed) + e.getMessage());
        }
        return zipSituationData;
    }

    private static SituationData extractZip(Context context , ZipInputStream zis, String targetPath) {
        LogUtils.d(TAG,  "extractZip targetPath = " + targetPath);
        ZipEntry entry = null;
        SituationData zipSituationData = new SituationData();
        try {
            entry = zis.getNextEntry();
            zipSituationData.setCode(CodeDetail.SUCCESS);
            while (entry != null) {
                File file = new File(targetPath + File.separator + entry.getName());
                LogUtils.d(TAG,  "file = " + file.getAbsolutePath());
                if (!entry.isDirectory()) {
                    File parentFile = file.getParentFile();
                    if (parentFile != null && !parentFile.exists()) {
                        parentFile.mkdirs();
                    }
                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(file); // 输出流创建文件时必须保证父路径存在
                        int len = 0;
                        byte[] buf = new byte[1024];
                        while ((len = zis.read(buf)) != -1) {
                            fos.write(buf, 0, len);
                        }
                        fos.flush();
                    } catch (Exception e) {
                        zipSituationData.setCode(CodeDetail.FILE_ZIP_READ_ERROR);
                        zipSituationData.setMassage(context.getResources().getString(R.string.error_zip_file_read_failed) + e.getMessage());
                    } finally {
                        if (fos != null) {
                            fos.close();
                        }
                    }
                    zis.closeEntry();
                    entry = zis.getNextEntry();
                }
            }
        } catch (IOException e) {
            LogUtils.e(TAG,  "error IOException : " + e.getMessage());
            zipSituationData.setCode(CodeDetail.FILE_ZIP_READ_ERROR);
            zipSituationData.setMassage(context.getResources().getString(R.string.error_zip_file_read_failed) + e.getMessage());
        }
        return zipSituationData;
    }


    public static SituationData readkeyFile(Context context , File file) {
        LogUtils.e(TAG,  "readkeyFile file = " + file.getAbsolutePath());
        SituationData situationData = new SituationData();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = "";
            StringBuilder value = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                // 处理每行数据
                value.append(line);
            }
            situationData.setKey(String.valueOf(value));
            situationData.setCode(CodeDetail.SUCCESS);
        } catch (IOException e) {
            LogUtils.e(TAG,  "error IOException : " + e.getMessage());
            situationData.setCode(CodeDetail.FILE_READ_ERROR);
            situationData.setMassage(context.getResources().getString(R.string.error_file_read_failed));
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    LogUtils.e(TAG,  "error IOException : " + e.getMessage());
                    situationData.setCode(CodeDetail.FILE_CLEAN_ERROR);
                    situationData.setMassage(context.getResources().getString(R.string.error_file_cache_clean_failed));
                }
            }
        }
        return situationData;
    }
    public static SituationData readIniFile(Context context , File file) {
        LogUtils.e(TAG,  "error file : " + file.getAbsolutePath());
        SituationData situationData = new SituationData();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = "";
            int i = 0;
            String[] value = new String[2];
            while ((line = reader.readLine()) != null) {
                // 处理每行数据
                if (i < 2){
                    value[i] = line;
                    i += 1;
                }
            }
            situationData.setValue(value);
            situationData.setCode(CodeDetail.SUCCESS);
        } catch (IOException e) {
            LogUtils.e(TAG,  "error IOException : " + e.getMessage());
            situationData.setCode(CodeDetail.FILE_READ_ERROR);
            situationData.setMassage(context.getResources().getString(R.string.error_file_read_failed));
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    LogUtils.e(TAG,  "error IOException : " + e.getMessage());
                    situationData.setCode(CodeDetail.FILE_CLEAN_ERROR);
                    situationData.setMassage(context.getResources().getString(R.string.error_file_cache_clean_failed));
                }
            }
        }
        return situationData;
    }
}
