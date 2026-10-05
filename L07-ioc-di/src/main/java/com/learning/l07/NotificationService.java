package com.learning.l07;

/**
 * 通知服务
 */
public class NotificationService {
    private final String channel;

    public NotificationService(String channel) {
        this.channel = channel;
    }

    public void send(String to, String msg) {
        System.out.println("[" + channel + "] -> " + to + ": " + msg);
    }

    public String getChannel() {
        return channel;
    }
}
