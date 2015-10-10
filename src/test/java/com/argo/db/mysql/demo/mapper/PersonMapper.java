package com.argo.db.mysql.demo.mapper;

import com.argo.db.Values;
import com.argo.db.mysql.TableContext;
import com.argo.db.mysql.demo.Person;
import com.argo.db.template.MySqlMapper;
import com.google.common.base.Preconditions;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yamingd on 9/15/15.
 */
@Repository
public class PersonMapper extends MySqlMapper<Person, Integer> {


    public static final String N_tableName = "person";
    public static final String N_pkColumnName = "id";

    public static final String SQL_FIELDS = "id, name";
    public static final List<String> columnList = new ArrayList<String>();
    public static final boolean pkAutoIncr = true;

    static {
        columnList.add("id");
        columnList.add("name");
    }

    @Override
    public void prepare() {

    }

    @Override
    public String getTableName() {
        return N_tableName;
    }

    @Override
    public String getTableName(TableContext context) {
        return null == context ? getTableName() : context.getName();
    }

    @Override
    public Class<Person> getRowClass() {
        return Person.class;
    }

    @Override
    protected void setPKValue(Person item, KeyHolder holder) {
        item.setId(holder.getKey().intValue());
    }

    @Override
    protected Integer getPkValue(Person item){
        return item.getId();
    }

    @Override
    protected void setInsertStatementValues(PreparedStatement ps, Person item) throws SQLException {
        int index = 1;
        if (!isPKAutoIncr()){
            ps.setObject(index, item.getId());
            index ++;
        }

        ps.setString(index, item.getName());
        index++;
    }


    @Override
    public boolean update(TableContext context, final Person item) throws DataAccessException {
        Preconditions.checkNotNull(item);

        final StringBuilder s = new StringBuilder(128);
        s.append(UPDATE).append(getTableName(context)).append(SET);

        final List<Object> args = new ArrayList<Object>();

        if (null != item.getName()){
            s.append("name=?, ");
            args.add(item.getName());
        }

        if (args.size() == 0){
            logger.warn("Nothing to update. ");
            return false;
        }

        s.setLength(s.length() - 2);
        s.append(WHERE).append(N_pkColumnName).append(S_E_Q);
        args.add(getPkValue(item));

        boolean ret = super.update(s.toString(), args);

        super.afterUpdate(context, item);

        return ret;
    }

    @Override
    public List<Person> selectRows(TableContext context, List<Integer> args, boolean ascending) throws DataAccessException {
        return super.selectRows(context, args.toArray(new Integer[0]), ascending);
    }

    @Override
    protected Person mapRow(ResultSet rs, int rowIndex) throws SQLException {
        Person item = new Person();
        item.setId(rs.getInt(1));
        item.setName(rs.getString(2));

        String name = Values.getResultSetValue(rs, 2, String.class);
        item.setName(name);

        return item;
    }

    @Override
    public String getPKColumnName() {
        return N_pkColumnName;
    }

    @Override
    public String getSelectedColumns() {
        return SQL_FIELDS;
    }

    @Override
    public List<String> getColumnList() {
        return columnList;
    }

    @Override
    protected boolean isPKAutoIncr() {
        return pkAutoIncr;
    }
}
