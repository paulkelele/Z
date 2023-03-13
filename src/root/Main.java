package root;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

public class Main {

    public static void main(String[] args) throws Exception {

        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        ObjectName name = null;
        try {
            name = new ObjectName("root:type=MOnAgentJmx");
            JMXAgent jmxAgent = new JMXAgent();
            mbs.registerMBean(jmxAgent,name);
            while (true){
                Thread.sleep(1000);
                System.out.println("Printing something.........");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }


}
