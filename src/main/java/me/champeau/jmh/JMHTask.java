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
import org.gradle.api.file.DuplicatesStrategy;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.MapProperty;
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
public abstract class JMHTask extends DefaultTask implements WithJavaToolchain {
    private final static String JAVA_IO_TMPDIR = "java.io.tmpdir";


    private ListProperty<Integer> threadGroups;
    private ListProperty<String> benchmarkMode;
    private ListProperty<String> excludes;
    private ListProperty<String> includes;
    private ListProperty<String> jvmArgs;
    private ListProperty<String> jvmArgsAppend;
    private ListProperty<String> jvmArgsPrepend;
    private ListProperty<String> profilers;
    private ListProperty<String> warmupBenchmarks;
    private MapProperty<String, ListProperty<String>> benchmarkParameters;
    private MapProperty<String, Object> environment;
    private Property<Boolean> failOnError;
    private Property<Boolean> forceGC;
    private Property<Boolean> includeTests;
    private Property<Boolean> synchronizeIterations;
    private Property<Boolean> zip64;
    private Property<DuplicatesStrategy> duplicateClassesStrategy;
    private Property<Integer> batchSize;
    private Property<Integer> fork;
    private Property<Integer> iterations;
    private Property<Integer> operationsPerInvocation;
    private Property<Integer> threads;
    private Property<Integer> warmupBatchSize;
    private Property<Integer> warmupForks;
    private Property<Integer> warmupIterations;
    private Property<String> jmhTimeout;
    private Property<String> jmhVersion;
    private Property<String> jvm;
    private Property<String> resultExtension;
    private Property<String> resultFormat;
    private Property<String> timeOnIteration;
    private Property<String> timeUnit;
    private Property<String> verbosity;
    private Property<String> warmup;
    private Property<String> warmupMode;

    @Input
    public ListProperty<Integer> getThreadGroups() {
        return threadGroups;
    }

    public void setThreadGroups(ListProperty<Integer> threadGroups) {
        this.threadGroups = threadGroups;
    }

    @Input
    public ListProperty<String> getBenchmarkMode() {
        return benchmarkMode;
    }

    public void setBenchmarkMode(ListProperty<String> benchmarkMode) {
        this.benchmarkMode = benchmarkMode;
    }

    @Input
    public ListProperty<String> getExcludes() {
        return excludes;
    }

    @Option(option = "excludes", description = "Exclude pattern for benchmarks to be executed")
    public void setExcludes(ListProperty<String> excludes) {
        this.excludes = excludes;
    }

    @Input
    public ListProperty<String> getIncludes() {
        return includes;
    }

    @Option(option = "includes", description = "Include pattern for benchmarks to be executed")
    public void setIncludes(ListProperty<String> includes) {
        this.includes = includes;
    }

    @Input
    public ListProperty<String> getJvmArgs() {
        return jvmArgs;
    }

    public void setJvmArgs(ListProperty<String> jvmArgs) {
        this.jvmArgs = jvmArgs;
    }

    @Input
    public ListProperty<String> getJvmArgsAppend() {
        return jvmArgsAppend;
    }

    public void setJvmArgsAppend(ListProperty<String> jvmArgsAppend) {
        this.jvmArgsAppend = jvmArgsAppend;
    }

    @Input
    public ListProperty<String> getJvmArgsPrepend() {
        return jvmArgsPrepend;
    }

    public void setJvmArgsPrepend(ListProperty<String> jvmArgsPrepend) {
        this.jvmArgsPrepend = jvmArgsPrepend;
    }

    @Input
    public ListProperty<String> getProfilers() {
        return profilers;
    }

    public void setProfilers(ListProperty<String> profilers) {
        this.profilers = profilers;
    }

    @Input
    public ListProperty<String> getWarmupBenchmarks() {
        return warmupBenchmarks;
    }

    public void setWarmupBenchmarks(ListProperty<String> warmupBenchmarks) {
        this.warmupBenchmarks = warmupBenchmarks;
    }

    @Input
    public MapProperty<String, ListProperty<String>> getBenchmarkParameters() {
        return benchmarkParameters;
    }

    @Option(option = "benchmarkMode", description = "Benchmark mode. Available modes are: [Throughput/thrpt, AverageTime/avgt, SampleTime/sample, SingleShotTime/ss, All/all]")
    public void setBenchmarkParameters(MapProperty<String, ListProperty<String>> benchmarkParameters) {
        this.benchmarkParameters = benchmarkParameters;
    }

    @Input
    public MapProperty<String, Object> getEnvironment() {
        return environment;
    }

    public void setEnvironment(MapProperty<String, Object> environment) {
        this.environment = environment;
    }

    @Input
    public Property<Boolean> getFailOnError() {
        return failOnError;
    }

    @Option(option = "failOnError", description = "Should JMH fail immediately if any benchmark had experienced the unrecoverable error?")
    public void setFailOnError(Boolean failOnError) {
        this.failOnError.set( failOnError);
    }

    @Input
    public Property<Boolean> getForceGC() {
        return forceGC;
    }

    @Option(option = "forceGC", description = "Should JMH force GC between iterations?")
    public void setForceGC(Boolean forceGC) {
        this.forceGC.set(forceGC);
    }

    @Input
    public Property<Boolean> getIncludeTests() {
        return includeTests;
    }

    @Option(option = "includeTests", description = "Include test sources in generated jar file")
    public void setIncludeTests(Property<Boolean> includeTests) {
        this.includeTests = includeTests;
    }

    @Input
    public Property<Boolean> getSynchronizeIterations() {
        return synchronizeIterations;
    }

    public void setSynchronizeIterations(Property<Boolean> synchronizeIterations) {
        this.synchronizeIterations = synchronizeIterations;
    }

    @Input
    public Property<Boolean> getZip64() {
        return zip64;
    }

    public void setZip64(Property<Boolean> zip64) {
        this.zip64 = zip64;
    }

    @Input
    public Property<DuplicatesStrategy> getDuplicateClassesStrategy() {
        return duplicateClassesStrategy;
    }

    public void setDuplicateClassesStrategy(Property<DuplicatesStrategy> duplicateClassesStrategy) {
        this.duplicateClassesStrategy = duplicateClassesStrategy;
    }

    @Input
    public Property<Integer> getBatchSize() {
        return batchSize;
    }

    @Option(option = "batchSize", description = "Batch size: number of benchmark method calls per operation. (some benchmark modes can ignore this setting)")
    public void setBatchSize(Integer batchSize) {
        this.batchSize.set(batchSize);
    }

    @Input
    public Property<Integer> getFork() {
        return fork;
    }

    @Option(option = "fork", description = "How many times to forks a single benchmark. Use 0 to disable forking altogether")
    public void setFork(Integer fork) {
        this.fork.set(fork);
    }

    @Input
    public Property<Integer> getIterations() {
        return iterations;
    }

    @Option(option = "iterations", description = "Number of measurement iterations to do.")
    public void setIterations(Integer iterations) {
        this.iterations.set(iterations);
    }

    @Input
    public Property<Integer> getOperationsPerInvocation() {
        return operationsPerInvocation;
    }

    public void setOperationsPerInvocation(Property<Integer> operationsPerInvocation) {
        this.operationsPerInvocation = operationsPerInvocation;
    }

    @Input
    public Property<Integer> getThreads() {
        return threads;
    }

    public void setThreads(Property<Integer> threads) {
        this.threads = threads;
    }

    @Input
    public Property<Integer> getWarmupBatchSize() {
        return warmupBatchSize;
    }

    public void setWarmupBatchSize(Property<Integer> warmupBatchSize) {
        this.warmupBatchSize = warmupBatchSize;
    }

    @Input
    public Property<Integer> getWarmupForks() {
        return warmupForks;
    }

    public void setWarmupForks(Property<Integer> warmupForks) {
        this.warmupForks = warmupForks;
    }

    @Input
    public Property<Integer> getWarmupIterations() {
        return warmupIterations;
    }

    public void setWarmupIterations(Property<Integer> warmupIterations) {
        this.warmupIterations = warmupIterations;
    }

    @Input
    public Property<String> getJmhTimeout() {
        return jmhTimeout;
    }

    public void setJmhTimeout(Property<String> jmhTimeout) {
        this.jmhTimeout = jmhTimeout;
    }

    @Input
    public Property<String> getJmhVersion() {
        return jmhVersion;
    }

    @Option(option = "jmhVersion", description = "JMH version")
    public void setJmhVersion(String jmhVersion) {
        this.jmhVersion.set(jmhVersion);
    }

    @Input
    public Property<String> getJvm() {
        return jvm;
    }

    public void setJvm(Property<String> jvm) {
        this.jvm = jvm;
    }

    @Input
    public Property<String> getResultExtension() {
        return resultExtension;
    }

    public void setResultExtension(Property<String> resultExtension) {
        this.resultExtension = resultExtension;
    }

    @Input
    public Property<String> getResultFormat() {
        return resultFormat;
    }

    public void setResultFormat(Property<String> resultFormat) {
        this.resultFormat = resultFormat;
    }

    @Input
    public Property<String> getTimeOnIteration() {
        return timeOnIteration;
    }

    public void setTimeOnIteration(Property<String> timeOnIteration) {
        this.timeOnIteration = timeOnIteration;
    }

    @Input
    public Property<String> getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(Property<String> timeUnit) {
        this.timeUnit = timeUnit;
    }

    @Input
    public Property<String> getVerbosity() {
        return verbosity;
    }

    public void setVerbosity(Property<String> verbosity) {
        this.verbosity = verbosity;
    }

    @Input
    public Property<String> getWarmup() {
        return warmup;
    }

    public void setWarmup(Property<String> warmup) {
        this.warmup = warmup;
    }

    @Input
    public Property<String> getWarmupMode() {
        return warmupMode;
    }

    public void setWarmupMode(Property<String> warmupMode) {
        this.warmupMode = warmupMode;
    }

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
        ParameterConverter.collectParameters((JmhParameters) this, jmhArgs);
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
}
