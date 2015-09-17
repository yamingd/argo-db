package com.argo.db.mysql.demo;

import com.argo.annotation.Column;
import com.argo.annotation.Table;
import com.google.common.base.Objects;
import org.msgpack.annotation.MessagePackMessage;

/**
 * Created by yamingd on 9/15/15.
 */
@Table("person")
@MessagePackMessage
public class Person {

    @Column(pk = true, autoIncrement = true)
    private Integer id;

    @Column
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", id)
                .add("name", name)
                .toString();
    }
}
