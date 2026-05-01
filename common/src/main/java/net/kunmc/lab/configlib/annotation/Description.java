package net.kunmc.lab.configlib.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Supplies descriptive metadata for a config field.
 * <p>
 * ConfigLib uses this text for command hover/help output and, for YAML-based stores,
 * emits it as a comment near the saved field.
 * </p>
 * <p>
 * Applicable to POJO config fields. For records, put this annotation on the
 * record component.
 * </p>
 * <p>
 * Value fields expose descriptions via {@link net.kunmc.lab.configlib.Value#description(String)}
 * instead of this annotation.
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Description {
    String value();
}
