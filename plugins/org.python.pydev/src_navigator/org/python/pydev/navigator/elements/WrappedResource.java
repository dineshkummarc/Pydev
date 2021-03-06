/**
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license.txt included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package org.python.pydev.navigator.elements;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.mapping.ResourceMapping;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.ui.actions.IToggleBreakpointsTarget;
import org.eclipse.debug.ui.actions.IWatchExpressionFactoryAdapter2;
import org.eclipse.search.ui.ISearchPageScoreComputer;
import org.eclipse.ui.IContributorResourceAdapter;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.model.IWorkbenchAdapter2;
import org.eclipse.ui.progress.IDeferredWorkbenchAdapter;
import org.eclipse.ui.views.tasklist.ITaskListResourceAdapter;
import org.python.pydev.core.FullRepIterable;
import org.python.pydev.core.log.Log;
import org.python.pydev.core.structure.FastStringBuffer;


/**
 * This class represents a resource that is wrapped for the python model.
 * 
 * @author Fabio
 *
 * @param <X>
 */
public class WrappedResource<X extends IResource> implements IWrappedResource, IContributorResourceAdapter, IAdaptable{

    protected IWrappedResource parentElement;
    protected X actualObject;
    protected PythonSourceFolder pythonSourceFolder;
    protected int rank;

    public WrappedResource(IWrappedResource parentElement, X actualObject, PythonSourceFolder pythonSourceFolder, int rank) {
        this.parentElement = parentElement;
        this.actualObject = actualObject;
        this.pythonSourceFolder = pythonSourceFolder;
        this.pythonSourceFolder.addChild(this);
        this.rank = rank;
    }
    
    public X getActualObject() {
        return actualObject;
    }

    public IWrappedResource getParentElement() {
        return parentElement;
    }

    public PythonSourceFolder getSourceFolder() {
        return pythonSourceFolder;
    }
    
    public int getRank() {
        return rank;
    }

    public IResource getAdaptedResource(IAdaptable adaptable) {
        return (IResource) getActualObject();
    }

    public boolean equals(Object other) {
        if(other instanceof IWrappedResource){
            if(other == this){
                return true;
            }
            IWrappedResource w = (IWrappedResource) other;
            return this.actualObject.equals(w.getActualObject());
        }
        return false;
        
//now returns always false because it could generate null things in the search page... the reason is that when the
//decorator manager had an update and passed in the search page, it thought that a file/folder was the python file/folder,
//and then, later when it tried to update it with that info, it ended up removing the element because it didn't know how
//to handle it.
//
// -- and this was also not a correct equals, because other.equals(this) would not return true as this was returning
// (basically we can't compare apples to oranges)
//        return actualObject.equals(other);
    }

    @Override
    public int hashCode() {
        return this.getActualObject().hashCode();
    }
    
    public Object getAdapter(Class adapter) {
        if(adapter == IContributorResourceAdapter.class){
            return this;
        }
        return this.getAdapterFromActualObject((IResource)this.getActualObject(), adapter);
    }


    public String toString() {
        FastStringBuffer buf = new FastStringBuffer();
        buf.append(FullRepIterable.getLastPart(super.toString())); //something as org.eclipse.ui.internal.WorkingSet@2813 will become WorkingSet@2813
        buf.append(" (");
        buf.append(this.getActualObject().toString());
        buf.append(")");
        return buf.toString();
    }
    
    public static Set<Class> logged = new HashSet<Class>();
    private static Object lock = new Object();

    public static Object getAdapterFromActualObject(IResource actualObject2, Class adapter) {
        if(     
                IProject.class.equals(adapter) || 
                IResource.class.equals(adapter) ||
                IFolder.class.equals(adapter) ||
                IContainer.class.equals(adapter) ||
                IFile.class.equals(adapter) ||
                ResourceMapping.class.equals(adapter) ||
                IFileStore.class.equals(adapter) ||
                
                //Added in 3.6
                ISearchPageScoreComputer.class.equals(adapter)||
                IToggleBreakpointsTarget.class.equals(adapter)||
                ITaskListResourceAdapter.class.equals(adapter) ||
                IFileInfo.class.equals(adapter)
                ){
            return actualObject2.getAdapter(adapter);
        }
        
        try {
            if(IWatchExpressionFactoryAdapter2.class.equals(adapter)){
                return actualObject2.getAdapter(adapter);
            }
        } catch (Throwable e) {
            //Ignore (not available in eclipse 3.2)
        }
        
        if(
                IDeferredWorkbenchAdapter.class.equals(adapter)||
                IWorkbenchAdapter2.class.equals(adapter)||
                IWorkbenchAdapter.class.equals(adapter)
                ){
            return null;
        }
        synchronized (lock) {
            if(!logged.contains(adapter)){
                logged.add(adapter);
                //Only log once per session.
                Log.logInfo("Did not expect adapter request: "+adapter);
            }
        }
        return null;
    }
}
