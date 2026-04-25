package net.kunmc.lab.configlib.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares inclusive numeric bounds for a config field.
 * <p>
 * ConfigLib applies this range during file load and generated command mutation validation.
 * </p>
 * <p>
 * Intended for numeric POJO fields. Value fields should typically use their own constructor
 * bounds and/or {@code addValidator(...)}.
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Range {
    double min() default -Double.MAX_VALUE;

    double max() default Double.MAX_VALUE;
}
