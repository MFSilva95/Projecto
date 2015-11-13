/*
 * Copyright 2006 Abdulla G. Abdurakhmanov (abdulla.abdurakhmanov@gmail.com).
 *
 * Licensed under the LGPL, Version 2 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/copyleft/lgpl.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * With any your questions welcome to my e-mail
 * or blog at http://abdulla-a.blogspot.com.
 */
package com.jadg.mydiabetes.middleHealth.org.bn.annotations;

import com.jadg.mydiabetes.middleHealth.org.bn.coders.TagClass;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target( {ElementType.FIELD })
public @interface ASN1Element {
    String name() default "";
    boolean isOptional() default true;
    boolean hasTag() default false;
    boolean isImplicitTag() default true;
    int tagClass() default TagClass.ContextSpecific;
    int tag() default 0;
    boolean hasDefaultValue() default false;

    /*
     * Used to get fields in declaration order. Not all java virtual machines
     * returns fields in order in getDeclaredFields method.
     * Next annotation is desirable to be automatically set by BNCompiler
     */
    boolean hasExplicitOrder() default false;
    int declarationOrder() default -1;
}
