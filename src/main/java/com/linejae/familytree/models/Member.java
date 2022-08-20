package com.linejae.familytree.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;

@Getter
@Setter
public class Member {

    @JsonProperty("Name")
    public String name;
    @JsonProperty("BirthYear")
    public String birthYear;
    @JsonProperty("DeathYear")
    public String deathYear;
    @JsonProperty("Members")
    public ArrayList<Member> members;

    public String toString() {
        return this.getName() + "-" + this.getBirthYear() + "-" + this.getDeathYear();
    }
}
