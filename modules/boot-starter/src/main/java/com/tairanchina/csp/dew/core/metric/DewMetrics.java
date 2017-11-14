package com.tairanchina.csp.dew.core.metric;

import com.tairanchina.csp.dew.core.filter.DewFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.PublicMetrics;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * desription:
 * Created by ding on 2017/11/13.
 */
@Component
public class DewMetrics implements PublicMetrics {

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public Collection<Metric<?>> metrics() {
        List<Metric<?>> metrics = new ArrayList<Metric<?>>();
        metrics.add(new Metric<Long>("spring.startup-date", applicationContext.getStartupDate()));
        metrics.add(new Metric<Integer>("spring.bean.definitions", applicationContext.getBeanDefinitionCount()));
        metrics.add(new Metric<Integer>("spring.beans", applicationContext.getBeanNamesForType(Object.class).length));
        metrics.add(new Metric<Integer>("spring.controllers", applicationContext.getBeanNamesForAnnotation(Controller.class).length));
        Object[] timeArr = DewFilter.timeList.toArray();
        Arrays.sort(timeArr);
        metrics.add(new Metric<Integer>("dew.response.nityPercent", (Integer) timeArr[(int) (DewFilter.timeList.size() * 0.9)]));
        long sum = 0;
        for (int unit : DewFilter.timeList) {
            sum += unit;
        }
        metrics.add(new Metric<Integer>("dew.response.average", (int) (sum / DewFilter.timeList.size())));
        metrics.add(new Metric<Integer>("dew.response.max", (Integer) timeArr[DewFilter.timeList.size() - 1]));
        return metrics;
    }

}

