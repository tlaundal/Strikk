package io.totokaka.strikk.processor;

import io.totokaka.strikk.annotations.Dependency;
import io.totokaka.strikk.annotations.StrikkPlugin;

import javax.lang.model.element.TypeElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StrikkPluginHolder {
    
    private final StrikkPlugin plugin;
    private final TypeElement element;

    public StrikkPluginHolder(StrikkPlugin plugin, TypeElement element) {
        this.plugin = plugin;
        this.element = element;
    }

    /**
     * Dumps this plugin into the target map.
     *
     * @param target The target map
     */
    public void dump(Map<String, Object> target) {
        target.put("name", plugin.name());
        target.put("version", plugin.version());
        target.put("main", element.getQualifiedName().toString());

        if (plugin.description().length() > 0) {
            target.put("description", plugin.description());
        }

        target.put("load", plugin.load().getValue());

        if (plugin.author().length() > 0) {
            target.put("author", plugin.author());
        }

        if (plugin.authors().length > 0) {
            target.put("authors", plugin.authors());
        }

        if (plugin.website().length() > 0) {
            target.put("website", plugin.website());
        }

        target.put("database", plugin.database());

        if (plugin.prefix().length() > 0) {
            target.put("prefix", plugin.prefix());
        }

        List<String> depends = new ArrayList<>();
        List<String> softDepends = new ArrayList<>();
        List<String> loadBefore = new ArrayList<>();
        for (Dependency dependency : plugin.depend()) {
            switch (dependency.type()) {
                case HARD:
                    depends.add(dependency.name());
                    break;
                case SOFT:
                    softDepends.add(dependency.name());
                    break;
                case LOAD_BEFORE:
                    loadBefore.add(dependency.name());
                    break;
            }
        }

        if (depends.size() > 0) {
            target.put("depend", depends);
        }
        if (softDepends.size() > 0) {
            target.put("softdepend", softDepends);
        }
        if (loadBefore.size() > 0) {
            target.put("loadbefore", loadBefore);
        }
    }
}
