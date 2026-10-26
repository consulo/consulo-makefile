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
package consulo.makefile.build.ui;

import consulo.annotation.component.ExtensionImpl;
import consulo.makefile.build.MakeConstants;
import consulo.makefile.icon.MakeFileIconGroup;
import consulo.externalSystem.ui.AbstractExternalSystemToolWindowFactory;
import consulo.ui.ex.toolWindow.ToolWindowAnchor;
import consulo.ui.image.Image;

/**
 * The Make tool window: the linked projects and their targets.
 *
 * @author VISTALL
 * @since 2026-10-25
 */
@ExtensionImpl
public class MakeToolWindowFactory extends AbstractExternalSystemToolWindowFactory {
    public MakeToolWindowFactory() {
        super(MakeConstants.SYSTEM_ID);
    }

    @Override
    public ToolWindowAnchor getAnchor() {
        return ToolWindowAnchor.RIGHT;
    }

    @Override
    public Image getIcon() {
        return MakeFileIconGroup.makefile();
    }
}
