package blog.anirbanm.googlehr.viewmodel.data;

import java.io.Serializable;

public class Department implements Serializable {
    
    @SuppressWarnings("compatibility:7031917440539028308")
    private static final long serialVersionUID = 7237671591490833501L;
    
    private Integer departmentId;
    private String departmentName;
    private Integer managerId;
    private Integer locationId;
    private String createdBy;

    public Department() {
        super();
    }
    
    public Department(Integer departmentId, String departmentName, Integer managerId, Integer locationId) {
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.managerId = managerId;
        this.locationId = locationId;
    }
    
    @Override
    public String toString() {
        return "Department ["
            + departmentId + ", "
            + departmentName + ", "
            + managerId + ", "
            + locationId + "]";
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setManagerId(Integer managerId) {
        this.managerId = managerId;
    }

    public Integer getManagerId() {
        return managerId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedBy() {
        return createdBy;
    }
}
