package com.cyuan.bimibimi.model;


import java.util.List;

public interface ITask {

    void repeatAdd(String s);

    void updateIngTask(List<DownloadTaskInfo> taskInfo);

    void updateDoneTask(List<DownloadTaskInfo> taskInfo);
}
