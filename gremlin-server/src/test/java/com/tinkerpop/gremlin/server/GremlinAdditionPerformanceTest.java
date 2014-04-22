package com.tinkerpop.gremlin.server;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import com.carrotsearch.junitbenchmarks.annotation.AxisRange;
import com.carrotsearch.junitbenchmarks.annotation.BenchmarkHistoryChart;
import com.carrotsearch.junitbenchmarks.annotation.BenchmarkMethodChart;
import com.carrotsearch.junitbenchmarks.annotation.LabelType;
import com.tinkerpop.gremlin.driver.Client;
import com.tinkerpop.gremlin.driver.Cluster;
import com.tinkerpop.gremlin.driver.Item;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import java.util.Random;

import static org.junit.Assert.assertEquals;

/**
 * Execute a simple script (1+1).
 *
 * @author Stephen Mallette (http://stephen.genoprime.com)
 */
@AxisRange(min = 0, max = 1)
@BenchmarkMethodChart(filePrefix = "gremlin-addition")
@BenchmarkHistoryChart(labelWith = LabelType.CUSTOM_KEY, maxRuns = 20, filePrefix = "hx-gremlin-addition")
public class GremlinAdditionPerformanceTest extends AbstractGremlinServerPerformanceTest {

    public final static int DEFAULT_BENCHMARK_ROUNDS = 50;
    public final static int DEFAULT_WARMUP_ROUNDS = 5;

    public final static int DEFAULT_CONCURRENT_BENCHMARK_ROUNDS = 500;
    public final static int DEFAULT_CONCURRENT_WARMUP_ROUNDS = 10;

    private final static Cluster cluster = Cluster.create("localhost").build();
    private final static Random rand = new Random();

    @Rule
    public TestRule benchmarkRun = new BenchmarkRule();

    @BenchmarkOptions(benchmarkRounds = DEFAULT_BENCHMARK_ROUNDS, warmupRounds = DEFAULT_WARMUP_ROUNDS, concurrency = BenchmarkOptions.CONCURRENCY_SEQUENTIAL)
    @Test
    public void webSocketsGremlin() throws Exception {
        tryWebSocketGremlin();
    }

    @BenchmarkOptions(benchmarkRounds = DEFAULT_CONCURRENT_BENCHMARK_ROUNDS, warmupRounds = DEFAULT_CONCURRENT_WARMUP_ROUNDS, concurrency = BenchmarkOptions.CONCURRENCY_AVAILABLE_CORES)
    @Test
    public void webSocketsGremlinConcurrent() throws Exception {
        tryWebSocketGremlin();
    }

    @Ignore("need a non-json format in play to truly validate this")
    @BenchmarkOptions(benchmarkRounds = 10, warmupRounds = 1, concurrency = BenchmarkOptions.CONCURRENCY_AVAILABLE_CORES)
    @Test
    public void webSocketsGremlinConcurrentAlternateSerialization() throws Exception {
        final String mimeType = rand.nextDouble() > 0.5d ? "application/json" : "application/vnd.gremlin-v1.0+json";
        System.out.println(mimeType);
        final Cluster cluster = Cluster.create("localhost")
                .serializer(mimeType)
                .build();
        final Client client = cluster.connect();
        assertEquals("2", client.submit("1+1").stream().map(Item::getString).findFirst().orElse("invalid"));
    }

    @BeforeClass
    public static void before() {
        // good to call init here ahead of performance tracking
        cluster.init();
    }

    @AfterClass
    public static void after() {
        cluster.close();
    }

    private void tryWebSocketGremlin() throws Exception {
        final Client client = cluster.connect();
        assertEquals("2", client.submit("1+1").stream().map(Item::getString).findFirst().orElse("invalid"));
    }
}
