package com.rae.creatingspace.content.rocket.engine.design.newDesign.testAbstraction.port;

import com.rae.formicapi.simulation.nodal.core.Node;

public class FluidPort extends Port {

    private final Node pressureNode;
    private final Node temperatureNode;

    public FluidPort(String name, Node pressureNode, Node temperatureNode) {
        super(name);
        this.pressureNode = pressureNode;
        this.temperatureNode = temperatureNode;
    }

    public Node getPressureNode() {
        return pressureNode;
    }

    public Node getTemperatureNode() {
        return temperatureNode;
    }
}
