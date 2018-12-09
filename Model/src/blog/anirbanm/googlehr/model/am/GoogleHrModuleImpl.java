package blog.anirbanm.googlehr.model.am;

import blog.anirbanm.googlehr.model.am.common.GoogleHrModule;
import blog.anirbanm.googlehr.model.vo.DepartmentsPVOImpl;
import blog.anirbanm.googlehr.model.vo.DepartmentsPVORowImpl;
import blog.anirbanm.googlehr.model.vo.EmployeesPVOImpl;
import blog.anirbanm.googlehr.viewmodel.data.Department;
import blog.anirbanm.googlehr.viewmodel.data.Employee;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.net.URLEncoder;

import java.util.Arrays;
import java.util.HashMap;

import javax.ws.rs.core.MediaType;

import oracle.jbo.Key;
import oracle.jbo.server.ApplicationModuleImpl;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
// ---------------------------------------------------------------------
// ---    File generated by Oracle ADF Business Components Design Time.
// ---    Sat Dec 08 11:59:15 IST 2018
// ---    Custom code may be added to this class.
// ---    Warning: Do not modify method signatures of generated methods.
// ---------------------------------------------------------------------
public class GoogleHrModuleImpl extends ApplicationModuleImpl implements GoogleHrModule {

    private String accessToken;

    /**
     * This is the default constructor (do not remove).
     */
    public GoogleHrModuleImpl() {
    }

    public void initFirebase() {
        try {
            FileInputStream serviceAccount =
                new FileInputStream("C:\\JDeveloper\\122130\\hrstore-test-firebase-adminsdk-v4li2-09770ed145.json");
            GoogleCredentials googleCred = GoogleCredentials.fromStream(serviceAccount);
            FirebaseOptions options = new FirebaseOptions.Builder().setCredentials(googleCred)
                                                                   .setDatabaseUrl("https://hrstore-test.firebaseio.com/")
                                                                   .build();
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options, "hrstore");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void generateAccessToken() {
        try {
            FileInputStream serviceAccount =
                new FileInputStream("C:\\JDeveloper\\122130\\hrstore-test-firebase-adminsdk-v4li2-09770ed145.json");
            GoogleCredential googleCred =
                GoogleCredential.fromStream(serviceAccount)
                .createScoped(Arrays.asList("https://www.googleapis.com/auth/firebase.database",
                                            "https://www.googleapis.com/auth/userinfo.email"));
            googleCred.refreshToken();
            setAccessToken(googleCred.getAccessToken());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initDepartments() {
        getDepartments(null);
    }

    public void onDepartmentSelection() {
        final DepartmentsPVOImpl departmentsView = getDepartmentsPVO1();
        final DepartmentsPVORowImpl department = (DepartmentsPVORowImpl) departmentsView.getCurrentRow();
        try {
            if (department != null) {
                refreshEmployees(department.getDepartmentId());
            } else {
                refreshEmployees(null);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addDepartment(final Department department) {
        String addDepartmentUri = getDepartmentUri(department.getDepartmentId());
        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(new InitCapNamingStrategy());
        String departmentString = null;
        try {
            departmentString = mapper.writeValueAsString(department);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        try {
            saveData(addDepartmentUri, departmentString);
            getDepartments(department.getDepartmentId());
        } catch (JsonMappingException | JsonParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void addEmployee(final Employee employee) {
        final Integer departmentId = employee.getDepartmentId();
        if (departmentId == null) {
            return;
        }
        String addEmployeeUri = getEmployeeUri(employee.getEmployeeId());
        employee.setDepartmentId(departmentId);
        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(new InitCapNamingStrategy());
        String employeeString = null;
        try {
            employeeString = mapper.writeValueAsString(employee);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        try {
            saveData(addEmployeeUri, employeeString);
            refreshEmployees(departmentId);
        } catch (JsonMappingException | JsonParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void deleteDepartment() {
        final DepartmentsPVOImpl departmentsView = getDepartmentsPVO1();
        final DepartmentsPVORowImpl department = (DepartmentsPVORowImpl) departmentsView.getCurrentRow();
        if (department != null) {
            String deleteDepartmentUri = getDepartmentUri(department.getDepartmentId());
            try {
                deleteData(deleteDepartmentUri);
                getDepartments(null);
            } catch (JsonMappingException | JsonParseException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void getDepartments(final Integer currentDepartmentId) {
        final DepartmentsPVOImpl departmentsView = getDepartmentsPVO1();
        DepartmentsPVORowImpl department = null;
        try {
            departmentsView.setData(getAllDepartments());
        } catch (IOException e) {
            departmentsView.setData(null);
        }
        departmentsView.executeQuery();
        Integer id = null;

        if (currentDepartmentId != null) {
            department =
                (DepartmentsPVORowImpl) departmentsView.findByKey(new Key(new Object[] { currentDepartmentId }), 1)[0];
            if (department != null) {
                departmentsView.setCurrentRow(department);
            }
        } else {
            department = (DepartmentsPVORowImpl) departmentsView.first();
        }
        id = department == null ? null : department.getDepartmentId();

        try {
            refreshEmployees(id);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private HashMap<String, Object> getAllDepartments() throws IOException {
        return getData(getAllDepartmentsUri());
    }

    private HashMap<String, Object> getEmployeesByDepartment(Integer departmentId) throws IOException {
        return getData(filterEmployeesByDepartmentUri(departmentId));
    }

    private void refreshEmployees(final Integer departmentId) throws IOException {
        final EmployeesPVOImpl employeesView = getEmployeesPVO1();
        employeesView.setData(departmentId == null ? new HashMap<String, Object>() :
                              getEmployeesByDepartment(departmentId));
        employeesView.executeQuery();
    }

    private FirebaseApp getFirebaseApp(final String ref) {
        return FirebaseApp.getInstance(ref);
    }

    private DatabaseReference getFirebaseDatabase(final String ref) {
        return FirebaseDatabase.getInstance(getFirebaseApp(ref)).getReference();
    }

    private String getFirebaseRootReference(final String ref) {
        return getFirebaseDatabase(ref).toString();
    }

    private String getAllDepartmentsUri() {
        return getFirebaseDatabase("hrstore").child("Departments") + ".json";
    }

    private String getDepartmentUri(final Integer departmentId) {
        return getFirebaseDatabase("hrstore").child("Departments").child(String.valueOf(departmentId)) + ".json";
    }

    private String getEmployeeUri(final Integer employeeId) {
        return getFirebaseDatabase("hrstore").child("Employees").child(String.valueOf(employeeId)) + ".json";
    }

    private String filterEmployeesByDepartmentUri(Integer departmentId) throws UnsupportedEncodingException {
        return getFirebaseDatabase("hrstore").child("Employees") + ".json?" +
               URLEncoder.encode("orderBy=\"DepartmentId\"&equalTo=" + departmentId, "UTF-8");
    }

    private HashMap<String, Object> getData(final String uri) throws IOException, JsonParseException,
                                                                     JsonMappingException {
        HashMap<String, Object> hashmap = new HashMap<String, Object>();
        if (getAccessToken() == null) {
            generateAccessToken();
        }
        Client client = Client.create();
        WebResource ws = client.resource(uri);
        ClientResponse response = ws.header("Authorization", "Bearer " + getAccessToken())
                                    .accept(MediaType.APPLICATION_JSON)
                                    .get(ClientResponse.class);
        int status = response.getStatus();
        if (status == 401) {
            generateAccessToken();
            response = ws.header("Authorization", "Bearer " + getAccessToken())
                         .accept(MediaType.APPLICATION_JSON)
                         .get(ClientResponse.class);
        }
        String value = response.getEntity(String.class);
        final ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(new InitCapNamingStrategy());
        hashmap = mapper.readValue(value, HashMap.class);
        return hashmap;
    }

    private void saveData(final String uri, final String data) throws IOException, JsonParseException,
                                                                      JsonMappingException {
        if (getAccessToken() == null) {
            generateAccessToken();
        }
        Client client = Client.create();
        WebResource ws = client.resource(uri);
        ClientResponse response = ws.header("Authorization", "Bearer " + getAccessToken())
                                    .type(MediaType.APPLICATION_JSON)
                                    .put(ClientResponse.class, data);
        int status = response.getStatus();
        if (status == 401) {
            generateAccessToken();
            response = ws.header("Authorization", "Bearer " + getAccessToken())
                         .type(MediaType.APPLICATION_JSON)
                         .put(ClientResponse.class, data);
        }
    }

    private void deleteData(final String uri) throws IOException, JsonParseException, JsonMappingException {
        if (getAccessToken() == null) {
            generateAccessToken();
        }
        Client client = Client.create();
        WebResource ws = client.resource(uri);
        ClientResponse response = ws.header("Authorization", "Bearer " + getAccessToken()).delete(ClientResponse.class);
        int status = response.getStatus();
        if (status == 401) {
            generateAccessToken();
            response = ws.header("Authorization", "Bearer " + getAccessToken()).delete(ClientResponse.class);
        }
    }

    @Override
    protected void activateState(Element element) {
        if (element != null) {
            NodeList nl = element.getElementsByTagName("GHMI_OBJECT");
            if (nl != null) {
                final Node node = nl.item(0);
                final NodeList nodeList = node.getChildNodes();
                Element elem = null;
                for (int i = 0; i < nodeList.getLength(); i++) {
                    elem = (Element) nodeList.item(0);
                    if (elem.getNodeName().equals("GHMI_ELEMENT")) {
                        setAccessToken(elem.getAttribute("accesstoken"));
                    }
                }
            }
        }
        super.activateState(element);
    }

    @Override
    protected void passivateState(Document document, Element element) {
        final Element elem = document.createElement("GHMI_ELEMENT");
        elem.setAttribute("accesstoken", getAccessToken());
        final Node parentNode = document.createElement("GHMI_OBJECT");
        parentNode.appendChild(elem);
        element.appendChild(parentNode);
        super.passivateState(document, element);
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    /**
     * Container's getter for DepartmentsPVO1.
     * @return DepartmentsPVO1
     */
    public DepartmentsPVOImpl getDepartmentsPVO1() {
        return (DepartmentsPVOImpl) findViewObject("DepartmentsPVO1");
    }

    /**
     * Container's getter for EmployeesPVO1.
     * @return EmployeesPVO1
     */
    public EmployeesPVOImpl getEmployeesPVO1() {
        return (EmployeesPVOImpl) findViewObject("EmployeesPVO1");
    }
}

