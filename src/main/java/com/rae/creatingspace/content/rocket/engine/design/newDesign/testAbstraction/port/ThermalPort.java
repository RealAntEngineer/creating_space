package com.rae.creatingspace.content.rocket.engine.design.newDesign.testAbstraction.port;

import com.rae.formicapi.simulation.nodal.core.Node;

public class ThermalPort extends Port {

    private final Node node;

    public ThermalPort(String name, Node node) {
        super(name);
        this.node = node;
    }

    public Node getNode() {
        return node;
    }
}