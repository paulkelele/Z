package root;

import javax.management.*;
import java.beans.*;
import java.beans.IntrospectionException;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * ----------- Interface DynamicBean
 *
 * MBeanInfo getMBeanInfo() * Renvoyer un objet de type MbeanInfo qui encapsule les fonctionnalités exposées par le MBean
 *
 * Object getAttribute(String attribute) * Permettre d'obtenir la valeur d'un attribut à partir de son nom
 *
 * void setAttribute(Attribute attribute)   * Permettre de mettre à jour la valeur d'un attribut
 *
 * AttributeList getAttributes(String[] attributes)  * Permettre d'obtenir la valeur d'un ensemble d'attributs à partir de leurs noms
 *
 * AttributeList setAttributes(AttributeList attributes)  * Permettre de mettre à jour la valeur d'un ensemble d'attributs
 *
 * Object invoke(String actionName, Object params[], String signature[]) * Permettre d'invoquer une opération
 */

public class JMXAgent implements DynamicMBean {
    User user;
    BeanInfo beanInfo;
    Constructor[] cs;
    java.lang.reflect.Field[] fields;
    String nameBean;
    ArrayList<String> attributs;
    ArrayList<Method> getters;
    ArrayList<Method> setters;
    HashMap<String, AnnotatedType> d = new HashMap<>();

    private HashMap<String, Method> methods = new HashMap<String, Method>();

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
            d.put(fields[i].getName(),fields[i].getAnnotatedType());

        }
        //setters.get(0).invoke(user,12);
        System.out.println(attributs );
        System.out.println(d);
        for(Method method: user.getClass().getMethods()) {
            methods.put(method.getName(), method);
        }
        System.out.println(methods);
    }


    @Override
    public Object getAttribute(String attribute) throws AttributeNotFoundException, MBeanException, ReflectionException {
        Object o = null;
        for (String a: attributs) {
            if (attribute.equals(a)){
                PropertyDescriptor pd;
                try {
                    pd = new PropertyDescriptor(a, user.getClass());
                    Method getter = pd.getReadMethod();
                    o = getter.invoke(user);
                } catch (IntrospectionException | InvocationTargetException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return o;
    }

    @Override
    public void setAttribute(Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
        for(String a: attributs){
            if(attribute.getName().equals(a)){
                PropertyDescriptor pd = null;
                try {
                    pd = new PropertyDescriptor(a,user.getClass());
                    Method setter = pd.getWriteMethod();
                    setter.invoke(user, attribute.getValue());
                } catch (IntrospectionException | InvocationTargetException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public AttributeList getAttributes(String[] attributes) {
        AttributeList attributs = new AttributeList();
        for (String attribute : attributes) {
            try {
                Object value = getAttribute((String) attribute);
                attributs.add(new Attribute(attribute, value));
            } catch (AttributeNotFoundException | MBeanException | ReflectionException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println(attributs);
        return attributs;
    }

    @Override
    public AttributeList setAttributes(AttributeList attributes) {
        // Check attributesto avoid NullPointerException later on
        if (attributes == null) {
            throw new RuntimeOperationsException(
                    new IllegalArgumentException(
                            "AttributeList attributes cannot be null"),
                    "Cannot invoke a setter of " );
        }
        AttributeList resultList = new AttributeList();

        // if attributeNames is empty, nothing more to do
        if (attributes.isEmpty())
            return resultList;

        // try to set each attribute and add to result list if successful
        for (Iterator i = attributes.iterator(); i.hasNext();) {
            Attribute attr = (Attribute) i.next();
            try {
                setAttribute(attr);
                String name = attr.getName();
                Object value = getAttribute(name);
                resultList.add(new Attribute(name,value));
            } catch(Exception e) {
                // print debug info but keep processing list
                e.printStackTrace();
            }
        }
        return(resultList);
    }

    @Override
    public Object invoke(String actionName, Object[] params, String[] signature) throws MBeanException, ReflectionException {
        if (methods.containsKey(actionName))
            try {
                return methods.get(actionName).invoke(user, params);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                System.err.println("Erreur lors de l'invocation : " + e.getMessage());
            }
        return null;
    }

    /**
     *  Class MBeanInfo------------
     *
     * MBeanAttributeInfo[] getAttributes() * Renvoyer un tableau de type MBeanAttributeInfo qui contient les métadonnées des attributs
     *
     * MBeanConstructorInfo[] getConstructors()  * Renvoyer un tableau de type MBeanConstructorInfo qui contient les métadonnées des constructeurs
     *
     * String getDescription()  * Renvoyer une description du MBean
     *
     * MBeanNotificationInfo[] getNotifications() * Renvoyer un tableau de type MBeanNotificationInfo qui contient les métadonnées des notifications
     *
     * MBeanOperationInfo[] getOperations() * Renvoyer un tableau de type MBeanOperationInfo qui contient les métadonnées des opérations
     */


    @Override
    public MBeanInfo getMBeanInfo() {

        // Pour les attributs
        MBeanAttributeInfo [] attribs = new MBeanAttributeInfo[this.attributs.size()];
        for (int i = 0; i < attribs.length; i++) {
            attribs[i] = new MBeanAttributeInfo(this.attributs.get(i),this.d.get(this.attributs.get(i)).toString() ,"attrib_"+this.attributs.get(i),true,true,false);
        }

        //Pour les constructeurs
        MBeanConstructorInfo[] constructeurs = new MBeanConstructorInfo[cs.length];
        for (int i = 0; i < cs.length; i++){
            constructeurs[i] = new MBeanConstructorInfo(cs[i].getName(),cs[i].getName(),null);
        }

        // pour les operations
        MBeanOperationInfo[] operations = new MBeanOperationInfo[3];

        MBeanParameterInfo[] sansParamInfo = new MBeanParameterInfo[1];
        sansParamInfo[0]= new MBeanParameterInfo("nom","java.lang.String", "Aucune");
        operations[0] = new MBeanOperationInfo("setNom",
                "setNom", sansParamInfo, user.getClass().getName(),
                MBeanOperationInfo.ACTION);

        // pour les notifications
        //...........null


        return new MBeanInfo(nameBean, "Ma description", attribs,constructeurs,null,null);
    }
}
