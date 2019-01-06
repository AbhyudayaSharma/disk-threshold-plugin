package com.abhyudayasharma.jenkins.diskthreshold;

import hudson.Extension;
import hudson.model.Action;
import hudson.model.BuildableItemWithBuildWrappers;
import hudson.model.Queue;
import jenkins.model.Jenkins;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Extension
public class DiskThresholdQueueDecisionHandler extends Queue.QueueDecisionHandler {
    private static Logger LOGGER = Logger.getLogger(DiskThresholdQueueDecisionHandler.class.getName());

    @Override
    public boolean shouldSchedule(Queue.Task p, List<Action> actions) {
        Jenkins jenkins = Jenkins.getInstance();
        long freeSpace = jenkins.getRootDir().getUsableSpace();
        BuildableItemWithBuildWrappers wrappers;
        if (p instanceof BuildableItemWithBuildWrappers) {
            wrappers = (BuildableItemWithBuildWrappers) p;
            for (Object o :
                    wrappers.getBuildWrappersList()) {
                if (o instanceof DiskThresholdBuildWrapper) {
                    DiskThresholdBuildWrapper buildWrapper = (DiskThresholdBuildWrapper) o;
                    long thresholdBytes = buildWrapper.getThresholdMegaBytes() * 1024 * 1024; // convert to bytes from MB
                    if (freeSpace < thresholdBytes) {
                        LOGGER.log(Level.SEVERE, "Not adding task to Queue due to limited disk space." +
                                "\nCurrent disk space: " + freeSpace);
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
