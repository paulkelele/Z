package root;

import javax.management.*;
import java.beans.*;
import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;


public class JMXAgent implements DynamicMBean {
    User user;
    BeanInfo beanInfo;

    ArrayList<String> attributs;
    ArrayList<Method> getters;
    ArrayList<Method> setters;

    public JMXAgent( Object o) throws IntrospectionException, InvocationTargetException, IllegalAccessException {
        user =  new User();
        user.setNom("Dupont");
        user.setPrenom("Jean");
        user.setAge(25);
        beanInfo = Introspector.getBeanInfo( user.getClass());
        attributs = new ArrayList<String>();
        getters = new ArrayList<Method>();
        setters = new ArrayList<Method>();
        init();

    }

    private void init() throws InvocationTargetException, IllegalAccessException {

        java.beans.PropertyDescriptor[] pd  =  beanInfo.getPropertyDescriptors();

        for(PropertyDescriptor pdi : pd){

            Method m = pdi.getReadMethod();
            Method p = pdi.getWriteMethod();
            if(m.getName().equals("getClass")) continue;
            attributs.add(pdi.getName());
            getters.add(m);
            setters.add(p);
        }



    }

    @Override
    public Object getAttribute(String attribute) throws AttributeNotFoundException, MBeanException, ReflectionException {
        return null;
    }

    @Override
    public void setAttribute(Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {

    }

    @Override
    public AttributeList getAttributes(String[] attributes) {
        return null;
    }

    @Override
    public AttributeList setAttributes(AttributeList attributes) {
        return null;
    }

    @Override
    public Object invoke(String actionName, Object[] params, String[] signature) throws MBeanException, ReflectionException {
        return null;
    }

    @Override
    public MBeanInfo getMBeanInfo() {
        MBeanAttributeInfo [] attributs = new MBeanAttributeInfo[this.attributs.size()];
        for (int i = 0; i < attributs.length; i++) {
            attributs[i] = new MBeanAttributeInfo(this.attributs.get(i),this.attributs.get(i).getClass().getName(),"descr",true,true,false);
        }
        return null;
    }
}
