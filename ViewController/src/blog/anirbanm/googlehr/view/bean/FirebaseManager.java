package blog.anirbanm.googlehr.view.bean;

import blog.anirbanm.googlehr.view.ADFUtils;
import blog.anirbanm.googlehr.view.JSFUtils;
import blog.anirbanm.googlehr.viewmodel.data.Department;
import blog.anirbanm.googlehr.viewmodel.data.Employee;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import javax.faces.event.ActionEvent;

import oracle.adf.view.rich.component.rich.RichPopup;
import oracle.adf.view.rich.event.DialogEvent;
import oracle.adf.view.rich.event.PopupFetchEvent;

import oracle.binding.OperationBinding;

import oracle.jbo.Row;

import org.apache.myfaces.trinidad.event.SelectionEvent;
import org.apache.myfaces.trinidad.model.UploadedFile;

public class FirebaseManager implements Serializable {
    
    @SuppressWarnings("compatibility:3508941060135145219")
    private static final long serialVersionUID = 383790161856066750L;
    
    private Department department;
    private Employee employee;
    private String profile;

    public FirebaseManager() {
    }

    public void authFirebase() {
        ADFUtils.findOperation("initFirebase").execute();
        ADFUtils.findOperation("generateAccessToken").execute();
    }

    public void initDepartments() {
        ADFUtils.findOperation("initDepartments").execute();
        setProfile((String) ADFUtils.findOperation("getProfileImage").execute());
    }
    
    public void onDepartmentSelection(final SelectionEvent selectionEvent) {
        final String _SELECTION = "#{bindings.DepartmentsPVO1.collectionModel.makeCurrent}";
        JSFUtils.invokeMethodExpression(_SELECTION, Object.class, SelectionEvent.class, selectionEvent);
        ADFUtils.findOperation("onDepartmentSelection").execute();
        setProfile((String) ADFUtils.findOperation("getProfileImage").execute());
    }
    
    public void onDepartmentDelete(final ActionEvent actionEvent) {
        ADFUtils.findOperation("deleteDepartment").execute();
        setProfile((String) ADFUtils.findOperation("getProfileImage").execute());
    }
    
    public void onEmployeeSelection(final SelectionEvent selectionEvent) {
        final String _SELECTION = "#{bindings.EmployeesPVO1.collectionModel.makeCurrent}";
        JSFUtils.invokeMethodExpression(_SELECTION, Object.class, SelectionEvent.class, selectionEvent);
        setProfile((String) ADFUtils.findOperation("getProfileImage").execute());
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
        setProfile((String) ADFUtils.findOperation("getProfileImage").execute());
    }

    public void onSaveEmployee(DialogEvent dialogEvent) {
        ADFUtils.findOperation("addEmployee").execute();
        setProfile((String) ADFUtils.findOperation("getProfileImage").execute());
    }
    
    public void onEmployeeDelete(final ActionEvent actionEvent) {
        ADFUtils.findOperation("deleteEmployee").execute();
        setProfile((String) ADFUtils.findOperation("getProfileImage").execute());
    }

    public void upload(DialogEvent dialogEvent) {
        OperationBinding uploadProfileImage = ADFUtils.findOperation("uploadProfileImage");
        UploadedFile file = (UploadedFile) getBean().getImagePath().getValue();
        try {
            InputStream imageIs = file.getInputStream();
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[16384];
            while ((nRead = imageIs.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            uploadProfileImage.getParamsMap().put("data", buffer.toByteArray());
            uploadProfileImage.execute();
            setProfile((String) ADFUtils.findOperation("getProfileImage").execute());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            getBean().getImagePath().resetValue();
        }
        
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

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getProfile() {
        return profile;
    }

    private ComponentManager getBean() {
        return (ComponentManager) JSFUtils.resolveExpression("#{backingBeanScope.componentBean}");
    }
}
