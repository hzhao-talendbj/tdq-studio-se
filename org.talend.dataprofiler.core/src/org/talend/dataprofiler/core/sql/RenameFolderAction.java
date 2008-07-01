// ============================================================================
//
// Copyright (C) 2006-2007 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package org.talend.dataprofiler.core.sql;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Display;

/**
 * DOC qzhang class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40Z qzhang $
 * 
 */
public class RenameFolderAction extends Action {

    private IFolder obj;

    /**
     * DOC qzhang RenameFolderAction constructor comment.
     * 
     * @param obj
     */
    public RenameFolderAction(IFolder obj) {
        this.obj = obj;
        setText("Rename Folder");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {
        InputDialog dialog = new InputDialog(Display.getDefault().getActiveShell(), "Rename folder name",
                "Input new folder name", null, new IInputValidator() {

                    public String isValid(String newText) {
                        return null;
                    }

                });
        if (dialog.open() == InputDialog.OK) {
            String value2 = dialog.getValue();
            IFolder folder = obj.getParent().getFolder(new Path(value2));
            try {
                obj.move(folder.getFullPath(), true, null);
            } catch (CoreException e) {
                e.printStackTrace();
            }
        }
    }

}
