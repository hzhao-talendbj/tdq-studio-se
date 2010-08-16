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
package org.talend.dq.helper.resourcehelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.resource.Resource;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.cwm.helper.ConnectionHelper;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.helper.ResourceHelper;
import org.talend.cwm.management.i18n.Messages;
import org.talend.cwm.softwaredeployment.TdSoftwareSystem;
import org.talend.dq.CWMPlugin;
import org.talend.dq.writer.EMFSharedResources;
import org.talend.dq.writer.impl.DataProviderWriter;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.resource.ResourceManager;
import org.talend.utils.sugars.ReturnCode;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * This class help the '.prv' file to store the corresponding DataProvider value.
 * 
 */
public final class PrvResourceFileHelper extends ResourceFileMap {

    protected static Logger log = Logger.getLogger(PrvResourceFileHelper.class);

    private Map<IFile, TypedReturnCode<Connection>> providerMap = new HashMap<IFile, TypedReturnCode<Connection>>();

    private static PrvResourceFileHelper instance;

    private PrvResourceFileHelper() {
        super();
    }

    public static PrvResourceFileHelper getInstance() {
        if (instance == null) {
            instance = new PrvResourceFileHelper();
        }
        return instance;
    }

    /**
     * Method "readFromFile".
     * 
     * @param file the file to read
     * @return the Data provider if found.
     */
    public TypedReturnCode<Connection> findProvider(IFile file) {
        if (checkFile(file)) {
            TypedReturnCode<Connection> rc = providerMap.get(file);
            if (rc == null) {
                rc = readFromFile(file);

            }

            return rc;
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dq.helper.resourcehelper.ResourceFileMap#findCorrespondingFile(orgomg.cwm.objectmodel.core.ModelElement
     * )
     */
    @Override
    public IFile findCorrespondingFile(ModelElement element) {
        if (providerMap.isEmpty()) {
            getAllDataProviders();
        }

        Iterator<IFile> iterator = providerMap.keySet().iterator();
        while (iterator.hasNext()) {
            IFile next = iterator.next();
            TypedReturnCode<Connection> typedReturnCode = providerMap.get(next);
            // tried to compare ids instead of instances but it gives another
            // exception later...
            // if (ResourceHelper.areSame(provider,
            // typedReturnCode.getObject())) {
            if (ResourceHelper.areSame(element, typedReturnCode.getObject())) {
                return next;
            }
        }
        return null;
    }

    /**
     * DOC rli Comment method "readFromFile".
     * 
     * @param file
     * @return
     */
    private TypedReturnCode<Connection> readFromFile(IFile file) {
        TypedReturnCode<Connection> rc;
        this.remove(file);
        rc = new TypedReturnCode<Connection>();
        Resource resource = getFileResource(file);

        // MOD scorreia 2009-01-09 password decryption is handled elsewhere
        // PasswordHelper.decryptResource(resource);

        Iterator<IFile> fileIterator = providerMap.keySet().iterator();
        while (fileIterator.hasNext()) {
            IFile key = fileIterator.next();
            TypedReturnCode<Connection> returnValue = providerMap.get(key);
            Resource resourceObj = returnValue.getObject().eResource();
            if (resourceObj == resource) {
                registedResourceMap.remove(key);
                providerMap.remove(key);
                break;
            }
        }
        retireTdProvider(file, rc, resource);
        return rc;
    }

    /**
     * DOC rli Comment method "findTdProvider".
     * 
     * @param file
     * @param rc
     * @param resource
     */
    private void retireTdProvider(IFile file, TypedReturnCode<Connection> rc, Resource resource) {
        Collection<Connection> tdDataProviders = ConnectionHelper.getTdDataProviders(resource.getContents());
        if (tdDataProviders.isEmpty()) {
            rc.setReturnCode(Messages.getString(
                    "PrvResourceFileHelper.NoDataProviderFound", file.getLocation().toFile().getAbsolutePath()), false); //$NON-NLS-1$
        }
        if (tdDataProviders.size() > 1) {
            rc.setReturnCode(Messages.getString("PrvResourceFileHelper.FoundTooManyDataProvider", tdDataProviders.size(), //$NON-NLS-1$
                    file.getLocation().toFile().getAbsolutePath()), false);
        }
        Connection prov = tdDataProviders.iterator().next();
        rc.setObject(prov);
        providerMap.put(file, rc);
    }

    public void remove(IFile file) {
        super.remove(file);
        this.providerMap.remove(file);
    }

    @Override
    public void clear() {
        super.clear();
        providerMap.clear();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.helper.resourcehelper.ResourceFileMap#deleteRelated(org.eclipse.core.resources.IFile)
     */
    @Override
    protected void deleteRelated(IFile file) {
        Connection dataProvider = findProvider(file).getObject();

        TdSoftwareSystem softwareSystem = DataProviderHelper.getSoftwareSystem(dataProvider);
        EMFSharedResources.getInstance().getSoftwareDeploymentResource().getContents().remove(softwareSystem);
        EMFSharedResources.getInstance().saveSoftwareDeploymentResource();

        // remove the alias from SQL Plugin
        CWMPlugin.getDefault().removeAliasInSQLExplorer(dataProvider);
    }

    public ReturnCode save(Connection dataProvider) {
        DataProviderWriter writer = ElementWriterFactory.getInstance().createDataProviderWriter();
        // ReturnCode returnCode = DqRepositoryViewService.saveOpenDataProvider(dataProvider, false);
        ReturnCode rc = writer.save(dataProvider);
        return rc;
    }

    public List<Connection> getAllDataProviders(IFolder folder) {
        List<IFile> allPRVFiles = new ArrayList<IFile>();
        searchAllDataProvider(folder, allPRVFiles);

        List<Connection> allDataProviders = new ArrayList<Connection>();
        if (!allPRVFiles.isEmpty()) {
            for (IFile file : allPRVFiles) {
                TypedReturnCode<Connection> rc = readFromFile(file);
                if (rc.isOk()) {
                    Connection dataProvider = rc.getObject();
                    allDataProviders.add(dataProvider);
                }
            }
        }

        return allDataProviders;
    }

    public List<Connection> getAllDataProviders() {
        return getAllDataProviders(ResourceManager.getMetadataFolder());
    }

    private List<IFile> searchAllDataProvider(IFolder folder, List<IFile> allPRVFiles) {

        try {
            for (IResource resource : folder.members()) {
                if (resource.getType() == IResource.FOLDER) {
                    searchAllDataProvider(folder.getFolder(resource.getName()), allPRVFiles);
                    continue;
                }
                IFile file = (IFile) resource;
                if (checkFile(file)) {
                    allPRVFiles.add(file);
                }
            }
        } catch (CoreException e) {
            log.error(e, e);
        }

        return allPRVFiles;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.helper.resourcehelper.ResourceFileMap#checkFile(org.eclipse.core.resources.IFile)
     */
    @Override
    protected boolean checkFile(IFile file) {
        String fileExtension = file.getFileExtension();
        return file != null && (FactoriesUtil.isProvFile(fileExtension) || FactoriesUtil.isItemFile(fileExtension));
    }
}
