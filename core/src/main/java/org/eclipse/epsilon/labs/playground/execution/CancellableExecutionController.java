package org.eclipse.epsilon.labs.playground.execution;

import org.eclipse.epsilon.eol.execute.control.DefaultExecutionController;

/**
 * Execution controller which provides a convenient {@link #cancelIfRunning()}
 * method, to be used to limit the processing time given to running an Epsilon
 * script.
 */
public class CancellableExecutionController extends DefaultExecutionController {

    private volatile boolean isCancelled = false;
    private volatile boolean isDisposed = false;
    private final Thread epsilonThread;
    
    public CancellableExecutionController(Thread epsilonThread) {
        this.epsilonThread = epsilonThread;
    }

    public CancellableExecutionController() {
        this(Thread.currentThread());
    }

    @Override
    public boolean isTerminated() {
        return isCancelled;
    }

    @Override
    public void dispose() {
        isDisposed = true;
    }

    public void cancelIfRunning() {
        if (!isDisposed) {
            isCancelled = true;
            epsilonThread.interrupt();
        }
    }

}
