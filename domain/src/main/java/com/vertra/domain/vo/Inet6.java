package com.vertra.domain.vo;

import org.jetbrains.annotations.NotNull;

import java.net.InetAddress;

public record Inet6(InetAddress address) {
    public Inet6 {
        if (address == null) {
            throw new IllegalArgumentException("Address must be a valid IPv6 address");
        }
    }

    public static Inet6 parse(String ip) {
        try {
            return new Inet6(InetAddress.getByName(ip));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid IP address: " + ip, e);
        }
    }

    @Override
    public @NotNull String toString() {
        return address.getHostAddress();
    }
}
