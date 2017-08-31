package io.totokaka.strikk.annotations;

/**
 * Types of {@link Dependency}
 */
public enum DependencyType {

    /**
     * A hard dependency means the plugin will not load unless the dependency is present.
     * The dependency will always load before the current plugin.
     */
    HARD,

    /**
     * A soft dependency means the plugin will be loaded after the dependency if it is present.
     * The plugin will still start if the plugin is not present.
     */
    SOFT,

    /**
     * A load before dependency means the current plugin will be loaded before the dependency, if it is present.
     */
    LOAD_BEFORE;

}
