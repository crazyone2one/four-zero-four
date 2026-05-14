package cn.master.quartz.config;

import org.jspecify.annotations.NullMarked;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author : 11's papa
 * @since : 2026/5/9, 星期六
 **/
@NullMarked
public record FixedDelayJobData(long delay, TimeUnit delayUnit) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public FixedDelayJobData(long delay) {
        this(delay, TimeUnit.MILLISECONDS);
    }

    public FixedDelayJobData {
        if (delay == 0) {
            throw new IllegalArgumentException("Delay cannot be zero");
        }
        // if (delayUnit == null) {
        //     throw new IllegalArgumentException("Delay Unit should be provided");
        // }
    }

    public Date getNextScheduleDate() {
        return new Date(System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(delay, delayUnit));
    }

    @Override
    public String toString() {
        return "FixedDelayJobData [delay=" + delay + ", delayUnit=" + delayUnit + "]";
    }
}
