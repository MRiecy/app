package com.jianjia.medicinevendingmachine.dataStore.localrepository;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class SystemRebootTimes {
    @PrimaryKey
    public int id;
    private int systemRebootTimes;

    public SystemRebootTimes(int id, int systemRebootTimes) {
        this.id = id;
        this.systemRebootTimes = systemRebootTimes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSystemRebootTimes() {
        return systemRebootTimes;
    }

    public void setSystemRebootTimes(int systemRebootTimes) {
        this.systemRebootTimes = systemRebootTimes;
    }
}
