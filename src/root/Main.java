package root;

import com.sun.jdmk.comm.AuthInfo;
import com.sun.jdmk.comm.HtmlAdaptorServer;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import java.lang.management.ManagementFactory;
import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

// "C:\Program Files\Java\jdk-17.0.2\bin\java.exe" -cp .;..\..\..\lib\jmxtools.jar root.Main
public class Main {
    static final int PORT = 8005;
    public static void main(String[] args) throws Exception {

        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        ObjectName name = null;
        ObjectName adapterName = null;
        AuthInfo[] authInfoList = new AuthInfo[1];
        try {

            name = new ObjectName("root:type=MonAgentJmx");
            JMXAgent jmxAgent = new JMXAgent();
            mbs.registerMBean(jmxAgent,name);
             authInfoList[0] = new AuthInfo("stef","cerise"); // AuthInfo(login,mdp)
            // adaptateur html
            HtmlAdaptorServer htmlAdaptorServer = new HtmlAdaptorServer(PORT,authInfoList);
            adapterName = new ObjectName("root:name=htmlAdaptor,port="+PORT);

            mbs.registerMBean(htmlAdaptorServer,adapterName);
            htmlAdaptorServer.start();
            System.out.println("Lancement de l'adaptateur de protocole HTML sur le port "+ PORT);


//            JMXServiceURL url = new JMXServiceURL(
//                    "service:jmx:rmi:///jndi/rmi://localhost:1599/servers");
//            JMXConnectorServer cs = JMXConnectorServerFactory.newJMXConnectorServer(
//                    url, null, mbs);
//            cs.start();
//            System.out.println("Lancement connecteur RMI "+url);
            int i = 0;
            while ( i<100000000  ){
                Thread.sleep(1000);
                System.out.println("Printing something.........");
                i++;
            }

//            cs.stop();
//
//            System.out.println("Arret de l'agent JMX");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }


}
