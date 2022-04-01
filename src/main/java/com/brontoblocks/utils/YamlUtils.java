package com.brontoblocks.utils;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;
import java.nio.file.Path;

public final class YamlUtils {

    public static <T> T parseYamlFileFromResources(Path filename, Class<T> clazz) {

        Yaml yaml = new Yaml(new Constructor(clazz));
        InputStream inputStream = YamlUtils.class.getClassLoader()
            .getResourceAsStream(filename.toString());

        return yaml.load(inputStream);
    }

}
