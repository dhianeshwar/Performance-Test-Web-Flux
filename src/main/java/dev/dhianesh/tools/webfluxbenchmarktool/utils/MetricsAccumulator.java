package dev.dhianesh.tools.webfluxbenchmarktool.utils;

import dev.dhianesh.tools.webfluxbenchmarktool.services.NonBlockingService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MetricsAccumulator {
    private final AtomicInteger successCount = new AtomicInteger(0);
    private final AtomicInteger failureCount = new AtomicInteger(0);
    private final AtomicInteger readTimeoutCount = new AtomicInteger(0);
    private final AtomicInteger totalRequests = new AtomicInteger(0);
    private final AtomicInteger sumLatencyMillis = new AtomicInteger(0);
    private final ConcurrentHashMap<String, AtomicInteger> errorTypeCounts = new ConcurrentHashMap<>();

    public void accumulate(NonBlockingService.RequestResult r) {
        totalRequests.incrementAndGet();
        sumLatencyMillis.addAndGet((int) r.durationMillis());
        if (r.success()) {
            successCount.incrementAndGet();
        } else {
            failureCount.incrementAndGet();
            errorTypeCounts
                    .computeIfAbsent(r.errorType(), k -> new AtomicInteger())
                    .incrementAndGet();
            if ("READ_TIMEOUT".equals(r.errorType())) {
                readTimeoutCount.incrementAndGet();
            }
        }
    }

    public int getSuccessCount() {
        return successCount.get();
    }

    public int getFailureCount() {
        return failureCount.get();
    }

    public int getReadTimeoutCount() {
        return readTimeoutCount.get();
    }

    public int getTotalRequests() {
        return totalRequests.get();
    }

    public int getSumLatencyMillis() {
        return sumLatencyMillis.get();
    }

    public Map<String, AtomicInteger> getErrorTypeCounts() {
        return errorTypeCounts;
    }

}
