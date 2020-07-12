package com.commander.model;

import java.nio.file.Path;

/**
 * {@code JobSeed} is made from a User bean and contains all the DNA to build
 * and execute a Job
 *
 * @author HGDIV
 */
public class JobSeed {

    private Path inputSource;
    private Path outputSource;
    private String identifier;

    public JobSeed(Path input, Path output, String identifier) {
        this.inputSource = input;
        this.outputSource = output;
        this.identifier = identifier;
    }


    public Path getInputSource() {
        return inputSource;
    }

    public void setInputSource(Path inputSource) {
        this.inputSource = inputSource;
    }

    public Path getOutputSource() {
        return outputSource;
    }

    public void setOutputSource(Path outputSource) {
        this.outputSource = outputSource;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public String toString() {
        return "JobSeed{" +
                "inputSource=" + inputSource +
                ", outputSource=" + outputSource +
                ", identifier='" + identifier + '\'' +
                '}';
    }


}
