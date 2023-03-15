package root;

import com.sun.jdmk.comm.AuthInfo;
import com.sun.jdmk.comm.HtmlAdaptorServer;
import root.utils.Entreprise;
import root.utils.Organization;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

// "C:\Program Files\Java\jdk-17.0.2\bin\java.exe" -cp .;..\..\..\lib\jmxtools.jar root.Main
public class Main {
    static final int PORT = 8005;
    public static void main(String[] args) throws Exception {
        int a =3;
        int b = 7;
        int r = ++a + ++b;
        System.out.println(a+" "+b+ " "+r);

        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        ObjectName adapterName = null;
        AuthInfo[] authInfoList = new AuthInfo[1];

        try {
            User user = new User();
            user = new User();
            user.setNom("Dupont");
            user.setPrenom("Jean");
            user.setAge(25);

            Company c = new Company();
            c.setAdresse("rue bidule");
            c.setNom("Ma companie");

            Entreprise e = new Entreprise();
            e.setAge(41);
            e.setCorporate("Corpo");

            Organization o = new Organization();
            o.setAge(52);
            o.setNom("Mon Organisation");

            Object[] tab = {user, c, e, o};
            ObjectName[] on = new ObjectName[tab.length];
            for (int i = 0; i < tab.length; i++) {
                on[i] = new ObjectName("Application:type="+tab[i].getClass().getPackageName()+",name="+tab[i].getClass().getSimpleName());
            }
            for (int i = 0; i < tab.length; i++) {
                MBeanAgent mba = new MBeanAgent(tab[i]);
                mbs.registerMBean(mba,on[i]);
            }
             authInfoList[0] = new AuthInfo("stef","cerise"); // AuthInfo(login,mdp)
            // adaptateur html
            HtmlAdaptorServer htmlAdaptorServer = new HtmlAdaptorServer(PORT,authInfoList);
            adapterName = new ObjectName("root:name=htmlAdaptor,port=MonServerWEB");

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
//            while ( i<100000000  ){
//                Thread.sleep(1000);
//                System.out.println("Printing something.........");
//                i++;
//            }

//            cs.stop();
//
//            System.out.println("Arret de l'agent JMX");
        } catch ( Exception e) {
            throw new RuntimeException(e);
        }

    }


}
