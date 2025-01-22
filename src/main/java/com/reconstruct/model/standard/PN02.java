package com.reconstruct.model.standard;

import com.reconstruct.model.beam.ConcreteGrade;
import com.reconstruct.model.beam.ReinforcementMaterialGrade;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
        STEEL_GRADES.add(
                new ReinforcementMaterialGrade(
                        "A-0",
                        190.0d,
                        0.637,
                        0.673,
                        0.428
                )
        );
        STEEL_GRADES.add(
                new ReinforcementMaterialGrade(
                        "A-I",
                        210.0d,
                        0.622,
                        0.680,
                        0.423
                )
        );
        STEEL_GRADES.add(
                new ReinforcementMaterialGrade(
                        "A-II",
                        310.0d,
                        0.561,
                        0.712,
                        0.399
                )
        );
        STEEL_GRADES.add(
                new ReinforcementMaterialGrade(
                        "A-III",
                        350.0d,
                        0.540,
                        0.772,
                        0.390
                )
        );
        STEEL_GRADES.add(
                new ReinforcementMaterialGrade(
                        "A-IIIN",
                        420.0d,
                        0.505,
                        0.740,
                        0.374
                )
        );
    }
}
