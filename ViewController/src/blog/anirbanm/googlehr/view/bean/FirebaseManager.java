package blog.anirbanm.googlehr.view.bean;

import blog.anirbanm.googlehr.view.ADFUtils;
import blog.anirbanm.googlehr.view.JSFUtils;
import blog.anirbanm.googlehr.viewmodel.data.Department;
import blog.anirbanm.googlehr.viewmodel.data.Employee;

import java.io.Serializable;

import oracle.adf.view.rich.component.rich.RichPopup;
import oracle.adf.view.rich.event.DialogEvent;
import oracle.adf.view.rich.event.PopupFetchEvent;

import oracle.jbo.Row;

import org.apache.myfaces.trinidad.event.SelectionEvent;

public class FirebaseManager implements Serializable {
    
    @SuppressWarnings("compatibility:3508941060135145219")
    private static final long serialVersionUID = 383790161856066750L;
    
    private Department department;
    private Employee employee;

    public FirebaseManager() {
    }

    public void authFirebase() {
        ADFUtils.findOperation("initFirebase").execute();
        ADFUtils.findOperation("generateAccessToken").execute();
    }

    public void initDepartments() {
        ADFUtils.findOperation("initDepartments").execute();
    }
    
    public void onDepartmentSelection(final SelectionEvent selectionEvent) {
        final String _SELECTION = "#{bindings.DepartmentsPVO1.collectionModel.makeCurrent}";
        JSFUtils.invokeMethodExpression(_SELECTION, Object.class, SelectionEvent.class, selectionEvent);
        ADFUtils.findOperation("onDepartmentSelection").execute();
    }

    public void onCreateDepartmentPopup(PopupFetchEvent popupFetchEvent) {
        final Department department = new Department();
        department.setCreatedBy(getUsername());
        setDepartment(department);
    }

    public void onCreateEmployeePopup(PopupFetchEvent popupFetchEvent) {
        final Row departmentRow = ADFUtils.findIterator("DepartmentsPVO1Iterator").getCurrentRow();
        RichPopup popup = (RichPopup) popupFetchEvent.getComponent();
        if (departmentRow == null) {
            popup.cancel();
        }
        final Integer departmentId = (Integer) departmentRow.getAttribute("DepartmentId");
        final Employee employee = new Employee();
        employee.setDepartmentId(departmentId);
        employee.setCreatedBy(getUsername());
        setEmployee(employee);
    }

    public void onSaveDepartment(DialogEvent dialogEvent) {
        ADFUtils.findOperation("addDepartment").execute();
    }

    public void onSaveEmployee(DialogEvent dialogEvent) {
        ADFUtils.findOperation("addEmployee").execute();
    }
    
    private String getUsername() {
        return (String) JSFUtils.resolveExpression("#{securityContext.userName}");
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Department getDepartment() {
        return department;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Employee getEmployee() {
        return employee;
    }
}
