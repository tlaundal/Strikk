package io.totokaka.strikk.processor.pluginyml;

import io.totokaka.strikk.annotations.DependencyType;
import io.totokaka.strikk.annotations.LoadTime;
import io.totokaka.strikk.annotations.PermissionDefault;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.Writer;
import java.util.*;

public class PluginYmlGenerator {
    private String author;
    private String[] authors;
    private String description;
    private LoadTime loadTime;
    private String main;
    private String name;
    private String prefix;
    private String version;
    private String website;
    private boolean usesDatabase;
    private Map<DependencyType, List<String>> dependencies;
    private Map<String, Map<String, Object>> permissions;
    private Map<String, Map<String, Object>> commands;

    public PluginYmlGenerator() {
        this.dependencies = new HashMap<>();
        dependencies.put(DependencyType.HARD, new ArrayList<>());
        dependencies.put(DependencyType.LOAD_BEFORE, new ArrayList<>());
        dependencies.put(DependencyType.SOFT, new ArrayList<>());
        permissions = new HashMap<>();
        commands = new HashMap<>();
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setAuthors(String[] authors) {
        this.authors = authors;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLoadTime(LoadTime loadTime) {
        this.loadTime = loadTime;
    }

    public void setMain(String main) {
        this.main = main;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public void setUsesDatabase(boolean usesDatabase) {
        this.usesDatabase = usesDatabase;
    }

    public void addDependency(DependencyType type, String name) {
        this.dependencies.get(type).add(name);
    }

    public void addPermission(String name, String description, PermissionDefault defaultAccess, Map<String, Boolean> children) {
        Map<String, Object> values = new HashMap<>();
        values.put("default", defaultAccess.getValue());
        if (description.length() > 0) {
            values.put("description", description);
        }
        if (children.size() > 0) {
            values.put("children", children);
        }
        permissions.put(name, values);
    }

    public void addCommand(String name, String description, String[] aliases, String permission, String permissionMessage, String usage) {
        Map<String, Object> values = new HashMap<>();
        if (description.length() > 0) {
            values.put("description", description);
        }
        if (aliases.length > 0) {
            values.put("aliases", aliases);
        }
        if (permission.length() > 0) {
            values.put("permission", permission);
        }
        if (permissionMessage.length() > 0) {
            values.put("permission-message", permissionMessage);
        }
        if (usage.length() > 0) {
            values.put("usage", usage);
        }
        commands.put(name, values);
    }

    public void generate(Writer output) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("version", version);
        if (description.length() > 0) {
            map.put("description", description);
        }
        map.put("load", loadTime.getValue());
        if (author.length() > 0) {
            map.put("author", author);
        }
        if (authors.length > 0) {
            map.put("authors", authors);
        }
        if (website.length() > 0) {
            map.put("website", website);
        }
        map.put("main", main);
        map.put("database", usesDatabase);
        if (dependencies.size() > 0) {
            map.put("depend", dependencies.get(DependencyType.HARD));
            map.put("softdepend", dependencies.get(DependencyType.SOFT));
            map.put("loadbefore", dependencies.get(DependencyType.LOAD_BEFORE));
        }
        if (prefix.length() > 0) {
            map.put("prefix", prefix);
        }
        if (commands.size() > 0) {
            map.put("commands", commands);
        }
        if (permissions.size() > 0) {
            map.put("permissions", permissions);
        }

        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        dumperOptions.setIndent(4);
        Yaml yaml = new Yaml(dumperOptions);
        yaml.dump(map, output);
    }

}
