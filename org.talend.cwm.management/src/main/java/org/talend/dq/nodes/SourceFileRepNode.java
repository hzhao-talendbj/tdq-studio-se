// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dq.nodes;

import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.dataquality.properties.TDQSourceFileItem;
import org.talend.repository.model.RepositoryNode;


/**
 * DOC klliu  class global comment. Detailled comment
 */
public class SourceFileRepNode extends RepositoryNode {

    /**
     * DOC klliu SourceFileRepNode constructor comment.
     * @param object
     * @param parent
     * @param type
     */
    public SourceFileRepNode(IRepositoryViewObject object, RepositoryNode parent, ENodeType type) {
        super(object, parent, type);
    }

    public String getLabel() {
        TDQSourceFileItem item = (TDQSourceFileItem) this.getObject().getProperty().getItem();
        return item.getName() + "." + item.getExtension();
    }
}
