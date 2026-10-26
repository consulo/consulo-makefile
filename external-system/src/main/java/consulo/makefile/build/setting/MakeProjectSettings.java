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
package consulo.makefile.build.setting;

import consulo.externalSystem.setting.ExternalProjectSettings;
import org.jspecify.annotations.Nullable;

/**
 * @author VISTALL
 * @since 2026-10-25
 */
public class MakeProjectSettings extends ExternalProjectSettings {
    private @Nullable String myBuildDirectory;
    private @Nullable String myMakeArguments;
    private @Nullable String myConfigureArguments;

    /**
     * The directory make runs in, relative to the sources; {@code null} to build in the sources.
     */
    public @Nullable String getBuildDirectory() {
        return myBuildDirectory;
    }

    public void setBuildDirectory(@Nullable String buildDirectory) {
        myBuildDirectory = buildDirectory;
    }

    /**
     * Arguments of every make run - {@code -j8}, {@code CC=clang}, {@code -f build.mk}.
     */
    public @Nullable String getMakeArguments() {
        return myMakeArguments;
    }

    public void setMakeArguments(@Nullable String makeArguments) {
        myMakeArguments = makeArguments;
    }

    /**
     * Arguments of the configure script, when the import runs it.
     */
    public @Nullable String getConfigureArguments() {
        return myConfigureArguments;
    }

    public void setConfigureArguments(@Nullable String configureArguments) {
        myConfigureArguments = configureArguments;
    }

    @Override
    public MakeProjectSettings clone() {
        MakeProjectSettings clone = new MakeProjectSettings();
        copyTo(clone);
        clone.myBuildDirectory = myBuildDirectory;
        clone.myMakeArguments = myMakeArguments;
        clone.myConfigureArguments = myConfigureArguments;
        return clone;
    }
}
