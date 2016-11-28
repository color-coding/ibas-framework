package org.colorcoding.ibas.bobas.data;

import java.io.InputStream;

/**
 * 文件数据
 * 
 * @author Niuren.Zhu
 *
 */
public class FileData {

    private String fileName;

    /**
     * 文件名称
     * 
     * @return
     */
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    private String location;

    /**
     * 文件位置
     * 
     * @return
     */
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    private InputStream stream;

    /**
     * 文件流
     * 
     * @return
     */
    public InputStream getStream() {
        return stream;
    }

    public void setStream(InputStream stream) {
        this.stream = stream;
    }

    @Override
    public String toString() {
        return String.format("{file data: %s}", this.getFileName());
    }
}
