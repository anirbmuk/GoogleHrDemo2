package blog.anirbanm.googlehr.model.vo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import oracle.jbo.Key;
import oracle.jbo.RangePagingDataFilter;
import oracle.jbo.ScrollableDataFilter;
import oracle.jbo.server.ProgrammaticViewObjectImpl;

public class BaseProgrammaticViewObjectImpl extends ProgrammaticViewObjectImpl {
    
    private HashMap<String, Object> data;
    
    public BaseProgrammaticViewObjectImpl() {
        super();
    }

    /**
     * getScrollableData - for custom java data source support.
     */
    public Collection<Object> getScrollableData(ScrollableDataFilter filter) {
        ArrayList<Object> rows = new ArrayList<Object>();
        final TreeMap<String, Object> collection = new TreeMap<String, Object>(getData());
        if (collection != null) {
            for (Map.Entry row : collection.entrySet()) {
                rows.add(row.getValue());
            }
        }
        return rows;
    }

    /**
     * getRangePagingData - for custom java data source support.
     */
    public Collection<Object> getRangePagingData(RangePagingDataFilter filter) {
        Collection<Object> value = super.getRangePagingData(filter);
        return value;
    }

    /**
     * retrieveDataByKey - for custom java data source support.
     */
    public Collection<Object> retrieveDataByKey(Key key, int size) {
        Collection<Object> value = super.retrieveDataByKey(key, size);
        return value;
    }

    public void setData(HashMap<String, Object> data) {
        this.data = data;
    }

    public HashMap<String, Object> getData() {
        return data;
    }
}
