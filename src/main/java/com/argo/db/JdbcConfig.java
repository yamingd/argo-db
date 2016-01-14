package com.argo.db;

import com.argo.yaml.YamlTemplate;

import java.io.IOException;

/**
 * User: yamingdeng
 * Date: 13-11-25
 * Time: 下午9:54
 */
public class JdbcConfig {

    private static final String confName = "jdbc.yaml";

    public static JdbcConfig instance = null;

    private Integer idleConnectionTestPeriodInMinutes;
    private Integer idleMaxAgeInMinutes = 60;
    private Integer maxConnectionsPerPartition = 5;
    private Integer minConnectionsPerPartition = 5;
    private Integer partitionCount = 3;
    private Integer acquireIncrement = 2;
    private Integer statementsCacheSize = 100;
    private Integer releaseHelperThreads = 3;
    private Integer queryExecuteTimeLimitInMs = 3000;
    private String initSQL = "select 1";
    private String connectionTestStatement = "select 1";
    private String poolStrategy = "CACHED";

    // url、username、password，maxActive

    private String url;
    private String username;
    private String password;
    private Integer maxActive;

    /**
     * 加载配置信息
     * @throws IOException
     */
    public synchronized static void load() throws IOException {
        if (instance != null){
            return;
        }

        JdbcConfig.instance = load(confName);
    }

    /**
     * 加载配置信息
     * @throws IOException
     */
    public static JdbcConfig load(String confName) throws IOException {
        JdbcConfig config = YamlTemplate.load(JdbcConfig.class, confName);
        return config;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getMaxActive() {
        if (null == maxActive){
            maxActive = 10;
        }
        return maxActive;
    }

    public void setMaxActive(Integer maxActive) {
        this.maxActive = maxActive;
    }



    public Integer getIdleConnectionTestPeriodInMinutes() {
        return idleConnectionTestPeriodInMinutes;
    }

    public void setIdleConnectionTestPeriodInMinutes(Integer idleConnectionTestPeriodInMinutes) {
        this.idleConnectionTestPeriodInMinutes = idleConnectionTestPeriodInMinutes;
    }

    public Integer getIdleMaxAgeInMinutes() {
        return idleMaxAgeInMinutes;
    }

    public void setIdleMaxAgeInMinutes(Integer idleMaxAgeInMinutes) {
        this.idleMaxAgeInMinutes = idleMaxAgeInMinutes;
    }

    public Integer getMaxConnectionsPerPartition() {
        return maxConnectionsPerPartition;
    }

    public void setMaxConnectionsPerPartition(Integer maxConnectionsPerPartition) {
        this.maxConnectionsPerPartition = maxConnectionsPerPartition;
    }

    public Integer getMinConnectionsPerPartition() {
        return minConnectionsPerPartition;
    }

    public void setMinConnectionsPerPartition(Integer minConnectionsPerPartition) {
        this.minConnectionsPerPartition = minConnectionsPerPartition;
    }

    public Integer getPartitionCount() {
        return partitionCount;
    }

    public void setPartitionCount(Integer partitionCount) {
        this.partitionCount = partitionCount;
    }

    public Integer getAcquireIncrement() {
        return acquireIncrement;
    }

    public void setAcquireIncrement(Integer acquireIncrement) {
        this.acquireIncrement = acquireIncrement;
    }

    public Integer getStatementsCacheSize() {
        return statementsCacheSize;
    }

    public void setStatementsCacheSize(Integer statementsCacheSize) {
        this.statementsCacheSize = statementsCacheSize;
    }

    public Integer getReleaseHelperThreads() {
        return releaseHelperThreads;
    }

    public void setReleaseHelperThreads(Integer releaseHelperThreads) {
        this.releaseHelperThreads = releaseHelperThreads;
    }

    public Integer getQueryExecuteTimeLimitInMs() {
        return queryExecuteTimeLimitInMs;
    }

    public void setQueryExecuteTimeLimitInMs(Integer queryExecuteTimeLimitInMs) {
        this.queryExecuteTimeLimitInMs = queryExecuteTimeLimitInMs;
    }

    public String getInitSQL() {
        return initSQL;
    }

    public void setInitSQL(String initSQL) {
        this.initSQL = initSQL;
    }

    public String getConnectionTestStatement() {
        return connectionTestStatement;
    }

    public void setConnectionTestStatement(String connectionTestStatement) {
        this.connectionTestStatement = connectionTestStatement;
    }

    public String getPoolStrategy() {
        return poolStrategy;
    }

    public void setPoolStrategy(String poolStrategy) {
        this.poolStrategy = poolStrategy;
    }
}
