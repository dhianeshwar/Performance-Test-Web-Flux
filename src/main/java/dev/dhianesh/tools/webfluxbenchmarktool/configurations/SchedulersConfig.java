package dev.dhianesh.tools.webfluxbenchmarktool.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.Executor;

@Configuration
public class SchedulersConfig {

    @Bean
    public Executor virtualThreadPoolExecutor() {
       SimpleAsyncTaskExecutor simpleAsyncTaskExecutor = new SimpleAsyncTaskExecutor();
       simpleAsyncTaskExecutor.setThreadNamePrefix("VT-");

       return simpleAsyncTaskExecutor;

    }

    @Bean
    public Scheduler virtualThreadScheduler(Executor virtualThreadPoolExecutor) {
        return Schedulers.fromExecutor(virtualThreadPoolExecutor);

    }
}
