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
package consulo.makefile.build.importProvider;

import consulo.annotation.component.ExtensionImpl;
import consulo.makefile.build.MakeConstants;
import consulo.makefile.build.setting.MakeProjectSettings;
import consulo.makefile.build.setting.MakeSettings;
import consulo.externalSystem.model.ProjectSystemId;
import consulo.externalSystem.service.setting.AbstractExternalProjectSettingsConfigurable;
import consulo.externalSystem.service.setting.ExternalSystemSettingsConfigurableFactory;
import consulo.externalSystem.service.setting.ExternalSystemSettingsPlace;
import consulo.project.ProjectManager;
import consulo.ui.annotation.RequiredUIAccess;

/**
 * @author VISTALL
 * @since 2026-10-25
 */
@ExtensionImpl
public class MakeSettingsConfigurableFactory implements ExternalSystemSettingsConfigurableFactory<MakeProjectSettings, MakeSettings> {
    @Override
    public ProjectSystemId getSystemId() {
        return MakeConstants.SYSTEM_ID;
    }

    @Override
    public MakeProjectSettings createProjectSettings() {
        return new MakeProjectSettings();
    }

    @Override
    public MakeSettings createSystemSettings() {
        return new MakeSettings(ProjectManager.getInstance().getDefaultProject());
    }

    @Override
    @RequiredUIAccess
    public AbstractExternalProjectSettingsConfigurable<MakeProjectSettings> createProjectSettingsConfigurable(
        MakeProjectSettings settings,
        ExternalSystemSettingsPlace place
    ) {
        return new MakeProjectSettingsConfigurable(settings, place);
    }
}
