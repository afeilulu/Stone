package com.afeilulu.stone.util;

public class IPEntry {
    private long id;
    private String name;
    private String host;
    private int port;
    private int status;
    private boolean fail;

    public IPEntry(String name) {
        this.name = name;
    }

    public IPEntry(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public IPEntry(String name, String host, int port) {
        this.name = name;
        this.host = host;
        this.port = port;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isFail() {
        return fail;
    }

    public void setFail() {
        fail = true;
        status += 1;
    }

    public String toPath() {
        return host + ":" + port;
    }

    public String toParameter() {
        String sta = isFail() ? "FAIL" : "OK";
        return "ServiceName=" + name + "&ServiceHost=" + host + "&ServicePort="
                + port + "&ServiceStatus=" + sta;
    }

    @Override
    public String toString() {
        return name + "[" + id + "],host=" + host + ",port=" + port
                + ",status=" + status;
    }
}
