package cn.master.quartz;

import org.quartz.spi.InstanceIdGenerator;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author : 11's papa
 * @since : 2026/5/9, 星期六
 **/
public class QuartzInstanceIdGenerator implements InstanceIdGenerator {
    @Override
    public String generateInstanceId() {
        try {
            return InetAddress.getLocalHost().getHostName() + System.currentTimeMillis();
        } catch (UnknownHostException e) {
            return "unknown-" + System.currentTimeMillis();
        }
    }
}