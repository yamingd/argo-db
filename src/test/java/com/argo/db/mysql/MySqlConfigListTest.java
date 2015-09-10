package com.argo.db.mysql;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by yamingd on 9/10/15.
 */
public class MySqlConfigListTest {

    @Test
    public void testLoad() throws Exception {
        MySqlConfigList.load();

        Assert.assertNotNull(MySqlConfigList.all);

        Assert.assertNotNull(MySqlConfigList.all.getMulti());

        Assert.assertNotNull(MySqlConfigList.all.getMs());

        Assert.assertTrue(MySqlConfigList.all.getMulti().size() > 0);

        Assert.assertTrue(MySqlConfigList.all.getMs().size() > 0);
    }
}
