package com.kim.ldap.utils;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.UnixStyleUsageFormatter;

public class Config {
    public static String codeBase;

    @Parameter(names = {"-i", "--ip"}, description = "Local ip address ", order = 1)
    public static String ip="0.0.0.0";

    @Parameter(names = {"-l", "--ldapPort"}, description = "Ldap bind port", order = 2)
    public static int ldapPort = 1389;

    @Parameter(names = {"-p", "--httpPort"}, description = "Http bind port", order = 3)
    public static int httpPort = 8080;

    @Parameter(names = {"-u", "--usage"}, description = "Show usage", order = 4)
    public static boolean showUsage;

    @Parameter(names = {"-h", "--help"}, help = true, description = "Show this help")
    private static boolean help = false;

    public static void applyCmdArgs(String[] args) {
        //process cmd args
        JCommander jc = JCommander.newBuilder()
                .addObject(new Config())
                .build();
        try{
            jc.parse(args);
        }catch(Exception e){
            if(!showUsage){
                System.out.println("Error: " + e.getMessage() + "\n");
                help = true;
            }
        }
        jc.setProgramName("java -jar JNDICheck.jar");
        jc.setUsageFormatter(new UnixStyleUsageFormatter(jc));

        if(help) {
            jc.usage(); //if -h specified, show help and exit
            System.exit(0);
        }

        // 特别注意：最后一个反斜杠不能少啊
        Config.codeBase = "http://" + Config.ip + ":" + Config.httpPort + "/";
    }
}
