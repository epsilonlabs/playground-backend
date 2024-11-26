package org.eclipse.epsilon.labs.playground.execution;

import org.eclipse.epsilon.eol.IEolModule;
import org.eclipse.epsilon.labs.playground.fn.PlaygroundConfiguration;

import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.TaskScheduler;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

/**
 * Allows for automatically scheduling the termination of scripts
 * that run for too long.
 */
@Singleton
public class ScriptTimeoutTerminator {
    @Inject
    private PlaygroundConfiguration config;

    @Inject
    @Named(TaskExecutors.SCHEDULED)
    private TaskScheduler taskScheduler;

    public void scheduleScriptTimeout(IEolModule module) {
        CancellableExecutionController executionController = new CancellableExecutionController();
        module.getContext().getExecutorFactory().setExecutionController(executionController);
        taskScheduler.schedule(config.getScriptTimeout(), executionController::cancelIfRunning);
    }
}
