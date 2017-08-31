package io.totokaka.strikk.annotations;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * A dependency for a plugin.
 *
 * <p>
 *     Different {@link DependencyType}s are treated differently, see their documentation.
 * </p>
 */
@Retention(RetentionPolicy.CLASS)
public @interface Dependency {

    /**
     * The type of dependency this is.
     *
     * This says something about the relation between the current plugin and the dependency.
     *
     * @return What type this dependency is
     */
    DependencyType type();

    /**
     * The name of this dependency.
     *
     * @return The name of the dependency plugin
     */
    String name();

}
