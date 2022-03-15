package com.boardwalk.model;

public interface IFileSystemWrapper
{
    public String getBasePath();
    public ITemplateFileDal getTemplateFileDal();

    // public String getPath(String suffix);
}
