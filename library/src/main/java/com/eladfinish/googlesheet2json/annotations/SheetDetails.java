package com.eladfinish.googlesheet2json.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//@Target(ElementType.FIELD)
//@Target(ElementType.METHOD)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface SheetDetails {

    String[] fields() default {};

    String[] labels() default {};

    String entryName() default "Custom";

    //will generate class <entryName>_SheetEntry where <entryName> is capitalized
    //e.g. Custom_SheetEntry

}

