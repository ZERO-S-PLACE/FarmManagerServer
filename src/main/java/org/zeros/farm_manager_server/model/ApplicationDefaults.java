package org.zeros.farm_manager_server.model;

import org.zeros.farm_manager_server.config.LoggedUserConfiguration;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public class ApplicationDefaults {
    public static final int pageSize=20;
    public static final LocalDate UNDEFINED_DATE_MIN=LocalDate.of(1800,1,1);
    public static final LocalDate UNDEFINED_DATE_MAX=LocalDate.of(2300,1,1);


}
