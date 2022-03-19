package com.gobrs.async;

/**
 * @program: gobrs-async-starter
 * @ClassName EventHandlerGroup
 * @description:
 * @author: sizegang
 * @create: 2022-03-16
 **/

import com.gobrs.async.task.AsyncTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TaskCluster {

    private final TaskBus taskBus;

    /**
     * cache taskList
     */
    private List<AsyncTask> cacheTaskList;

    TaskCluster(TaskBus taskBus, List<AsyncTask> taskList) {
        synchronized (taskBus) {
            if (taskBus.isReady()) {
                throw new IllegalStateException("taskBus is ready, cannot be edit any more.");
            }
            this.taskBus = taskBus;
            this.cacheTaskList = new ArrayList<>(taskList.size());
            /**
             *  src -> dest
             */
            copyList(taskList, this.cacheTaskList);
            for (AsyncTask task : taskList) {
                taskBus.addDependency(task, null);
            }
        }
    }

    public TaskCluster then(AsyncTask... eventHandlers) {
        synchronized (taskBus) {
            if (taskBus.isReady()) {
                throw new IllegalStateException(
                        "taskBus is ready, cannot be edit any more.");
            }
            for (AsyncTask from : this.cacheTaskList) {
                for (AsyncTask to : eventHandlers) {
                    taskBus.addDependency(from, to);
                }
            }
            for (AsyncTask to : eventHandlers) {
                taskBus.addDependency(to, null);
            }
            this.cacheTaskList = new ArrayList<AsyncTask>(
                    eventHandlers.length);
            copyList(Arrays.asList(eventHandlers), this.cacheTaskList);
            return this;
        }
    }

    private void copyList(List<AsyncTask> src,
                          List<AsyncTask> dest) {
        for (AsyncTask s : src) {
            dest.add(s);
        }
    }

}
