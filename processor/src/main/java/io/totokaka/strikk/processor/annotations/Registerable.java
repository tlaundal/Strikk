package io.totokaka.strikk.processor.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that this class's register method should be called in the register method of the Strikk class.
 *
 * May only be applied to classes that implement Registrant
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface Registerable {
}
