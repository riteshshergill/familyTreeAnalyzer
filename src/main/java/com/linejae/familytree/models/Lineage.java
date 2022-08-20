package com.linejae.familytree.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class Lineage {

    @JsonProperty("FamilyTree")
    public String familyTree;
    @JsonProperty("Members")
    public ArrayList<Member> members;
}
