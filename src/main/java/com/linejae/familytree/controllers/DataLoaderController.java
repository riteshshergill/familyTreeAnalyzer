package com.linejae.familytree.controllers;

import com.gcache.graph.GraphUtil;
import com.gcache.graph.model.Node;
import com.linejae.familytree.Utils.FileUtils;
import com.linejae.familytree.Utils.MockDataGeneratorUtil;
import com.linejae.familytree.models.Member;
import com.linejae.familytree.models.Root;
import com.linejae.familytree.services.CacheManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller to load all members and cache their relationshsips
 */
@RestController
public class DataLoaderController {

    @Autowired
    private CacheManagerService cacheManagerService;

    /**
     *
     * @param fileName File from which to load the data
     * @return The Root object containing the hierarchy
     * @throws Exception
     */
    @GetMapping("/familyTree/loadData/{fileName}")
    public Root loadFamilyTreeData(@PathVariable String fileName) throws Exception {
        FileUtils fUtils = new FileUtils();
        Root lineageData = fUtils.loadJsonFile(fileName);

        //caching all relationships in the graph cache
        if(lineageData.getLineage() != null) {
            Node startingNode = new Node(lineageData.getLineage().getFamilyTree(), lineageData.getLineage().getFamilyTree());

            //add the root to the graph first then send its children for addition
            //recursively
            for(Member rootchildren: lineageData.getLineage().getMembers()) {
                Node rootchild = new Node(rootchildren.toString(), rootchildren);
                cacheManagerService.getGraph().addRelationship(startingNode, rootchild);
            }
            cacheManagerService.setRootNode(startingNode);
            //Add the depth for the tree even as we construct it
            cacheManagerService.setDepth(cacheManagerService.getDepth() + 1);
            //send the children to be added to the graph recursively
            cacheChildren(lineageData.getLineage().getMembers(), cacheManagerService.getGraph(), startingNode);

        }
        return lineageData;

    }

    @GetMapping("/generateMockData")
    public void generateMockData() {
        try {
            MockDataGeneratorUtil.generateMockData();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //this method will add all the nodes to a JGrapht directed graph
    private void cacheChildren(ArrayList<Member> members, GraphUtil graphUtil, Node parent) {
         if(members == null) {
             return;
         }
        members.forEach((memberObject -> {

            if(memberObject.getMembers() != null) {
                cacheManagerService.setDepth(cacheManagerService.getDepth() + 1);
                for(Member memberChild: memberObject.getMembers()) {
                    Node parentNode = new Node(memberObject.toString(), memberObject, parent);
                    Node memberNode = new Node(memberChild.toString(), memberChild, parentNode);
                    graphUtil.addRelationship(parentNode, memberNode);
                    cacheChildren(memberChild.getMembers(), graphUtil, memberNode);
                }
            } else {
                Node leafNode = new Node(memberObject.toString(), memberObject, parent);
                graphUtil.addRelationship(leafNode, null);
                cacheManagerService.getLeafNodes().add(leafNode);
            }
        }));
    }
}
