package org.zeros.farm_manager_server.DAO.Interface.DataReaders;

import org.zeros.farm_manager_server.entities.fields.Field;

import java.math.BigDecimal;

public interface FieldDataReader {

    BigDecimal getIncomeTotal(Field field);
    BigDecimal getTotalExpenses(Field field);
    BigDecimal getIncomePerAreaUnit(Field field);
    BigDecimal getExpensesPerAreaUnit(Field field);
}
