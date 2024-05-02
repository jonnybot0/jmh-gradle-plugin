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

import org.gradle.api.file.DuplicatesStrategy;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.MapProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.options.Option;

public interface JmhParameters extends WithJavaToolchain {

    @Option(option = "jmhVersion", description = "JMH version")
    @Input
    Property<String> getJmhVersion();

    @Option(option = "includeTests", description = "Include test sources in generated jar file")
    @Input
    Property<Boolean> getIncludeTests();

    @Option(option = "includes", description = "Include pattern for benchmarks to be executed")
    @Input
    ListProperty<String> getIncludes();

    @Option(option = "excludes", description = "Exclude pattern for benchmarks to be executed")
    @Input
    ListProperty<String> getExcludes();

    @Option(option = "benchmarkMode", description = "Benchmark mode. Available modes are: [Throughput/thrpt, AverageTime/avgt, SampleTime/sample, SingleShotTime/ss, All/all]")
    @Input
    ListProperty<String> getBenchmarkMode();

    @Input
    @Optional
    Property<Integer> getIterations();

    @Option(option = "iterations", description = "Number of measurement iterations to do.")
    default void setIterations(Integer iterations) {
        getIterations().set(iterations);
    }

    @Option(option = "batchSize", description = "Batch size: number of benchmark method calls per operation. (some benchmark modes can ignore this setting)")
    @Input
    @Optional
    Property<Integer> getBatchSize();

    @Option(option = "fork", description = "How many times to forks a single benchmark. Use 0 to disable forking altogether")
    @Input
    @Optional
    Property<Integer> getFork();

    @Option(option = "failOnError", description = "Should JMH fail immediately if any benchmark had experienced the unrecoverable error?")
    @Input
    Property<Boolean> getFailOnError();

    @Option(option = "forceGC", description = "Should JMH force GC between iterations?")
    @Input
    Property<Boolean> getForceGC();

    @Input
    @Optional
    Property<String> getJvm();

    @Input
    ListProperty<String> getJvmArgs();

    @Input
    ListProperty<String> getJvmArgsAppend();

    @Input
    ListProperty<String> getJvmArgsPrepend();

    @Input
    @Optional
    Property<Integer> getOperationsPerInvocation();

    @Input
    MapProperty<String, ListProperty<String>> getBenchmarkParameters();

    @Input
    @Optional
    ListProperty<String> getProfilers();

    @Input
    @Optional
    Property<String> getTimeOnIteration();

    @Input
    @Optional
    Property<String> getResultExtension();

    @Input
    Property<String> getResultFormat();

    @Input
    @Optional
    Property<Boolean> getSynchronizeIterations();

    @Input
    @Optional
    Property<Integer> getThreads();

    @Input
    ListProperty<Integer> getThreadGroups();

    @Input
    @Optional
    Property<String> getTimeUnit();

    @Input
    @Optional
    Property<String> getVerbosity();

    @Input
    @Optional
    Property<String> getJmhTimeout();

    @Option(option = "warmup", description = "")
    @Input
    @Optional
    Property<String> getWarmup();

    @Input
    @Optional
    Property<Integer> getWarmupBatchSize();

    @Input
    @Optional
    Property<Integer> getWarmupForks();

    @Option(option = "warmupIterations", description = "")
    @Input
    @Optional
    Property<Integer> getWarmupIterations();

    @Input
    @Optional
    Property<String> getWarmupMode();

    @Input
    @Optional
    ListProperty<String> getWarmupBenchmarks();

    @Input
    Property<Boolean> getZip64();

    @Input
    Property<DuplicatesStrategy> getDuplicateClassesStrategy();

    @Input
    @Optional
    MapProperty<String, Object> getEnvironment();

    RegularFileProperty getHumanOutputFile();

    RegularFileProperty getResultsFile();
}
