package com.kim.ldap;

import com.kim.ldap.controllers.LdapController;
import com.kim.ldap.controllers.LdapMapping;
import com.kim.ldap.utils.Config;
import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig;
import com.unboundid.ldap.listener.InMemoryListenerConfig;
import com.unboundid.ldap.listener.interceptor.InMemoryInterceptedSearchResult;
import com.unboundid.ldap.listener.interceptor.InMemoryOperationInterceptor;
import org.reflections.Reflections;

import javax.net.ServerSocketFactory;
import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;
import java.lang.reflect.Constructor;
import java.net.InetAddress;
import java.util.*;


public class LdapServer extends InMemoryOperationInterceptor {
    public static List<String> mmList = new ArrayList<String>();
    TreeMap<String, LdapController> routes = new TreeMap<String, LdapController>();

    public static void start() {
        try {
            InMemoryDirectoryServerConfig serverConfig = new InMemoryDirectoryServerConfig("dc=example,dc=com");
            serverConfig.setListenerConfigs(new InMemoryListenerConfig(
                    "listen",
                    InetAddress.getByName("0.0.0.0"),
                    Config.ldapPort,
                    ServerSocketFactory.getDefault(),
                    SocketFactory.getDefault(),
                    (SSLSocketFactory) SSLSocketFactory.getDefault()));

            serverConfig.addInMemoryOperationInterceptor(new LdapServer());
            InMemoryDirectoryServer ds = new InMemoryDirectoryServer(serverConfig);
            ds.startListening();
            System.out.println("[+] LDAP Server Start Listening on " + Config.ldapPort + "...");
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    public LdapServer() throws Exception {

        Set<Class<?>> controllers = new Reflections(this.getClass().getPackage().getName())
                .getTypesAnnotatedWith(LdapMapping.class);

        for(Class<?> controller : controllers) {
            Constructor<?> cons = controller.getConstructor();
            LdapController instance = (LdapController) cons.newInstance();
            String[] mappings = controller.getAnnotation(LdapMapping.class).uri();
            for(String mapping : mappings) {
                if(mapping.startsWith("/"))
                    mapping = mapping.substring(1); //remove first forward slash
                routes.put(mapping, instance);
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see com.unboundid.ldap.listener.interceptor.InMemoryOperationInterceptor#processSearchResult(com.unboundid.ldap.listener.interceptor.InMemoryInterceptedSearchResult)
     */
    @Override
    public void processSearchResult(InMemoryInterceptedSearchResult result) {
        String base = result.getRequest().getBaseDN();
        System.out.println("[+] Received LDAP Query: " + base);
        mmList.add(base);

    }
}