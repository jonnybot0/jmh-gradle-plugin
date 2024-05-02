package me.champeau.jmh;

import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.options.Option;

import java.util.List;

public interface JmhSetters extends JmhParameters {

    @Option(option = "iterations", description = "Number of measurement iterations to do.")
    default void setIterations(Integer iterations) {
        this.getIterations().set(iterations);
    }

    @Option(option = "includes", description = "include pattern (regular expression) for benchmarks to be executed")
    default void setIncludes(List<String> includes) {
        this.getIncludes().set(includes);
    }
}
