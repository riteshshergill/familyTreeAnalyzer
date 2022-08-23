# familyTreeAnalyzer
### Analyze a family tree in a btree and trace the lienage

#### This is a Spring Boot project that offers a variety of utility methods for Graph Traversal on a family tree structure.

Running the project:

This is a Spring Boot project so you can either startup using <br/>

--mvn spring-boot:run 

--Or run as a spring boot app from intellij. 
#### Project has been created using Spring initializer.

##### Following list of requirements have been covered in this project:

1. Evaluate the BTree input and identify nodes that are invalid and print the reason for validation failure. <br/>
   a. Ignoring the invalid nodes <br/>
   b. Print individual family lines and mark the family line that was shortest and longest. <br/>
   c. Print all family members and their age <br/>
   d. Order age of family members in ascending order <br/>
   e. Find the range of period this lineage was active - ie., first ancestor birth year to the death year last member. <br/>
   f. Find mean age for this lineage <br/>
   g. Find the median age for this lineage <br/>
   h. Group and print middle 50% of members (name and age) of this lineage using IQR (Interquartile Range) <br/>
   i. Who lived longest (name and age) in this lineage?  Who died the youngest (name and age)? <br/>
2. Bonus points: <br/>
   a. Test data generator code to generate random lineage json generator <br/>
   b. Instead of a single file as input, take directory of lineage json files as input and generate output files for each json. <br/>
   c. Design a "worker thread pool class" with a preconfigured number of worker threads.  Then, utilize this worker thread pool class to process familytree*.json files from a directory.  Test with 20 familytree*.json samples and use threat pool size of 1, 2 and 5. <br/>

##### Following rest endpoints have been exposed in this project:

**DataGenerationController**--><br/>

_/familyTree/generateData/{threadPoolSize}_ ---> Assuming there are already some family tree files in src/main/resources, it will scan those files and load all the lineages, then generate reports for the lineages in parallel with a thread pool of the provided size

**DataLoaderController**--><br/>

_/familyTree/loadData/{fileName}_ ---> Assuming the file is already present in src/main/resources load the family tree for it

_/generateMockData/{numFiles}_ ---> Generate numFiles random family trees and put in the src/main/resources folder

**FamilyTreeEvaluationController**--><br/>

_/familyTree/findLongestShortest_ ---> Print the entire family lineage but also mark the shortest lineage and longest lineage with a Longest or Shortest marker at the end of the lineage output String

_/familyTree/findLongestShortest/{familyTreeName}_ ---> Print lineage for a specific family tree

_/familyTree/printSorted/{sortOrder}_ ---> Print the lineage in sorted order of ascending or descending age, ASC for ascending order and DESC for descending order, default order is ASC

_/familyTree/printSorted/{familyTreeName}/{sortOrder}_ ---> Sort for a specific lineage

_/familyTree/lineageRange_ ---> Find the range of period this lineage was active

_/familyTree/lineageRange/{familyTreeName}_ ---> range of period for a specific lineage

_/familyTree/meanAge_ ---> Find mean age for the loaded lineage

_/familyTree/meanAge/{familyTreeName}_ ---> Find mean age for the given lineage

_/familyTree/getMedianAge_ ---> Find the median age for the loaded lineage

_/familyTree/getMedianAge/{familyTreeName}_ ---> Get median age for the provided lineage

_/familyTree/getInterQuartileAge_ ---> Group and print middle 50% of members (name and age) of this lineage

_/familyTree/getInterQuartileAge/{familyTreeName}_ ---> Do the same for the provided lineage

_/familyTree/getLongestShortestLiving_ ---> Who lived longest (name and age) in this lineage?  Who died the youngest (name and age)?

_/familyTree/getLongestShortestLiving/{familyTreeName}_ ---> Above criteria for a specific lineage

The endpoints in this controller can be invoked with the following assumptions

1. If /familyTree/loadData/{fileName} has been invoked then call the endpoints without the family tree name
2. If /familyTree/loadMultiple/{fileNames} has been invoked then call the endpoints with a provided family tree name

**MultiLoadController** --->

_/familyTree/loadMultiple/{fileNames}_ ---> Load family tree data for multiple files assuming they are present in the src/main/resources directory

Sample --- /familyTree/loadMultiple/file1.json,file2.json,file3.json

Then invoke the reporting endpoints with the specific family tree name