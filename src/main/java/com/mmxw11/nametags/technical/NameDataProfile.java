package com.mmxw11.nametags.technical;

public class NameDataProfile {

    private final String realName;
    private String name;
    private String prefix;
    private String suffix;
    private long lastSeenTime;

    public NameDataProfile(String realName, String name) {
        this.realName = realName;
        this.name = name;
        resetLastSeenTime();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public void resetLastSeenTime() {
        this.lastSeenTime = System.currentTimeMillis();
    }

    public String getRealName() {
        return realName;
    }

    public String getName() {
        return name;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public long getLastSeenTime() {
        return lastSeenTime;
    }
}