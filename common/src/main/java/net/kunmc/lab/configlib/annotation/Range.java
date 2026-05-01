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
 * Intended only for numeric POJO config leaf fields. For records, put this
 * annotation on a numeric record component.
 * Do not put this annotation on section/object fields such as
 * {@code public ArenaSettings arena}; put it on a numeric field inside that object
 * instead. Value fields should typically use their own constructor bounds and/or
 * {@code addValidator(...)}.
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Range {
    double min() default -Double.MAX_VALUE;

    double max() default Double.MAX_VALUE;
}
