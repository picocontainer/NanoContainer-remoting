/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Joerg Schaible                                           *
 *****************************************************************************/

package org.nanocontainer.remoting.jmx;

import javax.management.MBeanInfo;
import javax.management.ObjectName;

import org.nanocontainer.remoting.jmx.testmodel.Person;
import org.nanocontainer.remoting.jmx.testmodel.PersonMBean;
import org.picocontainer.defaults.InstanceComponentAdapter;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.Touchable;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;


/**
 * @author J&ouml;rg Schaible
 */
public class RegisteredMBeanConstructingProviderTest extends MockObjectTestCase {

    private ObjectName objectName;
    private Mock dynamicMBeanFactory;

    protected void setUp() throws Exception {
        super.setUp();
        objectName = new ObjectName(":type=JUnit");
        dynamicMBeanFactory = mock(DynamicMBeanFactory.class);
    }

    public void testRegisterWithoutComponentKey() {
        final MBeanInfo mBeanInfo = Person.createMBeanInfo();
        final Person person = new Person();

        dynamicMBeanFactory.expects(once()).method("create").with(same(person), same(Person.class), same(mBeanInfo));

        final RegisteredMBeanConstructingProvider provider = new RegisteredMBeanConstructingProvider(
                (DynamicMBeanFactory)dynamicMBeanFactory.proxy());
        provider.register(objectName, mBeanInfo);
        assertNotNull(provider.provide(null, new InstanceComponentAdapter(Person.class, person)));
    }

    public void testRegisterWithArbitraryComponentKey() {
        final MBeanInfo mBeanInfo = Person.createMBeanInfo();
        final Person person = new Person();

        dynamicMBeanFactory.expects(once()).method("create").with(same(person), same(Person.class), same(mBeanInfo));

        final RegisteredMBeanConstructingProvider provider = new RegisteredMBeanConstructingProvider(
                (DynamicMBeanFactory)dynamicMBeanFactory.proxy());
        provider.register("JUnit", objectName, mBeanInfo);
        assertNotNull(provider.provide(null, new InstanceComponentAdapter("JUnit", person)));
    }

    public void testRegisterWithArbitraryComponentKeyAndManagementInterface() {
        final MBeanInfo mBeanInfo = Person.createMBeanInfo();
        final Touchable touchable = new SimpleTouchable();

        dynamicMBeanFactory.expects(once()).method("create").with(
                same(touchable), same(Touchable.class), same(mBeanInfo));

        final RegisteredMBeanConstructingProvider provider = new RegisteredMBeanConstructingProvider(
                (DynamicMBeanFactory)dynamicMBeanFactory.proxy());
        provider.register("JUnit", objectName, Touchable.class, mBeanInfo);
        assertNotNull(provider.provide(null, new InstanceComponentAdapter("JUnit", touchable)));
    }

    public void testRegisterWithTypedComponentKeyButWithoutMBeanInfo() {
        final Person person = new Person();

        dynamicMBeanFactory.expects(once()).method("create").with(same(person), same(PersonMBean.class), NULL);

        final RegisteredMBeanConstructingProvider provider = new RegisteredMBeanConstructingProvider(
                (DynamicMBeanFactory)dynamicMBeanFactory.proxy());
        provider.register(PersonMBean.class, objectName);
        assertNotNull(provider.provide(null, new InstanceComponentAdapter(PersonMBean.class, person)));
    }

    public void testRegisterWithArbitraryComponentKeyButWithoutMBeanInfo() {
        final Person person = new Person();

        dynamicMBeanFactory.expects(once()).method("create").with(same(person), same(Person.class), NULL);

        final RegisteredMBeanConstructingProvider provider = new RegisteredMBeanConstructingProvider(
                (DynamicMBeanFactory)dynamicMBeanFactory.proxy());
        provider.register("JUnit", objectName);
        assertNotNull(provider.provide(null, new InstanceComponentAdapter("JUnit", person)));
    }

    public void testUsageOfStandardMBeanFactory() {
        final RegisteredMBeanConstructingProvider provider = new RegisteredMBeanConstructingProvider();
        provider.register(PersonMBean.class, objectName);
        final JMXRegistrationInfo info = provider.provide(null, new InstanceComponentAdapter(
                PersonMBean.class, new Person()));
        assertNotNull(info.getMBean());
        assertEquals(objectName, info.getObjectName());
    }
}
