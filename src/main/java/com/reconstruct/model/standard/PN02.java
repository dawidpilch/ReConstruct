package com.reconstruct.model.standard;

import com.reconstruct.model.beam.ConcreteGrade;

import java.util.LinkedList;
import java.util.List;

public class PN02 {
    public static List<ConcreteGrade> CONCRETE_GRADES = new LinkedList<>();
    static {
        CONCRETE_GRADES.add(new ConcreteGrade("B15", 8.0d, 0.73d, 1.6d));
        CONCRETE_GRADES.add(new ConcreteGrade("B20", 10.6d, 0.87d, 1.9d));
        CONCRETE_GRADES.add(new ConcreteGrade("B25", 13.3d, 1.00d, 2.2d));
        CONCRETE_GRADES.add(new ConcreteGrade("B30", 16.7d, 1.20d, 2.6d));
        CONCRETE_GRADES.add(new ConcreteGrade("B37", 20.0d, 1.33d, 2.9d));
        CONCRETE_GRADES.add(new ConcreteGrade("B45", 23.3d, 1.47d, 3.2d));
        CONCRETE_GRADES.add(new ConcreteGrade("B50", 26.7d, 1.67d, 3.5d));
        CONCRETE_GRADES.add(new ConcreteGrade("B55", 30.0d, 1.80d, 3.8d));
        CONCRETE_GRADES.add(new ConcreteGrade("B60", 33.3d, 1.93d, 4.1d));}
}
