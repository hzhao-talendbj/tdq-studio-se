// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.editor;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.helper.WorkspaceResourceHelper;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dq.helper.ProxyRepositoryManager;
import org.talend.dq.writer.EMFSharedResources;
import org.talend.repository.model.ERepositoryStatus;

/**
 * DOC qiongli class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 55206 2011-02-15 17:32:14Z mhirt $
 * 
 */
public class CommonEditorPartListener extends PartListener {

    public Item getItem(IEditorPart editor) {
        Item tdqItem = null;
        IEditorInput editorInput = editor.getEditorInput();
        if (editorInput instanceof AbstractItemEditorInput) {
            return ((AbstractItemEditorInput) editorInput).getItem();
        }

        IFile propertyFile = getPropertyFile(editor);

        if (propertyFile == null) {
            return null;
        }
        URI uri = URI.createPlatformResourceURI(propertyFile.getFullPath().toString(), false);
        EMFSharedResources.getInstance().unloadResource(uri.toString());
        Resource propertyResource = EMFSharedResources.getInstance().getResource(uri, true);
        if (propertyResource != null) {
            EList<EObject> contents = propertyResource.getContents();
            Property property = null;
            for (EObject obj : contents) {
                if (obj instanceof Property) {
                    property = (Property) obj;
                    break;
                }
            }
            if (property != null) {
                tdqItem = property.getItem();
            }
        }
        return tdqItem;
    }

    @Override
    public void partClosed(IWorkbenchPart part) {
        // MOD mzhao bug 12497 Firstly check if the part is TDQ common form editor.
        if (!isCommonFormEditor(part)) {
            return;
        }
        Item item = getItem((IEditorPart) part);
        if (item == null) {
            return;
        }
        // MOD qiongli 2011-7-14 bug 22276,just unlock and commit for editable itme.
        if (ProxyRepositoryManager.getInstance().isReadOnly() || ProxyRepositoryManager.getInstance().isEditable(item)) {
            ProxyRepositoryManager.getInstance().unLock(item);
            WorkspaceResourceHelper.refreshItem(item);
        } else {
            ProxyRepositoryManager.getInstance().refresh();
            CorePlugin.getDefault().refreshDQView();
        }

        super.partClosed(part);
    }

    @Override
    public void partOpened(IWorkbenchPart part) {
        // MOD mzhao bug 12497 Firstly check if the part is TDQ common form editor.
        if (!isCommonFormEditor(part)) {
            return;
        }
        Item item = getItem((IEditorPart) part);
        if (item == null) {
            return;
        }

        CorePlugin.getDefault().refreshWorkSpace();
        // If the item is not editable.
        // MOD 20130624 TDQ-7497 yyin, change to use :isEditableAndLockIfPossible instead of lock method
        // (when remote and ask user: when the user select unlock should be also not editable)
        if (!ProxyRepositoryFactory.getInstance().isEditableAndLockIfPossible(item)) {
            String message = ""; //$NON-NLS-1$
            ERepositoryStatus status = ProxyRepositoryFactory.getInstance().getStatus(item);
            // when the item is not editable: one status is locked by
            if (status == ERepositoryStatus.LOCK_BY_OTHER) {
                message = DefaultMessagesImpl.getString("CommonEditorPartListener.lockByOthers"); //$NON-NLS-1$
            } else {
                message = DefaultMessagesImpl.getString("CommonEditorPartListener.readOnlyUser"); //$NON-NLS-1$
            }
            // ADD xwang 2011-08-30 if item was locked by others pop up a dialog to tell user
            MessageDialog.openWarning(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "", part.getTitle() //$NON-NLS-1$
                    + DefaultMessagesImpl.getString("CommonEditorPartListener.notEditable") + message); //$NON-NLS-1$
            // alert user that the item is not able to edit.
            // MOD yyi 2010-11-29 15686: Make the editor readonly when the login user has no sufficient previlege.
            lockCommonFormEditor(part);
            WorkspaceResourceHelper.refreshItem(item);
            return;
        }
        WorkspaceResourceHelper.refreshItem(item);
        super.partOpened(part);
    }

}