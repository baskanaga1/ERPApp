package com.guruinfo.scm.common.service;
import com.guruinfo.scm.common.model.SCMLoadValueModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
/**
 * Created by Kannan G on 7/20/2017.
 */
public class GridMasterDao {
    String currentDate = null;
    List<MasterChildDao> values = new ArrayList<MasterChildDao>();
    public List<MasterChildDao> getValues() {
        return values;
    }
    public void setValues(List<MasterChildDao> values) {
        this.values = values;
    }
    public String getcurrentDate() {
        return currentDate;
    }
    public void setcurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }
}
