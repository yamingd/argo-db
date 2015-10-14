package com.argo.db.mysql.demo;

import com.argo.annotation.Column;
import com.argo.annotation.Table;
import com.google.common.base.Objects;
import org.msgpack.annotation.MessagePackMessage;

import javax.annotation.Generated;
import java.io.Serializable;

/**
 * Created by yamingd on 9/15/15.
 */
@Table("person")
@MessagePackMessage
@Generated("Generate from mysql table")
public class Person implements Serializable {

    @Column(pk = true, autoIncrement = true)
    private Integer id;

    @Column(maxLength = "200", nullable=false)
    private String name;

    @Column(nullable = false)
    private String firstName;

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
