package com.vertra.domain.vo;

import java.net.Inet6Address;

public record Inet6(Inet6Address address) {
    public Inet6 {
        if (address == null) {
            throw new IllegalArgumentException("Address must be a valid IPv6 address");
        }
    }

    public static Inet6 parse(String ip) {
        try {
            Inet6Address address = (Inet6Address) Inet6Address.getByName(ip);
            return new Inet6(address);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid IPv6 address: " + ip, e);
        }
    }
}
