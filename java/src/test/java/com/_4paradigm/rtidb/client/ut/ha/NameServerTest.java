package com._4paradigm.rtidb.client.ut.ha;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com._4paradigm.rtidb.client.ha.impl.NameServerClientImpl;
import com._4paradigm.rtidb.ns.NS.PartitionMeta;
import com._4paradigm.rtidb.ns.NS.TableInfo;
import com._4paradigm.rtidb.ns.NS.TablePartition;


/**
 * 需要外部启动ns 环境
 * @author wangtaize
 *
 */
public class NameServerTest {

    private static String zkEndpoints = "127.0.0.1:6181";
    private static String leaderPath  = "/onebox/leader";
    private static String[] nodes = new String[] {"127.0.0.1:9522", "127.0.0.1:9521", "127.0.0.1:9520"};
    static {
        String envZkEndpoints = System.getenv("zkEndpoints");
        if (envZkEndpoints != null) {
            zkEndpoints = envZkEndpoints;
        }
        String envleaderPath = System.getenv("leaderPath");
        if (envleaderPath != null) {
            leaderPath = envleaderPath;
        }
    }
    @Test
    public void testInvalidZkInit() {
        try {
            NameServerClientImpl nsc = new NameServerClientImpl("xxxxx", "xxxx");
            nsc.init();
            Assert.assertTrue(false);
            nsc.close();
        } catch(Exception e) {
            Assert.assertTrue(true);
        }
    }
    
    @Test
    public void testInvalidEndpointInit() {
        try {
            NameServerClientImpl nsc = new NameServerClientImpl(zkEndpoints, "xxxx");
            nsc.init();
            Assert.assertTrue(false);
            nsc.close();
        } catch(Exception e) {
            Assert.assertTrue(true);
        }
    }
    
    @Test
    public void testNsInit() {
        try {
            NameServerClientImpl nsc = new NameServerClientImpl(zkEndpoints, leaderPath);
            nsc.init();
            Assert.assertTrue(true);
            nsc.close();
        } catch(Exception e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
    }
    
    @Test
    public void testAllFlow() {
        PartitionMeta pm = PartitionMeta.newBuilder().setEndpoint(nodes[0]).setIsLeader(true).build();
        TablePartition tp = TablePartition.newBuilder().addPartitionMeta(pm).setPid(0).build();
        TableInfo tableInfo = TableInfo.newBuilder().setName("t1").setSegCnt(8).addTablePartition(tp).build();
        try {
            NameServerClientImpl nsc = new NameServerClientImpl(zkEndpoints, leaderPath);
            nsc.init();
            Assert.assertTrue(true);
            Assert.assertTrue(nsc.createTable(tableInfo));
            List<TableInfo> tables = nsc.showTable("t1");
            Assert.assertTrue(tables.size() == 1);
            Assert.assertTrue(tables.get(0).getName().equals("t1"));
            Assert.assertTrue( nsc.dropTable("t1"));
            tables = nsc.showTable("t1");
            Assert.assertTrue(tables.size() == 0);
            nsc.close();
        } catch(Exception e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
    }
    
}