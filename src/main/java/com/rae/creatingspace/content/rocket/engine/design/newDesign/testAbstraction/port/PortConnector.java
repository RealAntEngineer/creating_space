package com.rae.creatingspace.content.rocket.engine.design.newDesign.testAbstraction.port;

import com.rae.formicapi.simulation.nodal.core.SimulationModel;
import com.rae.formicapi.simulation.nodal.thermal.Conduction;

public class PortConnector {

    public static void connectThermal(
            SimulationModel model,
            ThermalPort a,
            ThermalPort b,
            double conductance
    ) {
        model.addComponent(new Conduction(
                a.getNode(),
                b.getNode(),
                conductance
        ));
    }

    public static void connectMechanical(
            SimulationModel model,
            MechanicalPort a,
            MechanicalPort b,
            double stiffness // or conductance equivalent
    ) {
        model.addComponent(new RotationalDamping(
                a.getNode(),
                b.getNode(),
                stiffness
        ));
    }
}
