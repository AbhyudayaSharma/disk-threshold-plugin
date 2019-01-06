package com.abhyudayasharma.jenkins.diskthreshold;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildWrapper;
import hudson.tasks.BuildWrapperDescriptor;
import hudson.util.FormValidation;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

import javax.annotation.Nonnull;

@SuppressWarnings("unused")
public final class DiskThresholdBuildWrapper extends BuildWrapper {

    @Extension
    public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();
    private final static transient long MINIMUM_THRESHOLD_MEGABYTES = 1;
    private long thresholdMegaBytes;

    @DataBoundConstructor
    public DiskThresholdBuildWrapper(long thresholdMegaBytes) {
        this.thresholdMegaBytes = thresholdMegaBytes;
    }

    /**
     * Get the threshold bytes
     *
     * @return threshold bytes
     */
    @SuppressWarnings("WeakerAccess")
    public long getThresholdMegaBytes() {
        return thresholdMegaBytes;
    }

    /**
     * Set thresholdMegaBytes
     *
     * @param thresholdMegaBytes the threshold to disk full in bytes
     */
    @DataBoundSetter
    public void setThresholdMegaBytes(long thresholdMegaBytes) {
        this.thresholdMegaBytes = thresholdMegaBytes;
    }

    @Override
    public Environment setUp(AbstractBuild build, Launcher launcher, BuildListener listener) {
        return new Environment() {
        };
    }

    public static final class DescriptorImpl extends BuildWrapperDescriptor {
        @Override
        public boolean isApplicable(AbstractProject<?, ?> item) {
            return true; // should be applicable on all projects
        }

        /**
         * {@inheritDoc}
         */
        @Nonnull
        @Override
        public String getDisplayName() {
            return Messages.DisplayName();
        }

        /**
         * Check and validate the value of thresholdMegaBytes entered by the user
         *
         * @param value the entered value
         * @return OK if validated, Error otherwise
         */
        public FormValidation doCheckThresholdMegaBytes(@QueryParameter String value) {
            try {
                Long l = Long.valueOf(value);
                if (l > MINIMUM_THRESHOLD_MEGABYTES) return FormValidation.ok();
            } catch (NumberFormatException ignore) {
            }
            return FormValidation.error("Please enter a positive integer value greater than " +
                    MINIMUM_THRESHOLD_MEGABYTES + ".");
        }
    }
}
