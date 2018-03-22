package com.guruinfo.scm.Equipment.EquipSlotLip;
/**
 * Created by Kannan G on 12/20/2016.
 */
public class rowInfo {
    private String available;
    private String equipCount;
    private String title;
    private String equipUniq;
    private String isSelected;
    private Integer isInserted;
    private String slotEndTime;
    private String slotStartTime;


    private String comment;
    private String slotId;
    private String configId;
    private Boolean isFullSlot;
    private Boolean HolidayCheck;
    private int min;
    private int max;

    public String getAvailable() {
        return available;
    }
    public void setAvailable(String available) {
        this.available = available;
    }


    public String getEquipCount() {
        return equipCount;
    }
    public void setEquipCount(String equipCount) {
        this.equipCount = equipCount;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getEquipUniq() {
        return equipUniq;
    }
    public void setEquipUniq(String equipUniq) {
        this.equipUniq = equipUniq;
    }

    public String getSelected() {
        return isSelected;
    }
    public void setSelected(String isSelected) {
        this.isSelected = isSelected;
    }
    public Integer getInserted() {
        return isInserted;
    }
    public void setInserted(Integer isInserted) {
        this.isInserted = isInserted;
    }

    public String getslotEndTime() {
        return slotEndTime;
    }
    public void setslotEndTime(String slotEndTime) {
        this.slotEndTime = slotEndTime;
    }

    public String getslotStartTime() {
        return slotStartTime;
    }
    public void setslotStartTime(String slotStartTime) {
        this.slotStartTime = slotStartTime;
    }



    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public String getSlotId() {
        return slotId;
    }
    public void setSlotId(String slotId) {
        this.slotId = slotId;
    }
    public String getConfigId() {
        return configId;
    }
    public void setCongigId(String configId) {
        this.configId = configId;
    }
    public Boolean getIsFullSlot() {
        return isFullSlot;
    }
    public void setIsFullSlot(Boolean isFullSlot) {
        this.isFullSlot = isFullSlot;
    }
    public Integer getMin() {
        return min;
    }
    public void setMin(int min) {
        this.min = min;
    }
    public Integer getMax() {
        return max;
    }
    public void setMax(int max) {
        this.max = max;
    }
    public Boolean getIsHoliday() {
        return HolidayCheck;
    }
    public void setIsHoliday(Boolean HolidayCheck) {
        this.HolidayCheck = HolidayCheck;
    }
}
