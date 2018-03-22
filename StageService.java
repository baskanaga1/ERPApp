package com.guruinfo.scm.common.service;
import com.guruinfo.scm.mr.model.TreeModel;
import java.util.Collection;
/**
 * Created by Kannan G on 7/20/2017.
 */
public class StageService {
    String id = null;
    String value = null;
    String currentDate = null;
    private Collection<TreeModel> IOWSELECTVALUE;
    public String getid() {
        return id;
    }
    public void setid(String id) {
        this.id = id;
    }
    public String getvalue() {
        return value;
    }
    public void setvalue(String value) {
        this.value = value;
    }
    public String getcurrentDate() {
        return currentDate;
    }
    public void setcurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }
    public Collection<TreeModel> getIOWSELECTVALUE() {
        return IOWSELECTVALUE;
    }
    public void setIOWSELECTVALUE(Collection<TreeModel> IOWSELECTVALUE) {
        this.IOWSELECTVALUE = IOWSELECTVALUE;
    }
}
