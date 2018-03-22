package com.guruinfo.scm.common.service;
import java.util.Collection;
/**
 * Created by ERP on 7/20/2017.
 */
public class MainStageService {
    String currentDate = null;
    private Collection<StageService> SatgeLoadBasedOnProjectList;
    public String getcurrentDate() {
        return currentDate;
    }
    public void setcurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }
    public Collection<StageService> getSatgeLoadBasedOnProjectList() {
        return SatgeLoadBasedOnProjectList;
    }
    public void setSatgeLoadBasedOnProjectList(Collection<StageService> SatgeLoadBasedOnProjectList) {
        this.SatgeLoadBasedOnProjectList = SatgeLoadBasedOnProjectList;
    }
}
