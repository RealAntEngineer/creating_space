package com.rae.creatingspace.content.rocket.engine.design.newDesign.testAbstraction.port;

public abstract class Port {

    protected final String name;

    protected Port(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
