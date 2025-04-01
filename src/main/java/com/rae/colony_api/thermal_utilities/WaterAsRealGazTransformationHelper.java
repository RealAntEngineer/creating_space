package com.rae.colony_api.thermal_utilities;


public class WaterAsRealGazTransformationHelper {

    //terrible approximation just to get started

    static Float CLiquid = 4187f;
    static Float Cp = 2100f;
    static Float gamma = 1.3125f;

    static Float TPSat = (float) ((647 - 273) / (22.064 * 1000000 - 611));
    static Float T0 = (float) (372);
    //static float PCrit = 22.064f * 1000000f;
    static float dh0 = 2500000f;
    static Float dhSat = (float) (-2500000f / (22.064 * 1000000 - 611));
    public static final SpecificRealGazState DEFAULT_STATE = new SpecificRealGazState(300f, 101300f, get_h(0,300,101300),0f);


    private static float dhVap() {
        return  dhSat * 101300f + dh0;
    }

    private static float TSat(Float pressure) {
        return TPSat * pressure + T0;// take the 0 in account
    }

    public static SpecificRealGazState isobaricTransfert(SpecificRealGazState fluidState, float specific_heat) {
        if (specific_heat == 0) {
            return fluidState;
        }
        else {
            float newH = fluidState.specificEnthalpy() + specific_heat;
            float newPressure = fluidState.pressure();
            float newT = get_T(newPressure,newH);
            float newVaporQuality = get_x(newH,newT,newPressure);
            return new SpecificRealGazState(newT, newPressure, newH, newVaporQuality);
        }
    }

    public static float get_h(float x, float T, float P) {
        if (x == 0) {
            return (T - 273) * CLiquid;
        }
        else if(x < 1) {
            return (TSat(P) - 273) * CLiquid + dhVap() * x;
        }
        else{
            return (TSat(P) - 273) * CLiquid + dhVap() * x + (T - TSat(P)) * Cp;
        }
    }
    public static float get_T(float P, float h){
        if (h < (TSat(P) - 273) * CLiquid){
            return h/CLiquid +273;
        }
        else if (h < (TSat(P) - 273) * CLiquid + dhVap()){
            return TSat(P);
        }
        else {
            return TSat(P) + (h - (TSat(P) - 273) * CLiquid - dhVap())/Cp;
        }
    }
    public static float get_x(float h, float T, float P) {
        if (T < TSat(P)) {
            return 0;
        }
        else if(T < TSat(P)+0.0001) {
            return Math.min(1,(h - (TSat(P) - 273) * CLiquid)/dhVap());
        }
        else{
            return 1;
        }
    }

    /**
     * adiabatic reversible expansion
     * @param fluidState
     * @param expansionFactor the initial pressure over the pressure of the fluid at the end of the turbine
     * @return the new fluid state
     */
    public static SpecificRealGazState standardExpansion(SpecificRealGazState fluidState, float expansionFactor){//vapor quality has an issue
        float Pf = fluidState.pressure() / expansionFactor;
        float dP = Pf - fluidState.pressure();
        float dT = 0;
        float dx = 0;
        if (fluidState.vaporQuality() == 1) {
            // il faut verifier que
            float Tf = (float) (Math.pow((fluidState.pressure() / Pf), ((1 - gamma) / gamma)) * fluidState.temperature());
            dT = Tf - fluidState.temperature();
            // ça marche pas
            if (Tf < TSat(Pf)) {
                dT = TSat(Pf) - fluidState.temperature();
                if (dhVap() > 0) {
                    dx = (Tf - TSat(Pf)) * Cp / dhVap();
                    if (dx < -1) {
                        dx = -1;
                    }
                }
            } else {
                dx = 1 - fluidState.vaporQuality();
            }
        }
        else if (fluidState.vaporQuality() > 0) {
            // gaz part :
            float Tf_g = (float) (Math.pow(fluidState.pressure() / Pf,(1 - gamma) / gamma) * fluidState.temperature());
            float dT_g = Tf_g - fluidState.temperature();
            float dh_g = (dT_g * Cp + dhVap()) * fluidState.vaporQuality();
            return isobaricTransfert(new SpecificRealGazState(fluidState.temperature(),
                    Pf, get_h(0, fluidState.temperature(), Pf), 0f), dh_g);
        }
        else {
            if (fluidState.temperature() > TSat(Pf)) {
                float Pf_l = (fluidState.temperature() - T0) / TPSat;
                dx = ((fluidState.temperature() - TSat(Pf)) * CLiquid) / dhVap();
                dT = TSat(Pf) - fluidState.temperature();
                // need to be redone
                if (dx > 1) {
                    //to correct (low priority but might cause bug at high pressures)
                    float Pf_v = ((TSat(Pf_l) - T0) * CLiquid - dh0) / (TPSat * CLiquid + dhSat);
                    return standardExpansion(new SpecificRealGazState(TSat(Pf_v), Pf_v, get_h(1, TSat(Pf_v), Pf_v), 1f), Pf_v / Pf);

                }
            }
        }
        return new SpecificRealGazState(
                fluidState.temperature() + dT,
                fluidState.pressure() + dP,
                get_h(fluidState.vaporQuality() + dx, fluidState.temperature() + dT, fluidState.pressure() + dP),
                fluidState.vaporQuality() + dx);
    }
    /**
     * adiabatic expansion
     * @param fluidState :
     * @param isentropicYield : how much the fluid lost enthalpy over what it should have if reversible (how much energy was taken from it)
     * @param expansionCoef : the initial pressure over the pressure of the fluid at the end of the turbine
     * @return the new fluid state
     */
    public static SpecificRealGazState standardExpansion(SpecificRealGazState fluidState, float isentropicYield, float expansionCoef){
        SpecificRealGazState revFluidState = standardExpansion(fluidState,expansionCoef);
        float reversibleDh = revFluidState.specificEnthalpy()- fluidState.specificEnthalpy();
        float losth = reversibleDh*(1-isentropicYield);
        return isobaricTransfert(revFluidState,-losth);
    }
    /**
     * adiabatic reversible compression
     * @param fluidState :
     * @param compressionFactor :the pressure of the fluid at the end of the turbine over the initial pressure
     * @return the new fluid state
     */
    public static SpecificRealGazState standardCompression(SpecificRealGazState fluidState, float compressionFactor){
        float Pf = fluidState.pressure() * compressionFactor;
        float dP = Pf - fluidState.pressure();
        float dT = 0;
        float dx = 0;
        if (fluidState.vaporQuality() == 1) {
            //il faut verifier que
            float Tf = (float) (Math.pow((fluidState.pressure() / Pf),((1 - gamma) / gamma)) * fluidState.temperature());
            dT = Tf - fluidState.temperature();
            //ça marche pas
            if (Tf < TSat(Pf)) {
                dT = TSat(Pf) - fluidState.temperature();
                if (dhVap() > 0) {
                    dx = (Tf - TSat(Pf)) * Cp / dhVap();  //-np.log(dhVap(Pf) / dhVap(Pi)) * (TPSat * Cp / dhSat)
                    if (dx < -1) {
                        dx = -1;
                    }
                } else {
                    dx = -1;
                }
            }
        }
        else if (fluidState.vaporQuality() > 0) {
            // gaz part :
            float Tf_g = (float) (Math.pow(fluidState.pressure() / Pf,(1 - gamma) / gamma) * fluidState.temperature());
            float dT_g = Tf_g - fluidState.temperature();
            float dh_g = (dT_g * Cp + dhVap()) * fluidState.vaporQuality();

            return isobaricTransfert(new SpecificRealGazState(fluidState.temperature(), Pf, get_h(0, fluidState.temperature(), Pf), 0f), dh_g);
        }
        return new SpecificRealGazState(
                fluidState.temperature() + dT,
                fluidState.pressure() + dP,
                get_h(fluidState.vaporQuality() + dx, fluidState.temperature() + dT, fluidState.pressure() + dP),
                fluidState.vaporQuality() + dx);
    }
    /**
     * adiabatic compression
     * @param fluidState :
     * @param yield :how much the fluid gained enthalpy over what it should have if reversible (how much energy was put into it)
     * @param compressionCoef :the pressure of the fluid at the end of the turbine over the initial pressure
     * @return the new fluid state
     */
    public static SpecificRealGazState standardCompression(SpecificRealGazState fluidState, float yield, float compressionCoef){
        SpecificRealGazState revFluidState = standardCompression(fluidState,compressionCoef);
        float reversibleDh = revFluidState.specificEnthalpy()- fluidState.specificEnthalpy();
        float losth = reversibleDh*(1-yield);
        return isobaricTransfert(revFluidState,-losth);
    }

//TODO -> it seems to not be working when amount are too low -> protection against 0 values ?
    public static SpecificRealGazState mix(SpecificRealGazState first, float firstAmount, SpecificRealGazState second, float secondAmount){
        float P = first.pressure()*firstAmount/(firstAmount+ secondAmount) + second.pressure()*secondAmount/(firstAmount+ secondAmount);
        float h = first.specificEnthalpy()*firstAmount/(firstAmount+ secondAmount) + second.specificEnthalpy()*secondAmount/(firstAmount+ secondAmount);
        //float x = first.vaporQuality()*firstAmount/(firstAmount+ secondAmount) + second.vaporQuality()*secondAmount/(firstAmount+ secondAmount);
        SpecificRealGazState state = new SpecificRealGazState(
                get_T(P,h), P, h, get_x(h,get_T(P,h),P));
        //System.out.println(state);
        if (first.temperature().isNaN() || second.temperature().isNaN()){
            return DEFAULT_STATE;
        }
        if (first.temperature() > 20000 || second.temperature() > 20000){
            return DEFAULT_STATE;
        }
        return state;
    }
}
