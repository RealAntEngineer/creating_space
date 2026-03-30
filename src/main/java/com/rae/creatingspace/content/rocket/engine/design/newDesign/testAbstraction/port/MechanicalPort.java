package com.rae.creatingspace.content.rocket.engine.design.newDesign.testAbstraction.port;

import com.rae.formicapi.simulation.nodal.core.Node;

public class MechanicalPort extends Port {

    private final Node torqueNode;
    private final Node speedNode;

    public MechanicalPort(String name, Node torqueNode, Node speedNode) {
        super(name);
        this.torqueNode = torqueNode;
        this.speedNode = speedNode;
    }
}
