/**   
 * Copyright © 2015 浙江大华. All rights reserved.
 * 
 * @title: JSONType.java
 * @description: TODO
 * @author: 23536   
 * @date: 2015年12月23日 下午3:26:00 
 */
package com.android.library.net.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/** 
 * @description: JSONType定义
 * @author: 23536
 * @date: 2015年12月23日 下午3:26:00  
 */
public class JSONType<T> {
    final Type type;

    /**
     * Constructs a new type literal. Derives represented class from type
     * parameter.
     * <p/>
     * <p/>
     * Clients create an empty anonymous subclass. Doing so embeds the type
     * parameter in the anonymous class's type hierarchy so we can reconstitute
     * it at runtime despite erasure.
     */
    protected JSONType() {
        this.type = getSuperclassTypeParameter(getClass());
    }

    /**
     * Returns the type from super class's type parameter
     */
    Type getSuperclassTypeParameter(Class<?> subclass) {
        return ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    /**
     * Gets underlying {@code Type} instance.
     */
    public final Type getType() {
        return type;
    }

}
