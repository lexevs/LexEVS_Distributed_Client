/*******************************************************************************
 * Copyright: (c) 2004-2009 Mayo Foundation for Medical Education and 
 * Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
 * triple-shield Mayo logo are trademarks and service marks of MFMER.
 * 
 * Except as contained in the copyright notice above, or as used to identify 
 * MFMER as the author of this software, the trade names, trademarks, service
 * marks, or product names of the copyright holder shall not be used in
 * advertising, promotion or otherwise in connection with this software without
 * prior written authorization of the copyright holder.
 *   
 * Licensed under the Eclipse Public License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 *   
 *  		http://www.eclipse.org/legal/epl-v10.html
 * 
 *  		
 *******************************************************************************/
package org.LexGrid.LexBIG.serviceHolder;

import java.util.Arrays;
import java.util.List;

import org.LexGrid.LexBIG.DataModel.Collections.AssociationList;
import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeRenderingList;
import org.LexGrid.LexBIG.DataModel.Collections.LocalNameList;
import org.LexGrid.LexBIG.DataModel.Collections.ResolvedConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Core.AssociatedConcept;
import org.LexGrid.LexBIG.DataModel.Core.Association;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension.Mapping;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension.Mapping.SearchContext;
import org.LexGrid.LexBIG.Extensions.Generic.SearchExtension;
import org.LexGrid.LexBIG.Extensions.Generic.SearchExtension.MatchAlgorithm;
import org.LexGrid.LexBIG.Impl.Extensions.tree.model.LexEvsTree;
import org.LexGrid.LexBIG.Impl.Extensions.tree.model.LexEvsTreeNode;
import org.LexGrid.LexBIG.Impl.Extensions.tree.service.TreeService;
import org.LexGrid.LexBIG.Impl.Extensions.tree.service.TreeServiceFactory;
import org.LexGrid.LexBIG.Impl.Extensions.tree.utility.PrintUtility;
//import org.lexevs.tree.model.LexEvsTree;
//import org.lexevs.tree.model.LexEvsTreeNode;
//import org.lexevs.tree.service.TreeService;
//import org.lexevs.tree.service.TreeServiceFactory;
//import org.lexevs.tree.utility.PrintUtility;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet.SearchDesignationOption;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Utility.ConvenienceMethods;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;
import org.LexGrid.commonTypes.EntityDescription;
//import org.lexevs.tree.model.LexEvsTree;
//import org.lexevs.tree.model.LexEvsTreeNode;
//import org.lexevs.tree.service.TreeService;
//import org.lexevs.tree.utility.PrintUtility;

public class LexEVSExampleTest {
	String THES_SCHEME = "Thesaurus";
	LexBIGService lbs;
	SearchExtension searchExtension;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
	new LexEVSExampleTest().run();

	}
	
	public void run(){
		try {
			setUp();
//			testTree();
//			testMappingExtension();
			testGetSupportedCodingSchemes();
			testGetCodingSchemeConcepts();
			testGetCodingSchemeGraph();
			testSimpleSearchExtensionLucene();
			testSimpleSearchExtensionContains();
//			testSimpleSearchExtensionMultiWord();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	
	public void setUp(){
		lbs = (LexBIGService)LexEVSServiceHolder.instance().getLexEVSAppService();
	}

	public void testTree() throws LBException{
		TreeService service = TreeServiceFactory.getInstance().getTreeService(lbs);
 //       TreeService service = (TreeService)lbs.getGenericExtension("lex-tree-utility");;
        LexEvsTree tree = null;
        CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
        csvt.setVersion("April2015");
//         tree = service.getTree("npo", csvt, "NPO_1607", "npo", "is_a");
         tree = service.getTree("OBI", csvt, "BFO_0000002","obo");
//         tree = service.getTree("npo", csvt, "NPO_1607");
            LexEvsTreeNode focusNode = tree.getCurrentFocus();
            List<LexEvsTreeNode> nodeList = service.getEvsTreeConverter().buildEvsTreePathFromRootTree(focusNode);
            assert(nodeList.size() > 0);
            PrintUtility.print(nodeList);
            PrintUtility.print(focusNode);
            String jsonString =
            		service.getJsonConverter().buildJsonPathFromRootTree(focusNode);
            System.out.println(jsonString);
	}
	
	public void testMappingExtension(){
		MappingExtension mappingExtension = null;
		try {
			mappingExtension = (MappingExtension)lbs.getGenericExtension("MappingExtension");
		} catch (LBException e) {
			e.printStackTrace();
		}
		CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
		csvt.setVersion("1.2");
		Mapping mapping = null;
		try {
			mapping = mappingExtension.getMapping("NCIt_to_ChEBI_Mapping", csvt, "NCIt_to_ChEBI_Mapping");
		} catch (LBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

			if (mapping != null) {
				try {
					mapping = mapping.restrictToMatchingDesignations(
								"Warfarin", SearchDesignationOption.ALL, "LuceneQuery", null, SearchContext.SOURCE_OR_TARGET_CODES);
					ResolvedConceptReferencesIterator itr = mapping.resolveMapping();

					while(itr.hasNext()){
						ResolvedConceptReference rcr = itr.next();
						System.out.println(rcr.getEntityDescription().getContent() + " : " 
						+ rcr.getSourceOf().getAssociation(0).getAssociatedConcepts().getAssociatedConcept(0).getCode());
					   break;
				
					}
				} catch (LBException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	
			}
	}
			
	public void testGetSupportedCodingSchemes() throws Exception{
		CodingSchemeRenderingList csrl = lbs.getSupportedCodingSchemes();
		System.out.println("*****************************************************************");
		System.out.println("PRINTING CODING SCHEMES FROM LEXEVS 6.2 PRODUCTION SERVER");
		System.out.println("*****************************************************************");
		System.out.println("*");
		System.out.println("*");
		System.out.println("*");
		System.out.println("*");
		System.out.println("********************");
		for(int i = 0; i < csrl.getCodingSchemeRenderingCount(); i++)
		{
			System.out.println(csrl.getCodingSchemeRendering(i).getCodingSchemeSummary().getLocalName());
			System.out.println(csrl.getCodingSchemeRendering(i).getCodingSchemeSummary().getRepresentsVersion());
			System.out.println(csrl.getCodingSchemeRendering(i).getCodingSchemeSummary().getCodingSchemeURI());
			System.out.println("********************");
			
			//get a version of the NCI Thesaurus on the server
			
			if(csrl.getCodingSchemeRendering(i).getCodingSchemeSummary().getFormalName().equals(THES_SCHEME) ){
				if(csrl.getCodingSchemeRendering(i).getRenderingDetail().getVersionTags().getTagCount() > 0){}
//				{THES_VERSION = csrl.getCodingSchemeRendering(i).getCodingSchemeSummary().getRepresentsVersion();}
			}
		}
	}
	
	public void testGetCodingSchemeConcepts() throws Exception{
//		CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
//		csvt.setVersion(THES_VERSION);
		CodedNodeSet cns = lbs.getCodingSchemeConcepts(THES_SCHEME, null);
		cns = cns.restrictToMatchingDesignations("blood", SearchDesignationOption.PREFERRED_ONLY, "LuceneQuery", null);
		ResolvedConceptReferenceList  rcrl = cns.resolveToList(null, null, null, 10);
		System.out.println("*");
		System.out.println("*");
		System.out.println("*");
		System.out.println("*");
		System.out.println("*");
		System.out.println("FOR A SEARCH ON THE TEXT \"Blood\" PRINTING 1st CONCEPT IN THE LIST");
		System.out.println("********************************************************************");
		System.out.print("Concept Desigation: ");
		System.out.println(rcrl.getResolvedConceptReference(0).getEntityDescription().getContent());
		System.out.print("Concept Unique Code: ");
		System.out.println(rcrl.getResolvedConceptReference(0).getConceptCode());
		System.out.println(rcrl.getResolvedConceptReference(0).getEntity().getDefinition(0).getValue().getContent());
	}
	
	public void testSimpleSearchExtensionLucene() throws Exception{
		searchExtension = (SearchExtension)lbs.getGenericExtension("SearchExtension");
		ResolvedConceptReferencesIterator itr = searchExtension.search("blood", MatchAlgorithm.LUCENE);
		System.out.println("*");
		System.out.println("*");
		System.out.println("*");
		System.out.println("*");
		System.out.println("*");
		System.out.println("********************************************************************");
		System.out.println("FOR A GLOBAL SEARCH ON THE TEXT \"boxing\" PRINTING All Designations");
		System.out.println("********************************************************************");
		while(itr.hasNext()){
		System.out.println(itr.next().getEntityDescription().getContent());
		}
	}
	
	public void testSimpleSearchExtensionContains() throws Exception{
		searchExtension = (SearchExtension)lbs.getGenericExtension("SearchExtension");
		ResolvedConceptReferencesIterator itr = searchExtension.search("genome", MatchAlgorithm.PRESENTATION_CONTAINS);
		System.out.println("*");
		System.out.println("*");
		System.out.println("*");
		System.out.println("*");
		System.out.println("*");
		System.out.println("********************************************************************");
		System.out.println("FOR A GlOBAL SEARCH ON THE TEXT \"genome\" PRINTING 1st 20 Designations");
		System.out.println("********************************************************************");
		int count = 0;
		while(itr.hasNext() && count < 20){
		System.out.println(itr.next().getEntityDescription().getContent());
		count++;
		}
	}
	
	public void testSimpleSearchExtensionMultiWord()throws Exception{
		searchExtension = (SearchExtension)lbs.getGenericExtension("SearchExtension");
		System.out.println("*");
		System.out.println("*");
		System.out.println("*");
		System.out.println("*");
		System.out.println("*");
		System.out.println("*********************************************************************************");
		System.out.println("FOR A GlOBAL SEARCH ON THE TEXTS \"single cell\", \"cancer\", \"cure\"");
		System.out.println("*********************************************************************************");
		System.out.println("********************************************************************");
		System.out.println("PRINTING 1st DESIGNATION FOR EACH");
		System.out.println("********************************************************************");
		for(String term : Arrays.asList("single cell", "cancer", "cure")){
			ResolvedConceptReferencesIterator itr = searchExtension.search(term, MatchAlgorithm.LUCENE);
			System.out.println(itr.next().getEntityDescription().getContent());
		}
	}
	
	public void testGetCodingSchemeGraph() throws Exception{
//		CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
//		csvt.setVersion(THES_VERSION);
		System.out.println("*");
		System.out.println("*");
		System.out.println("*");
		System.out.println("*");
		System.out.println("*");
		System.out.println("***********************************************");
		System.out.println("PRINTING GRAPH NODES FOR DESIGNATION \"Blood\"");
		System.out.println("***********************************************");
		
		printTo("C12434",null, lbs, THES_SCHEME, null);
		printFrom("C12434",null, lbs, THES_SCHEME, null);
	}
	

	/**
     * Display relations to the given code from other concepts.
     * 
     * @param code
     * @param relation
     * @param lbSvc
     * @param scheme
     * @param csvt
     * @throws LBException
     */
    protected void printTo(String code, String relation, LexBIGService lbSvc, String scheme,
            CodingSchemeVersionOrTag csvt) throws LBException {
        System.out.println("Points to ...");

        ResolvedConceptReferenceList matches = lbSvc.getNodeGraph(scheme, csvt, null).resolveAsList(ConvenienceMethods.createConceptReference(code, scheme), true, false, 1, 1,
                new LocalNameList(), null, null, 1024);

        // Analyze the result ...
        if (matches.getResolvedConceptReferenceCount() > 0) {
            ResolvedConceptReference ref = (ResolvedConceptReference) matches.enumerateResolvedConceptReference()
                    .nextElement();

            // Print the associations
            AssociationList sourceof = ref.getSourceOf();
            Association[] associations = sourceof.getAssociation();
            for (int i = 0; i < associations.length; i++) {
                Association assoc = associations[i];
                AssociatedConcept[] acl = assoc.getAssociatedConcepts().getAssociatedConcept();
                for (int j = 0; j < acl.length; j++) {
                    AssociatedConcept ac = acl[j];
                    EntityDescription ed = ac.getEntityDescription();
                    System.out.println("\t\t" + ac.getConceptCode() + "/"
                            + (ed == null ? "**No Description**" : ed.getContent()));
                }
            }
        }
    }
    
    protected void printFrom(String code, String relation, LexBIGService lbSvc, String scheme,
            CodingSchemeVersionOrTag csvt) throws LBException {
        // Perform the query ...
        System.out.println("Pointed at by ...");
        ResolvedConceptReferenceList matches = lbSvc.getNodeGraph(scheme, csvt, null).resolveAsList(ConvenienceMethods.createConceptReference(code, scheme), false, true, 1, 1,
                new LocalNameList(), null, null, 1024);

        // Analyze the result ...
        if (matches.getResolvedConceptReferenceCount() > 0) {
            ResolvedConceptReference ref = (ResolvedConceptReference) matches.enumerateResolvedConceptReference()
                    .nextElement();

            // Print the associations
            AssociationList targetof = ref.getTargetOf();
            Association[] associations = targetof.getAssociation();
            for (int i = 0; i < associations.length; i++) {
                Association assoc = associations[i];
                AssociatedConcept[] acl = assoc.getAssociatedConcepts().getAssociatedConcept();
                for (int j = 0; j < acl.length; j++) {
                    AssociatedConcept ac = acl[j];
                    EntityDescription ed = ac.getEntityDescription();
                    System.out.println("\t\t" + ac.getConceptCode() + "/"
                            + (ed == null ? "**No Description**" : ed.getContent()));
                }
            }
        }

    }	
}
