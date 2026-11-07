package com.ded.necronmod.radio.ui;

/** Математика вращающейся ручки (без Minecraft). Углы в градусах: 0 = вверх, по часовой стрелке положительные. */
final class KnobMath {
    private KnobMath() {}

    /** Угол стрелки для позиции index из steps (полный оборот на steps позиций). */
    static double angleForIndex(int index, int steps) {
        return 360.0 * index / Math.max(1, steps);
    }

    /** Направление от центра к мыши. dx вправо, dy вниз (экранные координаты). */
    static double mouseAngle(double dx, double dy) {
        return Math.toDegrees(Math.atan2(dx, -dy));
    }

    /** Кратчайшая разница углов в (-180, 180]. */
    static double shortestDelta(double from, double to) {
        double d = (to - from) % 360.0;
        if (d > 180.0) d -= 360.0;
        if (d <= -180.0) d += 360.0;
        return d;
    }

    /** Плавное приближение отображаемого угла к целевому по кратчайшему пути. */
    static double approach(double current, double target, double factor) {
        double d = shortestDelta(current, target);
        if (Math.abs(d) < 0.2) return target;
        return current + d * factor;
    }

    /** Переводит движение мыши по кругу в целые шаги (знак = направление, + по часовой). */
    static final class DragAccumulator {
        private double last = Double.NaN;
        private double rest;

        void reset() {
            last = Double.NaN;
            rest = 0;
        }

        int update(double angle, int steps) {
            if (Double.isNaN(last)) {
                last = angle;
                return 0;
            }
            rest += shortestDelta(last, angle);
            last = angle;
            double step = 360.0 / Math.max(1, steps);
            int whole = (int) (rest / step); // к нулю
            rest -= whole * step;
            return whole;
        }
    }
}
