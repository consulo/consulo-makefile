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
import consulo.annotation.component.ServiceAPI;
import consulo.annotation.component.ServiceImpl;
import consulo.component.persist.PersistentStateComponent;
import consulo.component.persist.State;
import consulo.component.persist.Storage;
import consulo.component.persist.StoragePathMacros;
import consulo.externalSystem.setting.AbstractExternalSystemSettings;
import consulo.externalSystem.setting.DelegatingExternalSystemSettingsListener;
import consulo.externalSystem.setting.ExternalSystemSettingsListener;
import consulo.project.Project;
import consulo.util.xml.serializer.annotation.AbstractCollection;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.Set;
import java.util.TreeSet;

/**
 * @author VISTALL
 * @since 2026-10-25
 */
@ServiceAPI(ComponentScope.PROJECT)
@ServiceImpl
@Singleton
@State(name = "MakeSettings", storages = @Storage(file = StoragePathMacros.PROJECT_CONFIG_DIR + "/make.xml"))
public class MakeSettings extends AbstractExternalSystemSettings<MakeSettings, MakeProjectSettings, MakeSettingsListener>
    implements PersistentStateComponent<MakeSettings.MyState> {

    @Inject
    public MakeSettings(Project project) {
        super(MakeSettingsListener.TOPIC, project);
    }

    public static MakeSettings getInstance(Project project) {
        return project.getInstance(MakeSettings.class);
    }

    @Override
    public void subscribe(ExternalSystemSettingsListener<MakeProjectSettings> listener) {
        getProject().getMessageBus().connect(getProject()).subscribe(MakeSettingsListener.TOPIC, new MakeSettingsListenerAdapter(listener));
    }

    private static class MakeSettingsListenerAdapter extends DelegatingExternalSystemSettingsListener<MakeProjectSettings>
        implements MakeSettingsListener {
        MakeSettingsListenerAdapter(ExternalSystemSettingsListener<MakeProjectSettings> delegate) {
            super(delegate);
        }
    }

    @Override
    protected void copyExtraSettingsFrom(MakeSettings settings) {
    }

    @Override
    protected void checkSettings(MakeProjectSettings old, MakeProjectSettings current) {
    }

    @Override
    public MyState getState() {
        MyState state = new MyState();
        fillState(state);
        return state;
    }

    @Override
    public void loadState(MyState state) {
        super.loadState(state);
    }

    public static class MyState implements State<MakeProjectSettings> {
        private Set<MakeProjectSettings> myProjectSettings = new TreeSet<>();

        @Override
        @AbstractCollection(surroundWithTag = false, elementTypes = {MakeProjectSettings.class})
        public Set<MakeProjectSettings> getLinkedExternalProjectsSettings() {
            return myProjectSettings;
        }

        @Override
        public void setLinkedExternalProjectsSettings(Set<MakeProjectSettings> settings) {
            if (settings != null) {
                myProjectSettings.addAll(settings);
            }
        }
    }
}
