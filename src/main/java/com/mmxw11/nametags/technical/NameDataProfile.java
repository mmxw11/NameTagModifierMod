package com.mmxw11.nametags.technical;

public class NameDataProfile {

    private final String realName;
    private long lastSeenTime;
    private String name;
    private String prefix;
    private String suffix;

    public NameDataProfile(String realName, String name) {
        this.realName = realName;
        this.name = name;
        resetLastSeenTime();
    }

    public void resetLastSeenTime() {
        this.lastSeenTime = System.currentTimeMillis();
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

    public String getRealName() {
        return realName;
    }

    public long getLastSeenTime() {
        return lastSeenTime;
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
}