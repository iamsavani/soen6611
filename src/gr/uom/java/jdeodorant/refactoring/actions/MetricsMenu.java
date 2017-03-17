package gr.uom.java.jdeodorant.refactoring.actions;

import java.lang.reflect.InvocationTargetException;


import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

import gr.uom.java.ast.ASTReader;
import gr.uom.java.ast.CompilationErrorDetectedException;
import gr.uom.java.ast.CompilationUnitCache;
import gr.uom.java.ast.SystemObject;
import gr.uom.java.jdeodorant.metrics.reusability.ReusabilityQMOOD;
import gr.uom.java.jdeodorant.metrics.reusability.UnderstandabilityCalculator;
import gr.uom.java.jdeodorant.refactoring.views.ElementChangedListener;


public class MetricsMenu implements IWorkbenchWindowActionDelegate  {
	private IWorkbenchPart part;
	private ISelection selection;
	private IJavaProject selectedProject;
	
	private IWorkbenchWindow window;
	
	public void run(final IAction action) {
		
		try {
			CompilationUnitCache.getInstance().clearCache();
			if(selection instanceof IStructuredSelection) {
				IStructuredSelection structuredSelection = (IStructuredSelection)selection;
				Object element = structuredSelection.getFirstElement();
				if(element instanceof IJavaProject) {
					selectedProject = (IJavaProject)element;

				}
				else if(element instanceof IPackageFragmentRoot) {
					IPackageFragmentRoot packageFragmentRoot = (IPackageFragmentRoot)element;
					selectedProject = packageFragmentRoot.getJavaProject();
		
				}
				else if(element instanceof IPackageFragment) {
					IPackageFragment packageFragment = (IPackageFragment)element;
					selectedProject = packageFragment.getJavaProject();
			
				}
				else if(element instanceof ICompilationUnit) {
					ICompilationUnit compilationUnit = (ICompilationUnit)element;
					selectedProject = compilationUnit.getJavaProject();
			
				}
				else if(element instanceof IType) {
					IType type = (IType)element;
					selectedProject = type.getJavaProject();
				
				}
				else if(element instanceof IMethod) {
					IMethod method = (IMethod)element;
					selectedProject = method.getJavaProject();

				}
				IWorkbench wb = PlatformUI.getWorkbench();
				IProgressService ps = wb.getProgressService();
				ps.busyCursorWhile(new IRunnableWithProgress() {
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
						if(ASTReader.getSystemObject() != null && selectedProject.equals(ASTReader.getExaminedProject())) {
							try {
								new ASTReader(selectedProject, ASTReader.getSystemObject(), monitor);
							} catch (CompilationErrorDetectedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						else {
							try {
								new ASTReader(selectedProject, monitor);
							} catch (CompilationErrorDetectedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						SystemObject system = ASTReader.getSystemObject();
		
						
						if(action.getId().equals("gr.uom.java.jdeodorant.actions.ReusabilityV1")) {
							System.out.println("v1:" + ReusabilityQMOOD.calculate(system));;
						}
						else if(action.getId().equals("gr.uom.java.jdeodorant.actions.ReusabilityV2")) {
							System.out.println("v2:" + UnderstandabilityCalculator.calculate(system));;
						}
						
					}
				});
			}
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		
		
		
		
	}

	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
		
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		
		this.part = targetPart;
		JavaCore.addElementChangedListener(ElementChangedListener.getInstance());
	}

	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	public void init(IWorkbenchWindow arg0) {
		// TODO Auto-generated method stub
		this.window = window;
	}

}
