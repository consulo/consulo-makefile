/*
 * Copyright 2013-2026 consulo.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package consulo.makefile.build.service;

import consulo.process.ExecutionException;
import consulo.process.ProcessHandler;
import consulo.process.ProcessHandlerBuilder;
import consulo.process.ProcessOutputType;
import consulo.process.cmd.GeneralCommandLine;
import consulo.process.event.ProcessEvent;
import consulo.process.event.ProcessListener;
import consulo.util.dataholder.Key;
import org.jspecify.annotations.Nullable;

import java.util.function.BiConsumer;

/**
 * Runs the tools of a Make project - configure, make - one at a time, so a cancel stops the one running.
 *
 * @author VISTALL
 * @since 2026-10-25
 */
public final class MakeProcessRunner {
    public static final int CANCELLED_EXIT_CODE = Integer.MIN_VALUE;

    private static final long WAIT_INTERVAL_MS = 100;

    private volatile @Nullable ProcessHandler myHandler;
    private volatile boolean myCancelled;

    /**
     * @param output receives the text, and whether it is standard output rather than standard error
     * @return the exit code, {@link #CANCELLED_EXIT_CODE} when cancelled
     */
    public int run(GeneralCommandLine commandLine, BiConsumer<String, Boolean> output) throws ExecutionException {
        if (myCancelled) {
            return CANCELLED_EXIT_CODE;
        }

        ProcessHandler handler = ProcessHandlerBuilder.create(commandLine).build();
        handler.addProcessListener(new ProcessListener() {
            @Override
            public void onTextAvailable(ProcessEvent event, Key outputType) {
                if (outputType != ProcessOutputType.SYSTEM) {
                    output.accept(event.getText(), outputType != ProcessOutputType.STDERR);
                }
            }
        });

        myHandler = handler;
        try {
            handler.startNotify();
            while (!handler.waitFor(WAIT_INTERVAL_MS)) {
                if (myCancelled) {
                    handler.destroyProcess();
                    handler.waitFor();
                    return CANCELLED_EXIT_CODE;
                }
            }
        }
        finally {
            myHandler = null;
        }

        Integer exitCode = handler.getExitCode();
        return exitCode == null ? -1 : exitCode;
    }

    public boolean isCancelled() {
        return myCancelled;
    }

    public void cancel() {
        myCancelled = true;
        ProcessHandler handler = myHandler;
        if (handler != null) {
            handler.destroyProcess();
        }
    }
}
