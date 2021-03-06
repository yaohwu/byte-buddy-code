package xyz.yaohwu.another.world;

import xyz.yaohwu.anno.Monitor;
import xyz.yaohwu.anno.MonitorSub;

/**
 * @author yaohwu
 * created by yaohwu at 2020/7/24 18:02
 */
@Monitor
public class Bike {
    private Wheel wheel;

    @Monitor
    public void say() {
        System.out.println("ka-ka!");
        new Wheel().say();
    }

    public void setWheel(Wheel wheel) {
        this.wheel = wheel;
    }

    public Wheel getWheel() {
        return wheel;
    }

    @MonitorSub
    public final class Wheel implements Cloneable {

        private WheelInner wheelInner;

        @MonitorSub
        public void say() {
            System.out.println("wo-wo!");
            new WheelInner().say();
        }

        public void setWheelInner(WheelInner wheel) {
            this.wheelInner = wheel;
        }

        public WheelInner getWheelInner() {
            return wheelInner;
        }

        @MonitorSub
        public final class WheelInner implements Cloneable {

            @MonitorSub
            public void say() {
                System.out.println("to-to!");
            }
        }
    }
}
