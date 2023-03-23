package root;

import javax.management.*;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * ----------- Interface DynamicBean
 * <p>
 * MBeanInfo getMBeanInfo() * Renvoyer un objet de type MbeanInfo qui encapsule les fonctionnalités exposées par le MBean
 * <p>
 * Object getAttribute(String attribute) * Permettre d'obtenir la valeur d'un attribut à partir de son nom
 * <p>
 * void setAttribute(Attribute attribute)   * Permettre de mettre à jour la valeur d'un attribut
 * <p>
 * AttributeList getAttributes(String[] attributes)  * Permettre d'obtenir la valeur d'un ensemble d'attributs à partir de leurs noms
 * <p>
 * AttributeList setAttributes(AttributeList attributes)  * Permettre de mettre à jour la valeur d'un ensemble d'attributs
 * <p>
 * Object invoke(String actionName, Object params[], String signature[]) * Permettre d'invoquer une opération
 */

public class MBeanAgent implements DynamicMBean {
    private final HashMap<String, Method> methods = new HashMap<String, Method>();
    Object registerObject;
    BeanInfo beanInfo;
    Constructor[] cs;
    java.lang.reflect.Field[] fields;
    String nameBean;
    ArrayList<String> attributs;
    ArrayList<Method> getters;
    ArrayList<Method> setters;
    HashMap<String, AnnotatedType> d = new HashMap<>();
    ArrayList<String> methodsName = new ArrayList<String>();
    ArrayList<Method> met = new ArrayList<Method>();

    public MBeanAgent(Object o) throws IntrospectionException, InvocationTargetException, IllegalAccessException {
        registerObject = o;
        beanInfo = Introspector.getBeanInfo(o.getClass());
        fields = o.getClass().getDeclaredFields();
        cs = o.getClass().getConstructors();
        nameBean = o.getClass().getName();
        attributs = new ArrayList<String>();
        getters = new ArrayList<Method>();
        setters = new ArrayList<Method>();
        init();

    }

    private void init() throws InvocationTargetException, IllegalAccessException {
        java.beans.PropertyDescriptor[] pd = beanInfo.getPropertyDescriptors();

        for (PropertyDescriptor pdi : pd) {
            Method m = pdi.getReadMethod();
            Method p = pdi.getWriteMethod();
            if (m.getName().equals("getClass")) continue;
            attributs.add(pdi.getName());
            getters.add(m);
            setters.add(p);
        }

        for (int i = 0; i < fields.length; i++) {
            d.put(fields[i].getName(), fields[i].getAnnotatedType());

        }
        //setters.get(0).invoke(user,12);
        for (Method method : registerObject.getClass().getMethods()) {
            String name = method.getName();
            if (name.equals("getClass") || name.equals("wait") || name.equals("notifyAll") || name.equals("notify")
                    || name.equals("hashCode") || name.equals("equals") || name.equals("toString")) continue;
            methodsName.add(method.getName());
            methods.put(method.getName(), method);
            met.add(method);
        }
    }


    @Override
    public Object getAttribute(String attribute) throws AttributeNotFoundException, MBeanException, ReflectionException {
        Object o = null;
        for (String a : attributs) {
            if (attribute.equals(a)) {
                PropertyDescriptor pd;
                try {
                    pd = new PropertyDescriptor(a, registerObject.getClass());
                    Method getter = pd.getReadMethod();
                    o = getter.invoke(registerObject);
                } catch (IntrospectionException | InvocationTargetException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return o;
    }

    @Override
    public AttributeList getAttributes(String[] attributes) {
        AttributeList attributs = new AttributeList();
        for (String attribute : attributes) {
            try {
                Object value = getAttribute(attribute);
                attributs.add(new Attribute(attribute, value));
            } catch (AttributeNotFoundException | MBeanException | ReflectionException e) {
                throw new RuntimeException(e);
            }
        }
        return attributs;
    }


    @Override
    public void setAttribute(Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
        for (String a : attributs) {
            if (attribute.getName().equals(a)) {
                PropertyDescriptor pd = null;
                try {
                    pd = new PropertyDescriptor(a, registerObject.getClass());
                    Method setter = pd.getWriteMethod();
                    setter.invoke(registerObject, attribute.getValue());
                } catch (IntrospectionException | InvocationTargetException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public AttributeList setAttributes(AttributeList attributes) {
        if (attributes == null) {
            throw new RuntimeOperationsException(
                    new IllegalArgumentException(
                            "AttributeList attributes cannot be null"),
                    "Cannot invoke a setter of " + nameBean);
        }
        AttributeList resultList = new AttributeList();
        if (attributes.isEmpty())
            return resultList;

        // try to set each attribute and add to result list if successful
        for (Object attribute : attributes) {
            Attribute attr = (Attribute) attribute;
            try {
                setAttribute(attr);
                String name = attr.getName();
                Object value = getAttribute(name);
                resultList.add(new Attribute(name, value));
            } catch (Exception e) {
                // print debug info but keep processing list
                e.printStackTrace();
            }
        }
        return (resultList);
    }

    @Override
    public Object invoke(String actionName, Object[] params, String[] signature) throws MBeanException, ReflectionException {
        if (methods.containsKey(actionName))
            try {
                return methods.get(actionName).invoke(registerObject, params);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                System.err.println("Erreur lors de l'invocation : " + e.getMessage());
            }
        return null;
    }

    /**
     * Class MBeanInfo------------
     * <p>
     * MBeanAttributeInfo[] getAttributes() * Renvoyer un tableau de type MBeanAttributeInfo qui contient les métadonnées des attributs
     * <p>
     * MBeanConstructorInfo[] getConstructors()  * Renvoyer un tableau de type MBeanConstructorInfo qui contient les métadonnées des constructeurs
     * <p>
     * String getDescription()  * Renvoyer une description du MBean
     * <p>
     * MBeanNotificationInfo[] getNotifications() * Renvoyer un tableau de type MBeanNotificationInfo qui contient les métadonnées des notifications
     * <p>
     * MBeanOperationInfo[] getOperations() * Renvoyer un tableau de type MBeanOperationInfo qui contient les métadonnées des opérations
     */


    @Override
    public MBeanInfo getMBeanInfo() {

        MBeanFeatureInfo[] m = new MBeanFeatureInfo[1];
        MBeanFeatureInfo d = new MBeanFeatureInfo("Organization", "Extend Organisation");

        // Pour les attributs
        MBeanAttributeInfo[] attribs = new MBeanAttributeInfo[this.attributs.size()];
        for (int i = 0; i < attribs.length; i++) {
            attribs[i] = new MBeanAttributeInfo(this.attributs.get(i), this.d.get(this.attributs.get(i)).toString(), "attrib_" + this.attributs.get(i), true, false, false);
        }
        MBeanParameterInfo[] sansParamInfo = new MBeanParameterInfo[0];

        //Pour les constructeurs

        MBeanConstructorInfo[] constructeurs = new MBeanConstructorInfo[cs.length];
        for (int i = 0; i < cs.length; i++) {
            constructeurs[i] = new MBeanConstructorInfo(cs[i].getName(), cs[i].getName(), sansParamInfo);
        }

        // pour les operations
        MBeanOperationInfo[] operations = new MBeanOperationInfo[met.size()];
        MBeanParameterInfo[] dynParams = null;
        for (int j = 0; j < met.size(); j++) {
            dynParams = getMBeanParameterInfo(met.get(j));
            operations[j] = new MBeanOperationInfo(
                    methodsName.get(j), "description of " + methodsName.get(j), dynParams, registerObject.getClass().getName(),
                    methodsName.get(j).startsWith("get") ? MBeanOperationInfo.INFO : MBeanOperationInfo.ACTION, null);
        }
        // pour les notifications
        //...........null
        MBeanInfo mBeanInfo = new MBeanInfo(nameBean, "MBean from class " + nameBean, attribs, constructeurs, operations, null, null);

        return mBeanInfo;
    }

    /**
     * A partir d'une method, determine le nombre et le type de parametres d'une methode
     *
     * @param method
     * @return
     */
    private MBeanParameterInfo[] getMBeanParameterInfo(Method method) {
        if (method.getParameters().length == 0) {
            return null;
        }
        Type[] p = method.getParameterTypes();
        MBeanParameterInfo[] params = new MBeanParameterInfo[method.getParameters().length];
        for (int i = 0; i < params.length; i++) {
            params[i] = new MBeanParameterInfo(method.getName(), p[i].getTypeName(), "Description of " + method.getName());
        }
        return params;
    }
}
