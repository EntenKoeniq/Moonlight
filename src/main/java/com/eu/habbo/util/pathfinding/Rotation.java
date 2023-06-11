package com.eu.habbo.util.pathfinding;

public class Rotation {
    public static int Calculate(int x1, int y1, int x2, int y2) {
        int rotation = 0;
        if (x1 > x2) {
            if (y1 > y2) {
                rotation = 7;
            } else if (y1 < y2) {
                rotation = 5;
            } else {
                rotation = 6;
            }
        } else if (x1 < x2) {
            if (y1 > y2) {
                rotation = 1;
            } else if (y1 < y2) {
                rotation = 3;
            } else {
                rotation = 2;
            }
        } else {
            if (y1 > y2) {
                rotation = 0;
            } else if (y1 < y2) {
                rotation = 4;
            }
        }
        return rotation;
    }
}
