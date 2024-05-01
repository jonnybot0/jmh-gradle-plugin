/*
 * Copyright 2014-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.champeau.jmh;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Classpath;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;
import org.gradle.jvm.toolchain.JavaLauncher;
import org.gradle.process.ExecOperations;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * The JMH task is responsible for launching a JMH benchmark.
 */
public abstract class JMHTask extends DefaultTask implements JmhParameters {
    private final static String JAVA_IO_TMPDIR = "java.io.tmpdir";

    @Inject
    public abstract ExecOperations getExecOperations();

    @Inject
    public abstract ObjectFactory getObjects();

    @Classpath
    public abstract ConfigurableFileCollection getJmhClasspath();

    @Classpath
    public abstract ConfigurableFileCollection getTestRuntimeClasspath();

    @InputFile
    public abstract RegularFileProperty getJarArchive();


    @OutputFile
    @Optional
    public abstract RegularFileProperty getHumanOutputFile();

    @OutputFile
    public abstract RegularFileProperty getResultsFile();

    @TaskAction
    public void callJmh() {
        List<String> jmhArgs = new ArrayList<>();
        ParameterConverter.collectParameters(this, jmhArgs);
        getLogger().info("Running JMH with arguments: " + jmhArgs);
        getExecOperations().javaexec(spec -> {
            spec.setClasspath(computeClasspath());
            spec.getMainClass().set("org.openjdk.jmh.Main");
            spec.args(jmhArgs);
            spec.systemProperty(JAVA_IO_TMPDIR, getTemporaryDir().getAbsolutePath());
            spec.environment(getEnvironment().get());
            Provider<JavaLauncher> javaLauncher = getJavaLauncher();
            if (javaLauncher.isPresent()) {
                spec.executable(javaLauncher.get().getExecutablePath().getAsFile());
            }
        });
    }

    private FileCollection computeClasspath() {
        ConfigurableFileCollection classpath = getObjects().fileCollection();
        classpath.from(getJmhClasspath());
        classpath.from(getJarArchive());
        classpath.from(getTestRuntimeClasspath());
        return classpath;
    }

    @Option(option = "jmhVersion", description = "JMH version")
    void setJmhVersion(String jmhVersion) {
        this.getJmhVersion().set(jmhVersion);
    }

    @Option(option = "includeTests", description = "Include test sources in generated jar file")
    void setIncludeTests(Property<Boolean> includeTests) {
        this.getIncludeTests().set(includeTests);
    }

    @Option(option = "includes", description = "Include pattern for benchmarks to be executed")
    void setIncludes(List<String> includes){
        this.getIncludes().set(includes);
    }

    @Option(option = "excludes", description = "Exclude pattern for benchmarks to be executed")
    void setExcludes(List<String> excludes){
        this.getExcludes().set(excludes);
    }

    @Option(option = "benchmarkMode", description = "Benchmark mode. Available modes are: [Throughput/thrpt, AverageTime/avgt, SampleTime/sample, SingleShotTime/ss, All/all]")
    void setBenchmarkMode(List<String> benchmarkMode) {
        this.getBenchmarkMode().set(benchmarkMode);
    }

    @Option(option = "iterations", description = "Number of measurement iterations to do.")
    void setIterations(Integer iterations) {
        this.getIterations().set(iterations);
    }

    @Option(option = "batchSize", description = "Batch size: number of benchmark method calls per operation. (some benchmark modes can ignore this setting)")
    void setBatchSize(Integer batchSize){
        this.getBatchSize().set(batchSize);
    }

    @Option(option = "fork", description = "How many times to forks a single benchmark. Use 0 to disable forking altogether")
    void setFork(Integer fork) {
        this.getFork().set(fork);
    }

    @Option(option = "failOnError", description = "Should JMH fail immediately if any benchmark had experienced the unrecoverable error?")
    void getFailOnError(Boolean failOnerror) {
        this.getFailOnError().set(failOnerror);
    }

    @Option(option = "forceGC", description = "Should JMH force GC between iterations?")
    void getForceGC(Boolean forceGC) {
        this.getForceGC().set(forceGC);
    }
/*

    @Option(option = "jvm")
    Property<String> getJvm();

    @Option(option = "jvmArgs")
    ListProperty<String> getJvmArgs();

    @Option(option = "jvmArgsAppend")
    ListProperty<String> getJvmArgsAppend();

    @Option(option = "jvmArgsPrepend")
    ListProperty<String> getJvmArgsPrepend();

    @Option(option = "operationsPerInvocation")
    Property<Integer> getOperationsPerInvocation();

    @Option(option = "property<String>> getBenchmarkParameters")
    MapProperty<String, ListProperty<String>> getBenchmarkParameters();

    @Option(option = "profilers")
    ListProperty<String> getProfilers();

    @Option(option = "timeOnIteration")
    Property<String> getTimeOnIteration();

    @Option(option = "resultExtension")
    Property<String> getResultExtension();

    @Option(option = "resultFormat")
    Property<String> getResultFormat();

    @Option(option = "synchronizeIterations")
    Property<Boolean> getSynchronizeIterations();

    @Option(option = "threads")
    Property<Integer> getThreads();

    @Option(option = "threadGroups")
    ListProperty<Integer> getThreadGroups();

    @Option(option = "timeUnit")
    Property<String> getTimeUnit();

    @Option(option = "verbosity")
    Property<String> getVerbosity();

    @Option(option = "jmhTimeout")
    Property<String> getJmhTimeout();

    @Option(option = "warmup")
    Property<String> getWarmup();

    @Option(option = "warmupBatchSize")
    Property<Integer> getWarmupBatchSize();

    @Option(option = "warmupForks")
    Property<Integer> getWarmupForks();

    @Option(option = "warmupIterations")
    Property<Integer> getWarmupIterations();

    @Option(option = "warmupMode")
    Property<String> getWarmupMode();

    @Option(option = "warmupBenchmarks")
    ListProperty<String> getWarmupBenchmarks();

    @Option(option = "zip64")
    Property<Boolean> getZip64();

    @Option(option = "duplicateClassesStrategy")
    Property<DuplicatesStrategy> getDuplicateClassesStrategy();
*/
}
