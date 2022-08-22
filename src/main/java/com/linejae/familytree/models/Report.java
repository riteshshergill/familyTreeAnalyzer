package com.linejae.familytree.models;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Report {

    String familyTreeName;
    List<StringBuilder> lineage;
    List<StringBuilder> ageList;
    String lineageRange;
    String meanAge;
    String medianAge;
    List<StringBuilder> interQuartileRange;
    List<StringBuilder> longestAndShortestLiving;

}
