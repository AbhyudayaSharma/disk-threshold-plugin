package com.abhyudayasharma.jenkins.diskthreshold;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.Job;
import hudson.model.JobProperty;
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
        AbstractProject project = (AbstractProject) p;
        for (Object o : project.getAllJobs()) {
            Job job = (Job) o;
            JobProperty prop = job.getProperty(DiskThresholdJobProperty.class);
            if (prop instanceof DiskThresholdJobProperty) { // instanceof checks for null
                DiskThresholdJobProperty property = (DiskThresholdJobProperty) prop;
                long usableSpace = Jenkins.getInstance().getRootDir().getUsableSpace();
                long thresholdBytes = property.getThresholdMegaBytes() * 1024 * 1024; // convert to bytes from MBs
                if (usableSpace < thresholdBytes) {
                    LOGGER.log(Level.SEVERE, "Not adding task to Queue due to limited disk space. " +
                            "Current free disk space: " + usableSpace);
                    return false;
                }
            }
        }
        return true;
    }
}
