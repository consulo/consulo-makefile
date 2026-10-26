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

import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.TopicAPI;
import consulo.externalSystem.setting.ExternalSystemSettingsListener;

import java.util.Collection;
import java.util.Set;

/**
 * @author VISTALL
 * @since 2026-10-25
 */
@TopicAPI(ComponentScope.PROJECT)
public interface MakeSettingsListener extends ExternalSystemSettingsListener<MakeProjectSettings> {
    Class<MakeSettingsListener> TOPIC = MakeSettingsListener.class;

    @Override
    default void onProjectRenamed(String oldName, String newName) {
    }

    @Override
    default void onProjectsLoaded(Collection<MakeProjectSettings> settings) {
    }

    @Override
    default void onProjectsLinked(Collection<MakeProjectSettings> settings) {
    }

    @Override
    default void onProjectsUnlinked(Set<String> linkedProjectPaths) {
    }

    @Override
    default void onBulkChangeStart() {
    }

    @Override
    default void onBulkChangeEnd() {
    }
}
