package com.argo.db;

import com.argo.db.exception.EntityNotFoundException;
import com.argo.db.mysql.TableContext;
import org.springframework.dao.DataAccessException;

import java.util.List;
import java.util.Map;

/**
 * Created by yamingd on 9/29/15.
 */
public interface SqlMapper<T, PK extends Comparable> {
    /**
     * 准备公共常量
     */
    void prepare();

    /**
     * 数据表名称
     * @return String
     */
    String getTableName();

    /**
     *
     * @param context
     * @return String
     */
    String getTableName(TableContext context);

    /**
     *
     * @return String
     */
    String getPKColumnName();

    /**
     *
     * @return String
     */
    String getSelectedColumns();

    /**
     *
     * @return List
     */
    List<String> getColumnList();

    /**
     * 每行实体类
     * @return Class
     */
    Class<T> getRowClass();

    /**
     * 主键类型
     * @return
     */
    Class<PK> getPKClass();
    /**
     * 根据主键读取
     * @param id
     * @return T
     */
    T find(TableContext context, PK id) throws EntityNotFoundException;

    /**
     * 根据主键读取
     * @param context
     * @param id
     * @return T
     * @throws EntityNotFoundException
     */
    T findInMaster(TableContext context, PK id) throws EntityNotFoundException;

    /**
     * 插入记录
     * @param item
     * @return boolean
     */
    boolean insert(TableContext context, T item) throws DataAccessException;

    /**
     * 插入批量记录
     * @param list
     * @return boolean
     */
    boolean insertBatch(TableContext context, List<T> list) throws DataAccessException;

    /**
     * 删除缓存数据
     * @param id
     * @return boolean
     */
    boolean expire(PK id);
    /**
     * 删除缓存数据
     * @param ids
     */
    void expire(List<PK> ids);
    /**
     * 删除缓存数据
     * @param ids
     */
    void expire(PK[] ids);
    /**
     * 更新记录, 在子类实现
     * @param item
     * @return boolean
     */
    boolean update(TableContext context, T item) throws DataAccessException;

    /**
     * 更新
     * @param sql
     * @param args
     * @return boolean
     */
    boolean update(String sql, List<Object> args);

    /**
     * 更新记录
     * @param context
     * @param values
     * @param where
     * @param args
     * @return boolean
     */
    boolean update(TableContext context, String values, String where, Object... args) throws DataAccessException;

    /**
     * 删除记录
     * @param item
     * @return boolean
     */
    boolean delete(TableContext context, T item) throws DataAccessException;

    /**
     *
     * @param context
     * @param id
     * @return boolean
     */
    boolean deleteBy(TableContext context, PK id) throws DataAccessException;

    /**
     *
     * @param context
     * @param where
     * @param args
     * @return boolean
     */
    boolean deleteBy(TableContext context, String where, Object... args) throws DataAccessException;
    /**
     * 读取记录
     * @param context
     * @param pkWithCommas
     * @return List
     */
    List<T> findRows(TableContext context, String pkWithCommas, boolean ascending) throws DataAccessException;
    /**
     * 读取记录
     * @param context
     * @param args
     * @return List
     */
    List<T> selectRows(TableContext context, PK[] args, boolean ascending) throws DataAccessException;

    /**
     *
     * @param context
     * @param args
     * @param ascending
     * @return
     * @throws DataAccessException
     */
    List<T> selectRows(TableContext context, List<PK> args, boolean ascending) throws DataAccessException;

    /**
     *
     * 从数据库读取
     *
     * @param context
     * @param args
     * @return List
     */
    List<T> selectRowsInDb(TableContext context, List<PK> args, boolean ascending);

    /**
     * 读取记录PK
     * @param orderBy 不可为null
     * @param limit 不可为null
     * @return List
     */
    List<PK> selectPKs(TableContext context, String orderBy, int limit) throws DataAccessException;
    List<PK> selectPKs(TableContext context, String orderBy, int offset, int limit) throws DataAccessException;

    /**
     * 读取记录
     * @param orderBy 不可为null
     * @param limit 不可为null
     * @return List
     */
    List<T> selectRows(TableContext context, String orderBy, Integer limit) throws DataAccessException;
    List<T> selectRows(TableContext context, String orderBy, Integer offset, Integer limit) throws DataAccessException;

    /**
     * 读取记录
     * @param where 不可为null
     * @param orderBy 可为null
     * @param limit 可为null
     * @param args 不可为null
     * @return List
     */
    List<T> selectRows(TableContext context, String where, String orderBy, Integer limit, Object[] args) throws DataAccessException;
    List<T> selectRows(TableContext context, String where, String orderBy, Integer offset, Integer limit, Object[] args) throws DataAccessException;

    /**
     * 读取记录
     * @param where 不可为null
     * @param orderBy 可为null
     * @param limit 可为null
     * @param args 不可为null
     * @return List
     */
    List<PK> selectPks(TableContext context, String where, String orderBy, Integer limit, Object[] args) throws DataAccessException;
    List<PK> selectPks(TableContext context, String where, String orderBy, Integer offset, Integer limit, Object[] args) throws DataAccessException;

    /**
     * 读取PK
     *
     * @param where 必须
     * @param args 必须
     *
     * @return List
     */
    List<PK> selectPKs(TableContext context, String where, String orderBy, Object[] args) throws DataAccessException;

    /**
     *
     * 查询出一个Map
     * @param fields 必须
     * @param where 必须
     * @param groupBy 可为null
     * @param args 必须
     * @return Map
     */
    Map<String, Object> selectMap(TableContext context, String fields, String where, String groupBy, Object[] args) throws DataAccessException;

    /**
     *
     * @param sql
     * @param args
     * @return
     * @throws DataAccessException
     */
    Map<String, Object> selectMap(String sql, Object[] args) throws DataAccessException;

    /**
     * 计算个数
     *
     * @param where 必须
     * @param args 必须
     * @return int
     */
    int count(TableContext context, String where, Object[] args) throws DataAccessException;

    /**
     * 执行SQL, SQL返回T对象
     * @param sql SQL语句
     * @param args 参数
     * @return List
     * @throws DataAccessException
     */
    List<T> query(String sql, Object[] args) throws DataAccessException;

    /**
     * 计算个数
     * @param sql
     * @param args
     * @return
     * @throws DataAccessException
     */
    int count(String sql, Object[] args) throws DataAccessException;
    /**
     * 在Master服务器执行SQL
     * @param sql SQL语句
     * @param args 参数
     * @return int 影响的行数
     * @throws DataAccessException
     */
    int execute(String sql, Object[] args) throws DataAccessException;
}
