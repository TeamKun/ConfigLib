package net.kunmc.lab.configlib.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Allows a POJO config field to hold {@code null}. For records, put this
 * annotation on the record component.
 * <p>
 * Without this annotation, POJO fields are treated as non-null during validation.
 * </p>
 * <p>
 * This annotation is for POJO fields. Value fields define their own nullability through
 * their type and validators.
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigNullable {
}
