package com.tairanchina.csp.dew.core.jdbc.dialect;

class PostgresDialect implements Dialect {

    @Override
    public String paging(String sql, long pageNumber, int pageSize) {
        return sql + " LIMIT " + pageSize + " OFFSET " + (pageNumber - 1) * pageSize;
    }

    @Override
    public String count(String sql) {
        return "SELECT COUNT(1) FROM ( " + sql + " ) _" + System.currentTimeMillis();
    }

    @Override
    public String getTableInfo(String tableName) {
        return "SELECT * FROM pg_tables t WHERE t.tablename='" + tableName + "'";
    }

    @Override
    public String getDriver() {
        return "org.postgresql.Driver";
    }

    @Override
    public DialectType getDialectType() {
        return DialectType.POSTGRE;
    }
}