package io.totokaka.strikk.processor.pluginyml;

import io.totokaka.strikk.annotations.Dependency;
import io.totokaka.strikk.annotations.LoadTime;
import io.totokaka.strikk.annotations.StrikkPlugin;

import javax.lang.model.element.TypeElement;

public class StrikkPluginInterpreter {

    private final TypeElement element;
    private final StrikkPlugin annotation;

    public StrikkPluginInterpreter(TypeElement element, StrikkPlugin annotation) {
        this.element = element;
        this.annotation = annotation;
    }

    public String getName() {
        return annotation.name();
    }

    public String getVersion() {
        return annotation.version();
    }

    public String getDescription() {
        return annotation.description();
    }

    public String getAuthor() {
        return annotation.author();
    }

    public String[] getAuthors() {
        return annotation.authors();
    }

    public String getWebsite() {
        return annotation.website();
    }

    public LoadTime getLoadTime() {
        return annotation.load();
    }

    public boolean usesDatabase() {
        return annotation.database();
    }

    public Dependency[] getDependencies() {
        return annotation.depend();
    }

    public String getPrefix() {
        return annotation.prefix();
    }

    public String getMain() {
        return element.getQualifiedName().toString();
    }

}
