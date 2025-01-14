package org.zeros.farm_manager_server.Model;

import java.time.LocalDate;
import java.util.UUID;

public class ApplicationDefaults {
    public static final int pageSize = 20;
    public static final LocalDate UNDEFINED_DATE_MIN = LocalDate.of(1800, 1, 1);
    public static final LocalDate UNDEFINED_DATE_MAX = LocalDate.of(2300, 1, 1);
    public static final UUID UUID_UNDEFINED = UUID.randomUUID();
}
