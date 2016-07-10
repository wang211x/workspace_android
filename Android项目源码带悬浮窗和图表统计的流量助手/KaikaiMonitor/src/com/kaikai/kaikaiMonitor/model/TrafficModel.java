package com.kaikai.kaikaiMonitor.model;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 15/5/6
 * * * * * * * * * * * * * * * * * * * * * * *
 **/

/**
 * 以天为单位保存每个应用数据
 */
@Table(name = "traffic")
public class TrafficModel {
        @Id(column = "id")
        private int id;
        @Column(column = "uid")
        private int uid;
        @Column(column = "rx")
        private long rx;
        @Column(column = "tx")
        private long tx;
        @Column(column = "time")
        private long time;

        public long getTime() {
                return time;
        }

        public void setTime(long time) {
                this.time = time;
        }

        public int getId() {
                return id;
        }

        public void setId(int id) {
                this.id = id;
        }

        public long getRx() {
                return rx;
        }

        public void setRx(long rx) {
                this.rx = rx;
        }

        public long getTx() {
                return tx;
        }

        public void setTx(long tx) {
                this.tx = tx;
        }

        public int getUid() {
                return uid;
        }

        public void setUid(int uid) {
                this.uid = uid;
        }
}
