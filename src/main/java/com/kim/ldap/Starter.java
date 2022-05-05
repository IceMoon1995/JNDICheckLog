package com.kim.ldap;

import com.kim.ldap.utils.Config;
import java.io.IOException;

public class Starter {
    public static void main(String[] args) throws IOException {
        Config.applyCmdArgs(args);
        LdapServer.start();
        HTTPServer.start();
    }
}