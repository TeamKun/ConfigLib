package net.kunmc.lab.configlib.annotation;

import java.lang.annotation.*;

/**
 * Program elements annotated @Internal are intended for ConfigLib internal use only.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
public @interface Internal {
}
