package blog.anirbanm.googlehr.model.am;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;

public class InitCapNamingStrategy extends PropertyNamingStrategy {
    
    @SuppressWarnings("compatibility:6567059840713312140")
    private static final long serialVersionUID = -4592275352149232894L;

    public InitCapNamingStrategy() {
        super();
    }
    
    @Override
    public String nameForGetterMethod(MapperConfig<?> mapperConfig, AnnotatedMethod annotatedMethod, String string) {
        if (string == null || string.isEmpty()) {
            return string;
        }
        int len = string.length();
        string = string.substring(0, 1).toUpperCase() + string.substring(1, len);
        return string;
    }
}
