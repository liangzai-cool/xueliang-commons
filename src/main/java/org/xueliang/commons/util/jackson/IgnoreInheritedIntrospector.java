package org.xueliang.commons.util.jackson;

import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;

public class IgnoreInheritedIntrospector extends JacksonAnnotationIntrospector {
    
    private static final long serialVersionUID = 1L;

    @Override
    public boolean hasIgnoreMarker(AnnotatedMember m) {
        return m.getDeclaringClass().getPackage().getName().startsWith("org.jooq") || super.hasIgnoreMarker(m);
    }
}
