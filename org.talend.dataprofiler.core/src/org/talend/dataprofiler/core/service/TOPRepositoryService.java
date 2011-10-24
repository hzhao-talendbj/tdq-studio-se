// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.ITDQRepositoryService;
import org.talend.core.model.context.ContextUtils;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.core.model.metadata.builder.connection.MDMConnection;
import org.talend.core.model.metadata.builder.util.MetadataConnectionUtils;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.ContextItem;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.cwm.compare.exception.ReloadCompareException;
import org.talend.cwm.compare.factory.ComparisonLevelFactory;
import org.talend.cwm.compare.factory.IComparisonLevel;
import org.talend.cwm.db.connection.ConnectionUtils;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.cwm.relational.TdExpression;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.editor.PartListener;
import org.talend.dataprofiler.core.ui.editor.connection.ConnectionEditor;
import org.talend.dataprofiler.core.ui.editor.connection.ConnectionItemEditorInput;
import org.talend.dataprofiler.core.ui.editor.dqrules.DQRuleEditor;
import org.talend.dataprofiler.core.ui.editor.parserrules.ParserRuleItemEditorInput;
import org.talend.dataprofiler.core.ui.utils.WorkbenchUtils;
import org.talend.dataquality.indicators.definition.IndicatorCategory;
import org.talend.dataquality.rules.ParserRule;
import org.talend.dq.CWMPlugin;
import org.talend.dq.dqrule.DqRuleBuilder;
import org.talend.dq.helper.EObjectHelper;
import org.talend.dq.helper.PropertyHelper;
import org.talend.dq.helper.resourcehelper.DQRuleResourceFileHelper;
import org.talend.dq.indicators.definitions.DefinitionHandler;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.repository.model.IRepositoryNode;
import org.talend.resource.ResourceManager;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class TOPRepositoryService implements ITDQRepositoryService {

    private static Logger log = Logger.getLogger(TOPRepositoryService.class);

    public IViewPart getTDQRespositoryView() {
        return CorePlugin.getDefault().getRepositoryView();
    }

    public void notifySQLExplorer(Item... items) {
        if (items == null) {
            return;
        }

        for (Item item : items) {
            if (item instanceof ConnectionItem) {
                Connection connection = ((ConnectionItem) item).getConnection();
                CWMPlugin.getDefault().addConnetionAliasToSQLPlugin(connection);
            }
        }
    }

    public void openEditor(Item item) {

        Class<?> clazz = null;
        IEditorInput editorInput = null;
        if (item instanceof ConnectionItem) {
            clazz = ConnectionEditor.class;
            editorInput = new ConnectionItemEditorInput(item);
        }

        if (editorInput != null && clazz != null) {
            CorePlugin.getDefault().closeEditorIfOpened(item);
            CorePlugin.getDefault().openEditor(editorInput, clazz.getName());
        }
    }

    /**
     * Fill MDM connection only.
     */
    public void fillMetadata(ConnectionItem connItem) {
        MetadataConnectionUtils.fillConnectionInformation(connItem);
        // MOD gdbu 2011-7-12 bug : 22598
        MDMConnection mdmConnection = (MDMConnection) connItem.getConnection();
        mdmConnection.setLabel(connItem.getProperty().getLabel() + "");
        mdmConnection.setName(connItem.getProperty().getLabel() + "");
        ConnectionUtils.fillMdmConnectionInformation(mdmConnection);
        ElementWriterFactory.getInstance().createDataProviderWriter().save(mdmConnection);
        // ~22598
    }

    public void refresh() {
        CorePlugin.getDefault().refreshWorkSpace();
        CorePlugin.getDefault().refreshDQView();
    }

    public void initProxyRepository() {
        CorePlugin.getDefault().initProxyRepository();
    }

    public void addPartListener() {
        IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        // Calculate the extensions to register partListener.
        IPartListener listener = PartListener.getPartListener();
        if (listener != null) {
            activePage.addPartListener(listener);
        }
    }

    public boolean removeAliasInSQLExplorer(IRepositoryNode children) {
        boolean hasDependencyItem = true;
        // MOD klliu 2011-04-28 bug 20204 removing connection is synced to the connection view of SQL explore
        Item item = children.getObject().getProperty().getItem();
        // MOD mzhao filter the connections which is not a type of database.
        if (item != null && item instanceof ConnectionItem
                && ((ConnectionItem) item).getConnection() instanceof DatabaseConnection) {
            Connection connection = ((ConnectionItem) item).getConnection();
            List<ModelElement> dependencyClients = EObjectHelper.getDependencyClients(connection);
            if (!(dependencyClients == null || dependencyClients.isEmpty())) {
                hasDependencyItem = false;
            } else {
                CWMPlugin.getDefault().removeAliasInSQLExplorer(connection);
            }
        }

        return hasDependencyItem;
    }

    public void createParserRuleItem(ArrayList<HashMap<String, Object>> values, String parserRuleName) {
        ParserRule parserRule = null;
        DqRuleBuilder ruleBuilder = new DqRuleBuilder();
        boolean ruleInitialized = ruleBuilder.initializeParserRuleBuilder(parserRuleName);
        if (ruleInitialized) {
            parserRule = ruleBuilder.getParserRule();
        }
        TaggedValueHelper.setValidStatus(true, parserRule);
        for (HashMap<String, Object> expression : values) {

            parserRule.addExpression(expression.get(RULE_NAME).toString(),
                    expression.get(RULE_TYPE) instanceof Integer ? Integer.toString((Integer) expression.get(RULE_TYPE))
                            : expression.get(RULE_TYPE).toString(), expression.get(RULE_VALUE).toString());
        }
        IndicatorCategory ruleIndicatorCategory = DefinitionHandler.getInstance().getDQRuleIndicatorCategory();
        if (ruleIndicatorCategory != null && !parserRule.getCategories().contains(ruleIndicatorCategory)) {
            parserRule.getCategories().add(ruleIndicatorCategory);
        }
        IFolder folder = ResourceManager.getRulesParserFolder();
        TypedReturnCode<Object> returnObject = ElementWriterFactory.getInstance().createdRuleWriter().create(parserRule, folder);
        Object object = returnObject.getObject();
        if (object instanceof Item) {
            ParserRuleItemEditorInput parserRuleEditorInput = new ParserRuleItemEditorInput((Item) object);
            CorePlugin.getDefault().openEditor(parserRuleEditorInput, DQRuleEditor.class.getName());
            this.refresh();
        }
    }

    public List<Map<String, String>> getPaserRulesFromRules(Object parser) {
        if (parser != null && parser instanceof ParserRule) {
            List<Map<String, String>> ruleValues = new ArrayList<Map<String, String>>();
            for (TdExpression exp : ((ParserRule) parser).getExpression()) {
                Map<String, String> pr = new HashMap<String, String>();
                // MOD yyi 2011-08-12 TDQ-1698:avoid importing null value
                pr.put(RULE_NAME, null == exp.getName() ? StringUtils.EMPTY : exp.getName());
                pr.put(RULE_VALUE, null == exp.getBody() ? StringUtils.EMPTY : exp.getBody());
                pr.put(RULE_TYPE, null == exp.getLanguage() ? StringUtils.EMPTY : exp.getLanguage().toUpperCase());
                ruleValues.add(pr);
            }
            return ruleValues;
        }
        return null;
    }

    /*
     * Added yyi 2011-08-04 TDQ-3186
     * 
     * @see org.talend.core.ITDQRepositoryService#getPaserRulesFromResources(java.lang.Object[])
     */
    public List<Map<String, String>> getPaserRulesFromResources(Object[] rules) {
        List<Map<String, String>> ruleValues = new ArrayList<Map<String, String>>();
        for (Object rule : rules) {
            if (rule instanceof IFile) {
                ParserRule parserRule = (ParserRule) DQRuleResourceFileHelper.getInstance().findDQRule((IFile) rule);
                ruleValues.addAll(getPaserRulesFromRules(parserRule));
            }
        }
        return ruleValues;
    }

    /*
     * Add qiongli TDQ-3317 when context is changed,should retrive the related database info(schema,catalog) and sql
     * exploer.
     * 
     * @see org.talend.core.ITDQRepositoryService#reloadDatabase(org.talend.core.model.properties.ConnectionItem)
     */
    public void reloadDatabase(ContextItem contextItem) {
        IProxyRepositoryFactory factory = ProxyRepositoryFactory.getInstance();
        try {
            List<IRepositoryViewObject> dbConnList = factory.getAll(ERepositoryObjectType.METADATA_CONNECTIONS, true);
            for (IRepositoryViewObject obj : dbConnList) {
                Item item = obj.getProperty().getItem();
                if (item instanceof ConnectionItem) {
                    Connection conn = ((ConnectionItem) item).getConnection();
                    if (conn.isContextMode()) {
                        ContextItem cItem = ContextUtils.getContextItemById2(conn.getContextId());
                        if (contextItem == null) {
                            continue;
                        }
                        if (cItem == contextItem) {
                            if (conn instanceof DatabaseConnection) {
                                final IComparisonLevel creatComparisonLevel = ComparisonLevelFactory.creatComparisonLevel(conn);
                                Connection newConnection = creatComparisonLevel.reloadCurrentLevelElement();
                                // update the sql explore.
                                Property property = PropertyHelper.getProperty(newConnection);
                                if (property != null) {
                                    Item newItem = property.getItem();
                                    if (newItem != null) {
                                        CWMPlugin.getDefault()
                                                .updateConnetionAliasByName(newConnection, newConnection.getLabel());
                                        // notifySQLExplorer(newItem);
                                    }
                                }
                                // update the related analyses.
                                List<ModelElement> dependencyClients = EObjectHelper.getDependencyClients(conn);
                                if (!(dependencyClients == null || dependencyClients.isEmpty())) {
                                    MessageDialog.openWarning(
                                            null,
                                            DefaultMessagesImpl.getString("TOPRepositoryService.dependcyTile"),
                                            DefaultMessagesImpl.getString("TOPRepositoryService.dependcyMessage",
                                                    newConnection.getLabel()));
                                }
                                WorkbenchUtils.impactExistingAnalyses(newConnection);
                            }
                        }
                    }
                }
            }
            List<IRepositoryViewObject> fileConnList = factory.getAll(ERepositoryObjectType.METADATA_FILE_DELIMITED, true);
            for (IRepositoryViewObject obj : fileConnList) {
                Item item = obj.getProperty().getItem();
                if (item instanceof ConnectionItem) {
                    // TODO reload struct for DelimitedFile connection
                }
            }
        } catch (PersistenceException e) {
            log.error(e, e);
        } catch (ReloadCompareException e) {
            log.error(e, e);
        } catch (PartInitException e) {
            log.error(e);
        }

    }

    public void updateImpactOnAnalysis(ConnectionItem connectionItem) {
        try {
            if (connectionItem == null) {
                return;
            }
            Connection connection = connectionItem.getConnection();
            List<ModelElement> clientDependencys = EObjectHelper.getDependencyClients(connection);
            if (clientDependencys != null && clientDependencys.size() > 0) {
                MessageDialog.openInformation(null, DefaultMessagesImpl.getString("TOPRepositoryService.dependcyTile"),
                        DefaultMessagesImpl.getString("TOPRepositoryService.dependcyMessage", connectionItem.getProperty()
                                        .getLabel()));
                WorkbenchUtils.impactExistingAnalyses(connection);
            }

        } catch (PartInitException e) {
            e.printStackTrace();
        }
    }
}
