package com.argo.db.template;

import com.argo.db.MapperConfig;
import com.argo.db.Roles;
import com.argo.db.SqlMapper;
import com.argo.db.Values;
import com.argo.db.exception.EntityNotFoundException;
import com.argo.db.mysql.BeanNameUtil;
import com.argo.db.mysql.MySqlConfigList;
import com.argo.db.mysql.TableContext;
import com.argo.redis.RedisBuket;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.KeyHolder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.*;

/**
 *
 * 减少反射
 * 自定义 ResultSetExtractor
 *
 * 数据集处理
 *
 * org.springframework.jdbc.core.ResultSetExtractor
 *
 * org.springframework.jdbc.support.JdbcUtils#getResultSetValue(java.sql.ResultSet, int, java.lang.Class)
 *
 * 查询使用
 * org.springframework.jdbc.core.JdbcTemplate#query(java.lang.String, org.springframework.jdbc.core.ResultSetExtractor, java.lang.Object...)
 * org.springframework.jdbc.core.JdbcTemplate#queryForObject(java.lang.String, java.lang.Object[], org.springframework.jdbc.core.RowMapper)
 *
 * 更新、删除使用
 * org.springframework.jdbc.core.JdbcTemplate#update(java.lang.String, org.springframework.jdbc.core.PreparedStatementSetter)
 *
 * 新增
 * org.springframework.jdbc.core.JdbcTemplate#update(org.springframework.jdbc.core.PreparedStatementCreator, org.springframework.jdbc.support.KeyHolder)
 *
 * Created by yamingd on 9/15/15.
 */
public abstract class MySqlMapper<T, PK extends Comparable> implements InitializingBean, ApplicationContextAware, SqlMapper<T,PK> {

    public static final String S_COMMOA = ", ";
    public static final String S_QMARK = "?, ";
    public static final String S_OR = " OR ";
    public static final String S_AND = " AND ";
    public static final String S_E_Q = " = ? ";
    public static final String SELECT = "select ";
    public static final String FROM = " from ";
    public static final String ORDER_BY = " order by ";
    public static final String WHERE = " where ";
    public static final String LIMIT_OFFSET = " limit ?, ?";
    public static final String DELETE_FROM = "delete from ";
    public static final String UPDATE = "update ";
    public static final String SET = " set ";
    public static final String S_EMPTY = " ";
    public static final String STRING_NULL = "";
    public static final String DESC = " desc ";
    public static final String ASC = " asc ";
    public static final String GROUP_BY = " group by ";

    /**
     * for update、create、delete、in-transaction query
     */
    protected JdbcTemplate jdbcTemplateM = null;
    /**
     * for select
     */
    protected JdbcTemplate jdbcTemplateS = null;

    protected Map<String, String> cacheSql = new HashMap<String, String>();

    protected boolean cacheEnabled = false;

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired(required = false)
    protected RedisBuket redisBuket;

    protected ApplicationContext applicationContext;

    public MySqlMapper(){

    }

    @Override
    public void afterPropertiesSet() throws Exception{
        cacheEnabled = redisBuket != null && MySqlConfigList.all.isMemcache();
        initJdbcTemplate();
        prepare();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 初始化JdbcTemplate
     */
    protected void initJdbcTemplate(){
        if (MySqlConfigList.all.isMSEnabled()){

            String serverName = MapperConfig.getServer(this.getTableName());

            String beanName = BeanNameUtil.getJtBeanName(serverName, Roles.MASTER);
            this.jdbcTemplateM = applicationContext.getBean(beanName, JdbcTemplate.class);

            beanName = BeanNameUtil.getJtBeanName(serverName, Roles.SLAVE);
            this.jdbcTemplateS = applicationContext.getBean(beanName, JdbcTemplate.class);
        }
    }

    /**
     *
     * 设置PK 值
     *
     * @param item Item
     * @param holder KeyHolder
     */
    protected abstract void setPKValue(T item, KeyHolder holder);

    /**
     *
     * @param item Item
     * @return PK Primary Key
     */
    protected abstract PK getPkValue(T item);

    /**
     *
     * @return boolean
     */
    protected abstract boolean isPKAutoIncr();

    /**
     * 设置
     * @param rs ResultSet
     * @param rowIndex RowIndex
     * @return T Item
     */
    protected abstract T mapRow(ResultSet rs, int rowIndex) throws SQLException;

    /**
     * 设置
     * @param ps PreparedStatement
     * @param item Item
     */
    protected abstract void setInsertStatementValues(PreparedStatement ps, T item) throws SQLException;

    /**
     * 映射数据集到Java Bean
     * @param rs ResultSet
     * @param item Item
     * @param setterMethods setterMethods
     * @param fieldTypes fieldTypes
     * @param columnTypes columnTypes
     * @throws SQLException SQLException
     */
    protected void mapResultSetToBean(ResultSet rs, T item,
                                        Map<String, Method> setterMethods,
                                        Map<String, Class> fieldTypes, Map<String, Class> columnTypes) throws SQLException {

        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();

        for (int index = 1; index <= columnCount; index++) {
            String column = JdbcUtils.lookupColumnName(rsmd, index);
            Method method = setterMethods.get(column);
            if (null == method){
                logger.error("missing Method Setter. {}, {}, {}", this.getRowClass(), column, method);
            }else{
                Class fieldType = fieldTypes.get(column);
                Class colType = columnTypes.get(column);
                Object val = JdbcUtils.getResultSetValue(rs, index, colType);
                if (!colType.equals(fieldType)){
                    val = Values.get(val, fieldType);
                }
                try {
                    method.invoke(item, val);
                } catch (IllegalAccessException e) {
                    logger.error("Set Field Value Error: " + this.getRowClass() + ", " + method, e);
                } catch (InvocationTargetException e) {
                    logger.error("Set Field Value Error: " + this.getRowClass() + ", " + method, e);
                }
            }
        }

    }


    public JdbcTemplate getJdbcTemplateM() {
        return jdbcTemplateM;
    }

    public JdbcTemplate getJdbcTemplateS() {
        return jdbcTemplateS;
    }

    /**
     * 构造insert sql
     * @param context context
     * @return String
     */
    protected String prepareInsertSql(TableContext context){

        String tableName = getTableName(context);

        String sql = cacheSql.get(tableName);
        if (null != sql){
            return sql;
        }

        final StringBuilder s = new StringBuilder(128);
        s.append("insert into ").append(tableName);
        s.append("(");

        List<String> columnList = this.getColumnList();
        int size = columnList.size();
        if (isPKAutoIncr()){
            for (int i = 0; i < size; i++) {
                String str = columnList.get(i);
                if (str.equalsIgnoreCase(getPKColumnName())){
                    continue;
                }
                s.append(str).append(S_COMMOA);
            }
        }else{
            for (int i = 0; i < size; i++) {
                String str = columnList.get(i);
                s.append(str).append(S_COMMOA);
            }
        }

        s.setLength(s.length() - S_COMMOA.length());

        s.append(")values(");

        if (isPKAutoIncr()){
            size = size - 1;
        }
        for (int i = 0; i < size; i++) {
            s.append(S_QMARK);
        }
        s.setLength(s.length() - 2);

        s.append(")");

        sql = s.toString();
        cacheSql.put(tableName, sql);

        return sql;
    }

    @Override
    public T find(TableContext context, final PK id) throws EntityNotFoundException {
        if (null == id){
            return null;
        }

        String cacheKey = null;
        if (cacheEnabled){
            cacheKey = String.format("%s:%s", this.getTableName(), id);
            T item = redisBuket.get(getRowClass(), cacheKey);
            if (null != item){
                return item;
            }
        }

        T t = findInDb(context, this.jdbcTemplateS, id);

        if (null != t){
            if (cacheEnabled){
                redisBuket.set(cacheKey, t);
            }
        }

        return t;
    }

    public abstract PK[] toPKArrays(String pkWithCommas);

    @Override
    public List<T> findRows(TableContext context, String pkWithCommas, boolean ascending) throws DataAccessException {
        return this.selectRows(context, toPKArrays(pkWithCommas), ascending);
    }

    @Override
    public T findInMaster(TableContext context, final PK id) throws EntityNotFoundException {
        return findInDb(context, this.jdbcTemplateM, id);
    }
    /**
     * 按主键读取
     * @param context context
     * @param jdbcTemplate jdbcTemplate
     * @param id id
     * @return T item
     * @throws EntityNotFoundException EntityNotFoundException
     */
    protected T findInDb(TableContext context, JdbcTemplate jdbcTemplate, final PK id) throws EntityNotFoundException, DataAccessException{

        final StringBuilder s = new StringBuilder(128);
        s.append(SELECT).append(getSelectedColumns()).append(FROM).append(getTableName(context))
                .append(WHERE);

        s.append(getPKColumnName()).append(S_E_Q);

        List<T> list = jdbcTemplate.query(s.toString(), new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setObject(1, id);
            }
        }, new ResultSetExtractor<List<T>>() {
            @Override
            public List<T> extractData(ResultSet rs) throws SQLException, DataAccessException {
                List<T> tmp = new ArrayList<T>();

                int rowNum = 0;
                while (rs.next()) {
                    tmp.add(mapRow(rs, rowNum++));
                }

                return tmp;
            }

        });

        if (list.size() == 0){
            throw new EntityNotFoundException(this.getTableName(), id);
        }

        return list.get(0);

    }

    @Override
    public boolean insert(TableContext context, final T item) throws DataAccessException{
        Preconditions.checkNotNull(item);

        final String sql = prepareInsertSql(context);
        if (logger.isDebugEnabled()){
            logger.debug("SQL Statement: {}", sql);
        }
        if (isPKAutoIncr()) {

            KeyHolder keyHolder = new GeneratedKeyHolder();
            int ret = jdbcTemplateM.update(new PreparedStatementCreator() {

                @Override
                public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {

                    PreparedStatement ps = connection.prepareStatement(sql,
                            Statement.RETURN_GENERATED_KEYS);

                    setInsertStatementValues(ps, item);

                    return ps;

                }
            }, keyHolder);

            setPKValue(item, keyHolder);
            return ret > 0;

        }else{

            int ret = jdbcTemplateM.update(new PreparedStatementCreator() {

                @Override
                public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {

                    PreparedStatement ps = connection.prepareStatement(sql);

                    setInsertStatementValues(ps, item);

                    return ps;

                }
            });

            return ret > 0;

        }

    }

    @Override
    public boolean insertBatch(TableContext context, final List<T> list) throws DataAccessException{
        Preconditions.checkNotNull(list);
        if (list.size() == 0){
            return true;
        }

        final String sql = prepareInsertSql(context);

        int[] ret = jdbcTemplateM.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                T item = list.get(i);
                setInsertStatementValues(ps, item);
            }

            @Override
            public int getBatchSize() {
                return list.size();
            }
        });

        return ret.length == list.size();
    }

    @Override
    public boolean expire(PK id){
        if (!cacheEnabled){
            return true;
        }
        String cacheKey = null;
        cacheKey = String.format("%s:%s", this.getTableName(), id);
        return redisBuket.delete(cacheKey);
    }

    @Override
    public void expire(List<PK> ids) {
        if (!cacheEnabled){
            return;
        }
        String[] cacheKeys = new String[ids.size()];
        for (int i = 0; i < ids.size(); i++) {
            cacheKeys[i] = String.format("%s:%s", this.getTableName(), ids.get(i));
        }
        redisBuket.delete(cacheKeys);
    }

    @Override
    public void expire(PK[] ids) {
        if (!cacheEnabled){
            return;
        }
        String[] cacheKeys = new String[ids.length];
        for (int i = 0; i < ids.length; i++) {
            cacheKeys[i] = String.format("%s:%s", this.getTableName(), ids[i]);
        }
        redisBuket.delete(cacheKeys);
    }

    @Override
    public boolean update(TableContext context, T item) throws DataAccessException{
        Preconditions.checkNotNull(item);
        return false;
    }

    /**
     * 更新后
     * @param context context
     * @param item item
     */
    protected void afterUpdate(TableContext context, T item){
        this.expire(getPkValue(item));
    }

    @Override
    public boolean update(String sql, final List<Object> args){

        int ret = this.jdbcTemplateM.update(sql, new PreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                for (int i = 0; i < args.size(); i++) {
                    ps.setObject(i + 1, args.get(i));
                }
            }
        });

        return ret > 0;
    }

    @Override
    public boolean update(TableContext context, String values, String where, final Object... args) throws DataAccessException{
        Preconditions.checkNotNull(values);
        Preconditions.checkNotNull(where);
        Preconditions.checkNotNull(args);

        final StringBuilder s = new StringBuilder(128);
        s.append(UPDATE).append(getTableName(context)).append(SET).append(values).append(WHERE).append(where);

        int ret = this.jdbcTemplateM.update(s.toString(), new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                for (int i = 0; i < args.length; i++) {
                    ps.setObject(i + 1, args[i]);
                }
            }
        });

        return ret > 0;

    }

    @Override
    public boolean delete(TableContext context, T item) throws DataAccessException{
        return deleteBy(context, getPkValue(item));
    }

    @Override
    public boolean deleteBy(TableContext context, final PK id) throws DataAccessException{
        Preconditions.checkNotNull(id);

        final StringBuilder s = new StringBuilder(128);
        s.append(DELETE_FROM).append(getTableName(context))
                .append(WHERE).append(getPKColumnName()).append(S_E_Q);

        int ret = this.jdbcTemplateM.update(s.toString(), new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setObject(1, id);
            }
        });

        this.expire(id);

        return ret > 0;
    }

    @Override
    public boolean deleteBy(TableContext context, String where, final Object... args) throws DataAccessException{
        Preconditions.checkNotNull(where);
        Preconditions.checkNotNull(args);

        final StringBuilder s = new StringBuilder(128);
        s.append(DELETE_FROM).append(getTableName(context))
                .append(WHERE).append(where);

        int ret = this.jdbcTemplateM.update(s.toString(), new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                for (int i = 0; i < args.length; i++) {
                    ps.setObject(i + 1, args[i]);
                }
            }
        });

        return ret > 0;
    }

    @Override
    public List<T> selectRows(TableContext context, final PK[] args, final boolean ascending) throws DataAccessException{
        Preconditions.checkNotNull(args);
        if (args.length == 0){
            return Collections.emptyList();
        }

        if (cacheEnabled){

            Arrays.sort(args); // ascending sort

            List<String> keys = Lists.newArrayList();
            List<PK> missids = new ArrayList<PK>();
            if (ascending){
                for (int i = 0; i < args.length; i++) {
                    if (missids.contains(args[i])){
                        continue;
                    }
                    missids.add(args[i]);
                    keys.add(String.format("%s:%s", getTableName(), args[i]));
                }
            }else{
                for (int i = args.length-1; i >= 0; i--) {
                    if (missids.contains(args[i])){
                        continue;
                    }
                    missids.add(args[i]);
                    keys.add(String.format("%s:%s", getTableName(), args[i]));
                }
            }


            List<T> resultList = redisBuket.mget(getRowClass(), keys.toArray(new String[0]));
            if (resultList.size() > 0){
                for (int i = 0; i < resultList.size(); i++) {
                    T item = resultList.get(i);
                    if (null != item) {
                        missids.remove(getPkValue(item));
                        keys.set(i, null);
                    }
                }
            }
            if (missids.size() == 0){
                return resultList;
            }

            List<T> dblist = this.selectRowsInDb(context, missids, ascending);
            for (int i = 0, j=0; i < keys.size(); i++) {
                if (null != keys.get(i)){
                    resultList.set(i, dblist.get(j));
                    j++;
                }
            }

            return resultList;

        }

        List<T> res =  selectRowsInDb(context, Arrays.asList(args), ascending);
        return res;
    }

    @Override
    public List<T> selectRowsInDb(TableContext context, final List<PK> args, boolean ascending) {
        final StringBuilder s = new StringBuilder(128);
        s.append(SELECT).append(getSelectedColumns()).append(FROM).append(getTableName(context))
                .append(WHERE);

        for (int i = 0; i < args.size(); i++) {
            s.append(getPKColumnName()).append(S_E_Q).append(S_OR);
        }

        s.setLength(s.length() - S_OR.length());
        s.append(ORDER_BY).append(getPKColumnName()).append(ascending ? ASC : DESC);

        List<T> list = this.jdbcTemplateS.query(s.toString(), new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                for (int i = 0; i < args.size(); i++) {
                    ps.setObject(i + 1, args.get(i));
                }
            }
        }, new ResultSetExtractor<List<T>>() {
            @Override
            public List<T> extractData(ResultSet rs) throws SQLException, DataAccessException {
                List<T> tmp = new ArrayList<T>();

                int rowNum = 0;
                while (rs.next()) {
                    tmp.add(mapRow(rs, rowNum++));
                }

                return tmp;
            }

        });

        return list;
    }

    @Override
    public List<PK> selectPKs(TableContext context, String orderBy, final int limit) throws DataAccessException{
        Preconditions.checkNotNull(limit);
        Preconditions.checkNotNull(orderBy);

        final StringBuilder s = new StringBuilder(128);
        s.append(SELECT).append(getPKColumnName()).append(FROM).append(getTableName(context));
        s.append(ORDER_BY).append(orderBy);
        s.append(LIMIT_OFFSET);

        List<PK> pkList = this.jdbcTemplateS.query(s.toString(), new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setObject(1, 0);
                ps.setObject(2, limit);
            }

        }, new ResultSetExtractor<List<PK>>() {
            @Override
            public List<PK> extractData(ResultSet rs) throws SQLException, DataAccessException {

                List<PK> tmp = new ArrayList<PK>();

                while (rs.next()) {
                    PK v = (PK) JdbcUtils.getResultSetValue(rs, 1, getPKClass());
                    tmp.add(v);
                }

                return tmp;
            }

        });

        return pkList;
    }

    @Override
    public List<T> selectRows(TableContext context, String orderBy, final Integer limit) throws DataAccessException{
        Preconditions.checkNotNull(limit);
        Preconditions.checkNotNull(orderBy);

        final StringBuilder s = new StringBuilder(128);
        s.append(SELECT).append(getSelectedColumns()).append(FROM).append(getTableName(context));
        s.append(ORDER_BY).append(orderBy);
        s.append(LIMIT_OFFSET);

        List<T> list = this.jdbcTemplateS.query(s.toString(), new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setObject(1, 0);
                ps.setObject(2, limit);
            }

        }, new ResultSetExtractor<List<T>>() {
            @Override
            public List<T> extractData(ResultSet rs) throws SQLException, DataAccessException {

                List<T> tmp = new ArrayList<T>();

                int rowNum = 0;
                while (rs.next()) {
                    tmp.add(mapRow(rs, rowNum++));
                }

                return tmp;
            }

        });

        return list;

    }

    @Override
    public List<T> selectRows(TableContext context, String where, String orderBy, final Integer limit, final Object[] args) throws DataAccessException{
        Preconditions.checkNotNull(where);
        Preconditions.checkNotNull(args);

        final StringBuilder s = new StringBuilder(128);
        s.append(SELECT).append(getSelectedColumns()).append(FROM).append(getTableName(context));
        if (null != where) {
            s.append(WHERE).append(where);
        }
        if (null != orderBy){
            s.append(ORDER_BY).append(orderBy);
        }
        if (null != limit){
            s.append(LIMIT_OFFSET);
        }

        List<T> list = this.jdbcTemplateS.query(s.toString(), new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                int i = 0;
                for (i = 0; i < args.length; i++) {
                    ps.setObject(i + 1, args[i]);
                }
                if (null != limit) {
                    ps.setObject(i + 1, 0);
                    ps.setObject(i + 2, limit);
                }
            }
        }, new ResultSetExtractor<List<T>>() {
            @Override
            public List<T> extractData(ResultSet rs) throws SQLException, DataAccessException {
                List<T> tmp = new ArrayList<T>();

                int rowNum = 0;
                while (rs.next()) {
                    tmp.add(mapRow(rs, rowNum++));
                }

                return tmp;
            }

        });

        return list;
    }

    @Override
    public List<PK> selectPks(TableContext context, String where, String orderBy, final Integer limit, final Object[] args) throws DataAccessException{
        Preconditions.checkNotNull(where);
        Preconditions.checkNotNull(args);
        Preconditions.checkNotNull(orderBy);

        final StringBuilder s = new StringBuilder(128);
        s.append(SELECT).append(getPKColumnName()).append(FROM).append(getTableName(context));
        s.append(WHERE).append(where);
        s.append(ORDER_BY).append(orderBy);

        if (null != limit){
            s.append(LIMIT_OFFSET);
        }

        List<PK> list = this.jdbcTemplateS.query(s.toString(), new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                int i = 0;
                for (i = 0; i < args.length; i++) {
                    ps.setObject(i + 1, args[i]);
                }
                if (null != limit) {
                    ps.setObject(i + 1, 0);
                    ps.setObject(i + 2, limit);
                }
            }
        }, new ResultSetExtractor<List<PK>>() {
            @Override
            public List<PK> extractData(ResultSet rs) throws SQLException, DataAccessException {
                List<PK> tmp = new ArrayList<PK>();

                while (rs.next()) {
                    PK v = (PK) JdbcUtils.getResultSetValue(rs, 1, getPKClass());
                    tmp.add(v);
                }

                return tmp;
            }

        });

        return list;
    }

    @Override
    public List<PK> selectPKs(TableContext context, String where, String orderBy, final Object[] args) throws DataAccessException{
        Preconditions.checkNotNull(where);
        Preconditions.checkNotNull(args);

        final StringBuilder s = new StringBuilder(128);
        s.append(SELECT).append(getPKColumnName()).append(FROM).append(getTableName(context))
                .append(WHERE).append(where);

        if (null != orderBy){
            s.append(ORDER_BY).append(orderBy);
        }

        List<PK> list = this.jdbcTemplateS.query(s.toString(), new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                for (int i = 0; i < args.length; i++) {
                    ps.setObject(i + 1, args[i]);
                }
            }
        }, new ResultSetExtractor<List<PK>>() {
            @Override
            public List<PK> extractData(ResultSet rs) throws SQLException, DataAccessException {
                List<PK> personList = new ArrayList<PK>();

                while (rs.next()) {
                    PK val = (PK) JdbcUtils.getResultSetValue(rs, 1, getPKClass());
                    personList.add(val);
                }

                return personList;
            }

        });

        return list;

    }

    @Override
    public Map<String, Object> selectMap(TableContext context, String fields, String where, String groupBy, final Object[] args) throws DataAccessException{
        Preconditions.checkNotNull(fields);
        Preconditions.checkNotNull(args);
        Preconditions.checkNotNull(where);

        final StringBuilder s = new StringBuilder(128);
        s.append(SELECT).append(fields).append(FROM).append(getTableName(context));
        s.append(WHERE).append(where);

        if (null != groupBy){
            s.append(GROUP_BY).append(groupBy);
        }

        Map<String, Object> map = this.jdbcTemplateS.query(s.toString(), new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                for (int i = 0; i < args.length; i++) {
                    ps.setObject(i + 1, args[i]);
                }
            }
        }, new ResultSetExtractor<Map<String, Object>>() {
            @Override
            public Map<String, Object> extractData(ResultSet rs) throws SQLException, DataAccessException {
                Map<String, Object> map = new HashMap<String, Object>();

                ResultSetMetaData rsmd = rs.getMetaData();
                int columnCount = rsmd.getColumnCount();

                while (rs.next()) {

                    for (int i = 1; i <= columnCount; i++) {

                        String column = JdbcUtils.lookupColumnName(rsmd, i);
                        column.replaceAll(" ", "");

                        map.put(column, JdbcUtils.getResultSetValue(rs, i)); // 定位类型

                    }

                }

                return map;
            }

        });

        return map;
    }

    @Override
    public Map<String, Object> selectMap(String sql, Object[] args) throws DataAccessException {
        Preconditions.checkNotNull(sql);
        Preconditions.checkNotNull(args);

        Map<String, Object> map = this.jdbcTemplateS.query(sql, new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                for (int i = 0; i < args.length; i++) {
                    ps.setObject(i + 1, args[i]);
                }
            }
        }, new ResultSetExtractor<Map<String, Object>>() {
            @Override
            public Map<String, Object> extractData(ResultSet rs) throws SQLException, DataAccessException {
                Map<String, Object> map = new HashMap<String, Object>();

                ResultSetMetaData rsmd = rs.getMetaData();
                int columnCount = rsmd.getColumnCount();

                while (rs.next()) {

                    for (int i = 1; i <= columnCount; i++) {

                        String column = JdbcUtils.lookupColumnName(rsmd, i);
                        column.replaceAll(" ", "");

                        map.put(column, JdbcUtils.getResultSetValue(rs, i)); // 定位类型

                    }

                }

                return map;
            }

        });

        return map;
    }

    @Override
    public int count(TableContext context, String where, final Object[] args) throws DataAccessException{
        Preconditions.checkNotNull(where);
        Preconditions.checkNotNull(args);

        final StringBuilder s = new StringBuilder(128);
        s.append(SELECT).append("count(1) from ").append(getTableName(context));
        s.append(WHERE).append(where);

        List<Integer> list = this.jdbcTemplateS.query(s.toString(), new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                for (int i = 0; i < args.length; i++) {
                    ps.setObject(i + 1, args[i]);
                }
            }
        }, new ResultSetExtractor<List<Integer>>() {
            @Override
            public List<Integer> extractData(ResultSet rs) throws SQLException, DataAccessException {
                List<Integer> tmp = new ArrayList<Integer>();

                while (rs.next()) {
                    tmp.add(rs.getInt(1));
                }

                return tmp;
            }

        });

        return list.size() == 0 ? 0 : list.get(0);
    }

    @Override
    public List<T> query(String sql, Object[] args) throws DataAccessException {
        List<T> list = this.jdbcTemplateS.query(sql, new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                int i = 0;
                for (i = 0; i < args.length; i++) {
                    ps.setObject(i + 1, args[i]);
                }
            }
        }, new ResultSetExtractor<List<T>>() {
            @Override
            public List<T> extractData(ResultSet rs) throws SQLException, DataAccessException {
                List<T> tmp = new ArrayList<T>();

                int rowNum = 0;
                while (rs.next()) {
                    tmp.add(mapRow(rs, rowNum++));
                }

                return tmp;
            }

        });

        return list;
    }

    @Override
    public int execute(String sql, Object[] args) throws DataAccessException {
        return this.jdbcTemplateM.update(sql, new PreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps) throws SQLException {
                int i = 0;
                for (i = 0; i < args.length; i++) {
                    ps.setObject(i + 1, args[i]);
                }
            }
        });
    }
}
