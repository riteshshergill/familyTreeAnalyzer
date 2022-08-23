package com.linejae.familytree.services;

import com.gcache.graph.model.Node;
import com.github.javafaker.Faker;
import com.linejae.familytree.models.Lineage;
import com.linejae.familytree.models.Member;
import com.linejae.familytree.models.Root;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MockDataGeneratorUtil {

    /**
     * Generate a randomly generated lineage
     * @throws Exception
     */
    public static List<Root> generateMockData() throws Exception {

        List<String> familyTreeNamesList = new ArrayList<>(20);

        List<Root> randomLineage = new ArrayList<>();

        for(int i = 0; i < 5; i++) {
            familyTreeNamesList.add("Family Tree for family " + i);
            Root root = new Root();
            Lineage lineage = new Lineage();
            lineage.setFamilyTree(familyTreeNamesList.get(i));
            lineage.setMembers(generateInitialMembers());
            root.setLineage(lineage);
            //root and initial members are set now generate the hierarchy
            final AtomicInteger maxMembersCount = new AtomicInteger(0);
            generateMemberHierarchy(lineage.getMembers(), maxMembersCount);
            randomLineage.add(root);
        }



        return randomLineage;

    }

    /**
     * Method to generate a hierarchy of members randomly
     * @param membersList
     * @param maxMembersCount
     */
    private static void generateMemberHierarchy(List<Member> membersList, AtomicInteger maxMembersCount) {
        maxMembersCount.set(maxMembersCount.get() + 1);
        if(maxMembersCount.get() > 20) {
            return;
        }
        membersList.forEach(member -> {
            try {
                int min = 1;
                int max = 6;
                int randomLevelsToGenerate = (int) (Math.random()*(max-min+1)+min);
                System.out.println("level generation count: " + randomLevelsToGenerate);
                //generate only if 1,3 or 5 comes randomly
                if(randomLevelsToGenerate == 1 || randomLevelsToGenerate == 3
                        || randomLevelsToGenerate == 5) {
                    member.setMembers(generateRandomMembers());
                    generateMemberHierarchy(member.getMembers(), maxMembersCount);
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });
    }

    //generate random member data
    private static ArrayList<Member> generateRandomMembers() throws ParseException {
        int min = 1;
        int max = 5;
        ArrayList<Member> randomMembers = new ArrayList<>();
        int random = (int) (Math.random()*(max-min+1)+min);
        for(int i = 0; i < random; i++) {
            randomMembers.add(fakeMemberGenerator());
        }

        return randomMembers;
    }

    //generate the first level members randomly
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

    /**
     * Generate fake member info
     * @return fake member
     * @throws ParseException
     */
    private static Member fakeMemberGenerator() throws ParseException {
        Member member = new Member();
        Faker faker = new Faker();
        member.setName(faker.name().fullName());
        member.setBirthYear(getRandomDateString(faker, getInitialBirthSeedDate(), getLastBirthSeedDate()));
        member.setDeathYear(getRandomDateString(faker, getSecndaryBirthSeedDate(), getSecondaryLastBirthSeedDate()));
        return member;
    }

    //Start Date range for birth year of random data
    private static Date getInitialBirthSeedDate() throws ParseException {
        String date_string = "01-01-2000";
        //Instantiating the SimpleDateFormat class
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        //Parsing the given String to Date object
        return formatter.parse(date_string);
    }

    //End Date range for birth year of random data
    private static Date getLastBirthSeedDate() throws ParseException {
        String date_string = "01-01-2030";
        //Instantiating the SimpleDateFormat class
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        //Parsing the given String to Date object
        return formatter.parse(date_string);
    }

    //Second start Date range for birth year of random data
    private static Date getSecndaryBirthSeedDate() throws ParseException {
        String date_string = "01-01-2031";
        //Instantiating the SimpleDateFormat class
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        //Parsing the given String to Date object
        return formatter.parse(date_string);
    }

    //Second end Date range for birth year of random data
    private static Date getSecondaryLastBirthSeedDate() throws ParseException {
        String date_string = "01-01-2060";
        //Instantiating the SimpleDateFormat class
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        //Parsing the given String to Date object
        return formatter.parse(date_string);
    }

    //generate a random year
    private static String getRandomDateString(Faker faker, Date initialDate, Date finalDate) {
        Date randomDate = faker.date().between(initialDate, finalDate);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
        return formatter.format(randomDate);

    }

}
