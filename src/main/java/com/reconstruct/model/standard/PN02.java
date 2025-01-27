package com.reconstruct.model.standard;

import com.reconstruct.model.beam.ConcreteGrade;
import com.reconstruct.model.beam.ReinforcementMaterialGrade;

import java.util.*;

public class PN02 {
    private PN02() { }

    public static Map<String, Double> MINIMAL_CORROSION_COVER_THICKNESS_FOR_REINFORCEMENT_STANDARD_STEEL_MM = new LinkedHashMap<>();
    static {
        MINIMAL_CORROSION_COVER_THICKNESS_FOR_REINFORCEMENT_STANDARD_STEEL_MM.put("X0", 10d);
        MINIMAL_CORROSION_COVER_THICKNESS_FOR_REINFORCEMENT_STANDARD_STEEL_MM.put("XC1", 15d);
        MINIMAL_CORROSION_COVER_THICKNESS_FOR_REINFORCEMENT_STANDARD_STEEL_MM.put("XC2/XC3", 20d);
        MINIMAL_CORROSION_COVER_THICKNESS_FOR_REINFORCEMENT_STANDARD_STEEL_MM.put("XC4", 25d);
        MINIMAL_CORROSION_COVER_THICKNESS_FOR_REINFORCEMENT_STANDARD_STEEL_MM.put("XD1/XD2,XD3", 40d);
        MINIMAL_CORROSION_COVER_THICKNESS_FOR_REINFORCEMENT_STANDARD_STEEL_MM.put("XS1/XS2/XS3", 40d);
    }

    public static Map<String, Double> MINIMAL_CORROSION_COVER_THICKNESS_FOR_REINFORCEMENT_PRESTRESSED_STEEL_MM = new LinkedHashMap<>();
    static {
        MINIMAL_CORROSION_COVER_THICKNESS_FOR_REINFORCEMENT_PRESTRESSED_STEEL_MM.put("X0", 15d);
        MINIMAL_CORROSION_COVER_THICKNESS_FOR_REINFORCEMENT_PRESTRESSED_STEEL_MM.put("XC1", 20d);
        MINIMAL_CORROSION_COVER_THICKNESS_FOR_REINFORCEMENT_PRESTRESSED_STEEL_MM.put("XC2/XC3", 30d);
        MINIMAL_CORROSION_COVER_THICKNESS_FOR_REINFORCEMENT_PRESTRESSED_STEEL_MM.put("XC4", 35d);
        MINIMAL_CORROSION_COVER_THICKNESS_FOR_REINFORCEMENT_PRESTRESSED_STEEL_MM.put("XD1/XD2,XD3", 50d);
        MINIMAL_CORROSION_COVER_THICKNESS_FOR_REINFORCEMENT_PRESTRESSED_STEEL_MM.put("XS1/XS2/XS3", 50d);
    }

    public static List<ConcreteGrade> CONCRETE_GRADE = new LinkedList<>();
    static {
        CONCRETE_GRADE.add(new ConcreteGrade("B15", 8.0d));
        CONCRETE_GRADE.add(new ConcreteGrade("B20", 10.6d));
        CONCRETE_GRADE.add(new ConcreteGrade("B25", 13.3d));
        CONCRETE_GRADE.add(new ConcreteGrade("B30", 16.7d));
        CONCRETE_GRADE.add(new ConcreteGrade("B37", 20.0d));
        CONCRETE_GRADE.add(new ConcreteGrade("B45", 23.3d));
        CONCRETE_GRADE.add(new ConcreteGrade("B50", 26.7d));
        CONCRETE_GRADE.add(new ConcreteGrade("B55", 30.0d));
        CONCRETE_GRADE.add(new ConcreteGrade("B60", 33.3d));
    }

    public static List<ReinforcementMaterialGrade> STEEL_GRADES = new LinkedList<>();
    static {
        STEEL_GRADES.add(new ReinforcementMaterialGrade(
                        "A-0",
                190.0d,
                0.787,
                        0.637,
                        0.673,
                        0.428
        ));
        STEEL_GRADES.add(new ReinforcementMaterialGrade(
                        "A-I",
                210.0d,
                0.769,
                        0.622,
                        0.680,
                        0.423
        ));
        STEEL_GRADES.add(new ReinforcementMaterialGrade(
                        "A-II",
                310.0d,
                0.693,
                        0.561,
                        0.712,
                        0.399
        ));
        STEEL_GRADES.add(new ReinforcementMaterialGrade(
                        "A-III",
                350.0d,
                0.667,
                        0.540,
                        0.772,
                        0.390
        ));
        STEEL_GRADES.add(new ReinforcementMaterialGrade(
                        "A-IIIN",
                420.0d,
                0.625,
                        0.505,
                        0.740,
                        0.374
        ));
    }

    public static Map<Double, Double[]> CROSS_SECTION_AREAS = new LinkedHashMap<>();
    static {
        CROSS_SECTION_AREAS.put(4.5, new Double[]{ 0.16, 0.32, 0.49, 0.64, 0.80, 0.96, 1.11, 1.27, 1.43, 1.60, 1.76, 1.92, 2.09, 2.24, 2.40 });
        CROSS_SECTION_AREAS.put(5.0, new Double[]{ 0.20, 0.39, 0.59, 0.78, 0.98, 1.18, 1.37, 1.57, 1.77, 2.00, 2.20, 2.39, 2.59, 2.78, 3.00 });
        CROSS_SECTION_AREAS.put(6.0, new Double[]{ 0.283, 0.566, 0.849, 1.13, 1.42, 1.70, 1.98, 2.26, 2.55, 2.83, 3.11, 3.40, 3.70, 3.96, 4.24 });
        CROSS_SECTION_AREAS.put(8.0, new Double[]{ 0.503, 1.01, 1.51, 2.01, 2.52, 3.02, 3.52, 4.02, 4.53, 5.03, 5.53, 6.04, 6.54, 7.04, 7.54 });
        CROSS_SECTION_AREAS.put(10.0, new Double[]{ 0.785, 1.57, 2.36, 3.14, 3.92, 4.71, 5.50, 6.28, 7.06, 7.85, 8.64, 9.42, 10.20, 11.00, 11.80 });
        CROSS_SECTION_AREAS.put(12.0, new Double[]{ 1.13, 2.26, 3.39, 4.52, 5.65, 6.78, 7.91, 9.04, 10.18, 11.30, 12.40, 13.60, 14.70, 15.80, 17.00 });
        CROSS_SECTION_AREAS.put(14.0, new Double[]{ 1.54, 3.08, 4.62, 6.16, 7.70, 9.24, 10.78, 12.32, 13.86, 15.40, 16.94, 18.84, 20.02, 21.56, 32.10 });
        CROSS_SECTION_AREAS.put(16.0, new Double[]{ 2.01, 4.02, 6.03, 8.04, 10.05, 12.06, 14.07, 16.08, 18.09, 20.10, 22.11, 24.12, 26.13, 28.14, 30.15 });
        CROSS_SECTION_AREAS.put(18.0, new Double[]{ 2.54, 5.09, 7.63, 10.18, 12.72, 15.27, 17.81, 20.36, 22.90, 25.40, 27.94, 30.49, 33.03, 35.58, 38.18 });
        CROSS_SECTION_AREAS.put(20.0, new Double[]{ 3.14, 6.28, 9.43, 12.57, 15.71, 18.85, 21.99, 25.14, 28.28, 31.40, 34.54, 37.68, 40.83, 43.97, 47.20 });
        CROSS_SECTION_AREAS.put(25.0, new Double[]{ 4.91, 9.82, 14.73, 19.64, 24.54, 29.45, 34.36, 39.27, 44.18, 49.10, 54.01, 58.92, 63.83, 68.74, 73.64 });
        CROSS_SECTION_AREAS.put(32.0, new Double[]{ 8.04, 16.08, 24.13, 32.17, 40.21, 48.26, 56.30, 64.34, 72.38, 80.40, 88.44, 96.48, 104.60, 112.60, 120.60 });
        CROSS_SECTION_AREAS.put(40.0, new Double[]{ 12.56, 25.13, 37.70, 50.26, 62.83, 75.40, 87.96, 100.50, 113.10, 125.60, 138.20, 150.70, 163.30, 175.90, 188.40 });
    }
}
