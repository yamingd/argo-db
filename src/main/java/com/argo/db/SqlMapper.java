package com.argo.db;

import com.argo.db.exception.EntityNotFoundException;
import com.argo.db.mysql.TableContext;
import org.springframework.dao.DataAccessException;

import java.util.List;
import java.util.Map;

/**
 * SQL Mapper封装常用方法
 *
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
     * 返回表名称
     * @param context 数据访问上下文对象
     * @return String
     */
    String getTableName(TableContext context);

    /**
     * 返回主键列名称
     * @return String
     */
    String getPKColumnName();

    /**
     * 返回SELECT语句的字段
     * @return String
     */
    String getSelectedColumns();

    /**
     * 返回该表的数据字段
     * @return List
     */
    List<String> getColumnList();

    /**
     * 返回数据实体类型
     * @return Class
     */
    Class<T> getRowClass();

    /**
     * 返回主键字段的类型
     * @return
     */
    Class<PK> getPKClass();
    /**
     * 根据主键在从库读取记录
     * @param context 数据库访问上下文
     * @param id 主键
     * @return T
     * @throws EntityNotFoundException
     */
    T find(TableContext context, PK id) throws EntityNotFoundException;

    /**
     * 根据主键在主库中读取记录
     * @param context 数据库访问上下文
     * @param id 主键
     * @return T
     * @throws EntityNotFoundException
     */
    T findInMaster(TableContext context, PK id) throws EntityNotFoundException;

    /**
     * 插入记录
     * @param item 数据实体
     * @return boolean 返回true or false
     */
    boolean insert(TableContext context, T item) throws DataAccessException;

    /**
     * 批量插入记录
     * @param list 数据实体数组
     * @return boolean
     */
    boolean insertBatch(TableContext context, List<T> list) throws DataAccessException;
    /**
     * 删除缓存数据
     * @param id 数据主键值
     * @return boolean
     */
    boolean expire(PK id);
    /**
     * 删除缓存数据
     * @param ids 数据主键值
     */
    void expire(List<PK> ids);
    /**
     * 删除缓存数据
     * @param ids 数据主键值
     */
    void expire(PK[] ids);
    /**
     * 更新记录, 在子类实现
     * @param item 数据实体, 值为NULL的属性会不做更新
     * @return boolean 返回true or false
     */
    boolean update(TableContext context, T item) throws DataAccessException;

    /**
     * 更新记录
     * @param sql 更新SQL语句, 按SQL规范编写, 不可拼接参数. 注意SQL注入攻击
     * @param args SQL参数数组
     * @return boolean 返回true or false
     * @throws DataAccessException
     */
    boolean update(String sql, List<Object> args) throws DataAccessException;

    /**
     * 更新记录
     * @param context 数据库访问上下文
     * @param values 更新语句的SET部分, 多个字段用逗号分隔, 不可拼接参数, 注意SQL注入攻击
     * @param where 更新条件部分
     * @param args 更新参数
     * @return boolean 返回 true or false
     * @throws DataAccessException
     */
    boolean update(TableContext context, String values, String where, Object... args) throws DataAccessException;

    /**
     * 物理删除记录
     * @param context 数据库访问上下文
     * @param item 数据实体记录
     * @return boolean 返回true or false
     * @throws DataAccessException
     */
    boolean delete(TableContext context, T item) throws DataAccessException;

    /**
     * 按主键物理删除记录
     * @param context 数据库访问上下文
     * @param id 主键值
     * @return boolean 返回 true or false
     * @throws DataAccessException
     */
    boolean deleteBy(TableContext context, PK id) throws DataAccessException;
    /**
     * 按条件物理删除记录
     * @param context 数据库访问上下文
     * @param where 删除条件语句
     * @param args 删除参数
     * @return boolean 返回 true or false
     * @throws DataAccessException
     */
    boolean deleteBy(TableContext context, String where, Object... args) throws DataAccessException;
    /**
     * 按主键查询记录
     * @param context 数据库访问上下文
     * @param pkWithCommas 逗号分隔的主键值串
     * @param ascending 按主键排序(true=升序, false=降序)
     * @return List 返回数据库记录.
     * @throws DataAccessException
     */
    List<T> findRows(TableContext context, String pkWithCommas, boolean ascending) throws DataAccessException;
    /**
     * 按主键查询记录
     * @param context 数据库访问上下文
     * @param args 主键值数组
     * @param ascending 按主键排序(true=升序, false=降序)
     * @return List 返回数据库记录.
     * @throws DataAccessException
     */
    List<T> selectRows(TableContext context, PK[] args, boolean ascending) throws DataAccessException;
    /**
     * 按主键查询记录
     * @param context 数据库访问上下文
     * @param args 主键值数组
     * @param ascending 按主键排序(true=升序, false=降序)
     * @return 返回数据库记录
     * @throws DataAccessException
     */
    List<T> selectRows(TableContext context, List<PK> args, boolean ascending) throws DataAccessException;

    /**
     * 按主键从数据库读取记录
     * @param context 数据库访问上下文
     * @param args 主键值数组
     * @param ascending 按主键排序(true=升序, false=降序)
     * @return List 返回数据库记录
     * @throws DataAccessException
     */
    List<T> selectRowsInDb(TableContext context, List<PK> args, boolean ascending) throws DataAccessException;

    /**
     * 读取记录主键
     * @param context 数据库访问上下文
     * @param orderBy 排序字段串, 不可为null
     * @param limit 最多返回记录个数, 不可为null
     * @return List 返回主键值数组
     * @throws DataAccessException
     */
    List<PK> selectPKs(TableContext context, String orderBy, int limit) throws DataAccessException;

    /**
     * 读取记录主键
     * @param context 数据库访问上下文
     * @param orderBy 排序字段串, 不可为null
     * @param offset 列表分页偏移量
     * @param limit 最多返回记录个数, 不可为null
     * @return List 返回主键值数组
     * @throws DataAccessException
     */
    List<PK> selectPKs(TableContext context, String orderBy, int offset, int limit) throws DataAccessException;

    /**
     * 读取记录
     * @param context 数据库访问上下文
     * @param orderBy 排序字段串, 不可为null
     * @param limit 最多返回记录个数, 不可为null
     * @return List 返回数据实体数组
     * @throws DataAccessException
     */
    List<T> selectRows(TableContext context, String orderBy, Integer limit) throws DataAccessException;

    /**
     * 读取记录
     * @param context 数据库访问上下文
     * @param orderBy 排序字段串, 不可为null
     * @param offset 列表分页偏移量
     * @param limit 最多返回记录个数, 不可为null
     * @return List 返回数据实体数组
     * @throws DataAccessException
     */
    List<T> selectRows(TableContext context, String orderBy, Integer offset, Integer limit) throws DataAccessException;

    /**
     * 读取记录
     * @param context 数据库访问上下文
     * @param where 筛选条件, 不可为null
     * @param orderBy 排序字段串, 可为null
     * @param limit 最多返回记录个数, 可为null
     * @param args 筛选参数数组. 不可为null
     * @return List 返回数据实体数组
     * @throws DataAccessException
     */
    List<T> selectRows(TableContext context, String where, String orderBy, Integer limit, Object[] args) throws DataAccessException;
    /**
     * 读取记录
     * @param context 数据库访问上下文
     * @param where 筛选条件, 不可为null
     * @param orderBy 排序字段串, 可为null
     * @param offset 列表分页偏移量, 可为null
     * @param limit 最多返回记录个数, 可为null
     * @param args 筛选参数数组. 不可为null
     * @return List 返回数据实体数组
     * @throws DataAccessException
     */
    List<T> selectRows(TableContext context, String where, String orderBy, Integer offset, Integer limit, Object[] args) throws DataAccessException;

    /**
     * 读取记录主键
     * @param context 数据库访问上下文
     * @param where 筛选条件, 不可为null
     * @param orderBy 排序字段串, 可为null
     * @param limit 最多返回记录个数, 可为null
     * @param args 筛选参数数组. 不可为null
     * @return List 返回主键数组
     * @throws DataAccessException
     */
    List<PK> selectPks(TableContext context, String where, String orderBy, Integer limit, Object[] args) throws DataAccessException;
    /**
     * 读取记录主键
     * @param context 数据库访问上下文
     * @param where 筛选条件, 不可为null
     * @param orderBy 排序字段串, 可为null
     * @param offset 列表分页偏移量, 可为null
     * @param limit 最多返回记录个数, 可为null
     * @param args 筛选参数数组. 不可为null
     * @return List 返回主键数组
     * @throws DataAccessException
     */
    List<PK> selectPks(TableContext context, String where, String orderBy, Integer offset, Integer limit, Object[] args) throws DataAccessException;

    /**
     * 读取记录主键
     * @param context 数据库访问上下文
     * @param where 筛选条件, 不可为null
     * @param orderBy 排序字段串, 可为null
     * @param args 筛选参数数组. 不可为null
     * @return List 返回主键数组
     * @throws DataAccessException
     */
    List<PK> selectPKs(TableContext context, String where, String orderBy, Object[] args) throws DataAccessException;

    /**
     *
     * 执行查询,并返回Map结果集
     * @param context 数据库访问上下文
     * @param fields 返回字段(s), 逗号分隔
     * @param where 筛选条件语句
     * @param groupBy 分组条件语句
     * @param args SQL参数列表
     * @return Map
     * @throws DataAccessException
     */
    Map<String, Object> selectMap(TableContext context, String fields, String where, String groupBy, Object[] args) throws DataAccessException;

    /**
     * 执行查询,并返回Map结果集
     * @param sql 完整的查询SQL语句, 注意不要拼接参数, SQL注入攻击
     * @param args 查询参数
     * @return Map
     * @throws DataAccessException
     */
    Map<String, Object> selectMap(String sql, Object[] args) throws DataAccessException;

    /**
     * 计算个数
     * @param context 数据库访问上下文
     * @param where 查询条件部分
     * @param args 筛选参数
     * @return int 个数
     * @throws DataAccessException
     */
    int count(TableContext context, String where, Object[] args) throws DataAccessException;

    /**
     * 执行SQL, 返回数据实体对象
     * @param sql 查询SQL语句, 注意不要拼接参数, SQL注入攻击
     * @param args 查询参数
     * @return List 数据数组
     * @throws DataAccessException
     */
    List<T> query(String sql, Object[] args) throws DataAccessException;

    /**
     * 计算个数
     * @param sql 查询SQL语句, 注意不要拼接参数, SQL注入攻击
     * @param args 查询参数
     * @return int 个数
     * @throws DataAccessException
     */
    int count(String sql, Object[] args) throws DataAccessException;
    /**
     * 在主库服务器执行更新SQL(update, delete)
     * @param sql 更新SQL语句, 注意不要拼接参数, SQL注入攻击
     * @param args 更新参数
     * @return int 返回影响的行数, 可判断是否执行成功
     * @throws DataAccessException
     */
    int execute(String sql, Object[] args) throws DataAccessException;
}
