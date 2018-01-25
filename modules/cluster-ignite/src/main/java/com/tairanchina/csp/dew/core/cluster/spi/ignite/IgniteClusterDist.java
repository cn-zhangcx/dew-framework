package com.tairanchina.csp.dew.core.cluster.spi.ignite;

import com.tairanchina.csp.dew.core.cluster.ClusterDist;
import com.tairanchina.csp.dew.core.cluster.ClusterDistLock;
import com.tairanchina.csp.dew.core.cluster.ClusterDistMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

public class IgniteClusterDist implements ClusterDist {

    private IgniteAdapter igniteAdapter;

    public IgniteClusterDist(IgniteAdapter igniteAdapter) {
        this.igniteAdapter = igniteAdapter;
    }

    @Override
    public ClusterDistLock lock(String key) {
        return new IgniteClusterDistLock(key, igniteAdapter.getIgnite());
    }

    @Override
    public <M> ClusterDistMap<M> map(String key, Class<M> clazz) {
        return new IgniteClusterDistMap<>(key, igniteAdapter.getIgnite());
    }

}
