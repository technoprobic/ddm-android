package com.technoprobic.ddm.ddm.model;

public class InfuraIpfsResponse {

    // response object to infura ipfs HTTP API

    private String Name;
    private String Hash;
    private String Size;

    public InfuraIpfsResponse() {

    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getHash() {
        return Hash;
    }

    public void setHash(String hash) {
        Hash = hash;
    }

    public String getSize() {
        return Size;
    }

    public void setSize(String size) {
        Size = size;
    }
}
