package io.totokaka.strikk.internal.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that there should be a method in the generated Strikk class to get this class.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
public @interface Fetchable {

    /**
     * The return value from the method in the Strikk class.
     *
     * The class that is annotated must be assignable from this class.
     * This class will also be used as the name for the generated method.
     *
     * @return The class that should be the return value from the generated method
     */
    Class<?> value();

}
