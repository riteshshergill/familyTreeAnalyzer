package com.linejae.familytree;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.linejae.familytree.models.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(JUnit4.class)
public class MockDataGeneratorUtil {

    @Test
    public void generateFakeData() throws Exception {
        generateMockData();
    }

    public static void generateMockData() throws Exception {

        List<String> familyTreeNamesList = new ArrayList<>(20);

        for(int i = 0; i < 20; i++) {
            familyTreeNamesList.add("Family Tree for family " + i);
        }

        for(int i = 0; i < 20; i++) {
            familyTreeNamesList.add("Family Tree for family " + i);
            Root root = new Root();
            Lineage lineage = new Lineage();
            lineage.setFamilyTree(familyTreeNamesList.get(i));
            lineage.setMembers(generateInitialMembers());
            root.setLineage(lineage);
            System.out.println(new ObjectMapper().writeValueAsString(root));
        }



    }

    private static ArrayList<Member> generateInitialMembers() throws Exception{
        int min = 1;
        int max = 5;
        ArrayList<Member> initialMembers = new ArrayList<>();
        int random = (int) (Math.random()*(max-min+1)+min);
        for(int i = 0; i < random; i++) {
            initialMembers.add(fakeMemberGenerator());
        }

        return initialMembers;
    }

    private static Member fakeMemberGenerator() throws ParseException {
        Member member = new Member();
        Faker faker = new Faker();
        member.setName(faker.name().fullName());
        member.setBirthYear(getRandomDateString(faker, getInitialBirthSeedDate(), getLastBirthSeedDate()));
        member.setDeathYear(getRandomDateString(faker, getSecndaryBirthSeedDate(), getSecondaryLastBirthSeedDate()));
        return member;
    }

    private static Date getInitialBirthSeedDate() throws ParseException {
        String date_string = "01-01-2000";
        //Instantiating the SimpleDateFormat class
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        //Parsing the given String to Date object
        return formatter.parse(date_string);
    }

    private static Date getLastBirthSeedDate() throws ParseException {
        String date_string = "01-01-2030";
        //Instantiating the SimpleDateFormat class
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        //Parsing the given String to Date object
        return formatter.parse(date_string);
    }

    private static Date getSecndaryBirthSeedDate() throws ParseException {
        String date_string = "01-01-2031";
        //Instantiating the SimpleDateFormat class
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        //Parsing the given String to Date object
        return formatter.parse(date_string);
    }

    private static Date getSecondaryLastBirthSeedDate() throws ParseException {
        String date_string = "01-01-2060";
        //Instantiating the SimpleDateFormat class
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        //Parsing the given String to Date object
        return formatter.parse(date_string);
    }

    private static String getRandomDateString(Faker faker, Date initialDate, Date finalDate) {
        Date randomDate = faker.date().between(initialDate, finalDate);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
        return formatter.format(randomDate);

    }

}
