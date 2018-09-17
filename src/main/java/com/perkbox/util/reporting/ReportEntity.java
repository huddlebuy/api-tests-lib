package com.perkbox.util.reporting;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ReportEntity {

    public String packageName;
    public String endpoint;
    public String method;
    public String testData;
    public String description;
    public String status;

    public ReportEntity(String packageName, String endpoint, String method, Object[] testData, String description, String status) {
        this.packageName = packageName;
        this.endpoint = endpoint;
        this.method = method;
        this.testData = testData == null ? null : Arrays.stream(testData).map(Object::toString)
                .collect(Collectors.joining(","));
        this.description = description;
        this.status = status;
    }

    public String getPackageName() {
        return packageName;
    }
}