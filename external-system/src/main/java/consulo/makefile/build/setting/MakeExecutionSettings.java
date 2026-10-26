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

import consulo.externalSystem.model.setting.ExternalSystemExecutionSettings;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * @author VISTALL
 * @since 2026-10-25
 */
public class MakeExecutionSettings extends ExternalSystemExecutionSettings {
    private final @Nullable String myBuildDirectory;
    private final String myMakeArguments;
    private final String myConfigureArguments;

    public MakeExecutionSettings(@Nullable String buildDirectory, String makeArguments, String configureArguments) {
        myBuildDirectory = buildDirectory;
        myMakeArguments = makeArguments;
        myConfigureArguments = configureArguments;
    }

    public @Nullable String getBuildDirectory() {
        return myBuildDirectory;
    }

    public String getMakeArguments() {
        return myMakeArguments;
    }

    public String getConfigureArguments() {
        return myConfigureArguments;
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + Objects.hash(myBuildDirectory, myMakeArguments, myConfigureArguments);
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (!super.equals(o) || !(o instanceof MakeExecutionSettings that)) {
            return false;
        }
        return Objects.equals(myBuildDirectory, that.myBuildDirectory)
            && myMakeArguments.equals(that.myMakeArguments)
            && myConfigureArguments.equals(that.myConfigureArguments);
    }
}
