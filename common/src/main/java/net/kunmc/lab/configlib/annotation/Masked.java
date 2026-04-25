package net.kunmc.lab.configlib.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a config field as sensitive for command-oriented display.
 * <p>
 * ConfigLib may mask the field's value in outputs such as list/get/history/diff/audit,
 * depending on the active display policy.
 * </p>
 * <p>
 * Masking is display-only. Stored config files and restorable history snapshots still keep
 * the real value.
 * </p>
 * <p>
 * Applicable to both Value fields and POJO config fields.
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Masked {
}
