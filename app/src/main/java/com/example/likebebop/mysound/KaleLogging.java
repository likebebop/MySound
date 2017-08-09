package com.example.likebebop.mysound;



/**
 * Created by likebebop on 2016-11-02.
 */

public class KaleLogging {
    public static final KaleLog R_LOG = new KaleLog("renderProfile");
    public static final KaleLog P_LOG = R_LOG;

    public static final KaleLog RD_LOG = new KaleLog("renderDetail");
    public static final KaleLog K_LOG = new KaleLog("kale");
    public static final KaleLog CUR_LOG = new KaleLog("kale_debug");

    public static final HandyProfiler PROFILER = new HandyProfiler(CUR_LOG);
    public static final HandyProfiler R_PROFILER = new HandyProfiler(R_LOG);
    public static final HandyProfiler RD_PROFILER = new HandyProfiler(RD_LOG);
}
