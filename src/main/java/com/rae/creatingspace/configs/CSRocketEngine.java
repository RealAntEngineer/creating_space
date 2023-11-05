package com.rae.creatingspace.configs;

public class CSRocketEngine extends CSConfigBase{
    public final ConfigInt bigRocketEngineTrust = i((int) (500000*9.81),0,"bigRocketEngineTrust", Comments.bigRocketEngineTrust);
    public final ConfigInt smallRocketEngineTrust = i((int) (10000*9.81),0,"smallRocketEngineTrust", Comments.smallRocketEngineTrust);
    public final ConfigInt methaloxISP = i(365,0,"methaloxISP", Comments.methaloxISP);
    public final ConfigFloat ISPModifier = f(1F,0.1F,"ISPModifier", Comments.ISPModifier);

    @Override
    public String getName() {
        return "rocketEngine";
    }
    private static class Comments {
        static String ISPModifier = " reduction coefficient on the consumption";
        static String methaloxISP = " base ISP of rocket engines";
        static String bigRocketEngineTrust ="the trust in Newtons of the big engine";
        static String smallRocketEngineTrust = "the trust in Newtons of the small engine";
    }
}
