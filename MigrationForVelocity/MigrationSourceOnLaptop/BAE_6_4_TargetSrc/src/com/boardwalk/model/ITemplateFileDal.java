package com.boardwalk.model;

import java.io.IOException;
import java.io.InputStream;

public interface ITemplateFileDal {
    static String FolderTemplates = "templates";
    static String ExtTemplates = ".xlsb";
    void setTemplateFileByName(InputStream src, String fileName) throws IOException;
}
