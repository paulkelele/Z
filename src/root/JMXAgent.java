package root;

import javax.management.*;
import java.beans.*;
import java.beans.IntrospectionException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;


public class JMXAgent implements DynamicMBean {
    User user;
    BeanInfo beanInfo;
    Constructor[] cs;
    java.lang.reflect.Field[] fields;
    String nameBean;
    ArrayList<String> attributs;
    ArrayList<Method> getters;
    ArrayList<Method> setters;

    public JMXAgent( ) throws IntrospectionException, InvocationTargetException, IllegalAccessException {
        user =  new User();
        user.setNom("Dupont");
        user.setPrenom("Jean");
        user.setAge(25);
        beanInfo = Introspector.getBeanInfo( user.getClass());
        fields = user.getClass().getDeclaredFields();
        cs = user.getClass().getConstructors();
        nameBean  = user.getClass().getName();
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

        for (int i = 0; i < fields.length; i++) {
            System.out.println(fields[i].getAnnotatedType());
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
        AttributeList attributs = new AttributeList();
        for (int i = 0; i < this.attributs.size(); i++) {
            attributs.add(new Attribute(this.attributs.get(i), this.getters.get(i)));
        }

        return attributs;
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
        MBeanAttributeInfo [] attribs = new MBeanAttributeInfo[this.attributs.size()];
        for (int i = 0; i < attribs.length; i++) {

            attribs[i] = new MBeanAttributeInfo(this.attributs.get(i),this.attributs.get(i) ,"descr",true,true,false);
        }
        MBeanConstructorInfo[] constructeurs = new MBeanConstructorInfo[cs.length];
        for (int i = 0; i < cs.length; i++){
            constructeurs[i] = new MBeanConstructorInfo("User","une description",new MBeanParameterInfo[0]);
        }
        return new MBeanInfo(nameBean, "Ma description", attribs,constructeurs,null,null);
    }
}
