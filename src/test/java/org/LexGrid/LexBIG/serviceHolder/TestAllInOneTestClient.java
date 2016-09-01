package org.LexGrid.LexBIG.serviceHolder;
import static org.junit.Assert.*;

import org.LexGrid.LexBIG.DataModel.Collections.AssociationList;
import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeRenderingList;
import org.LexGrid.LexBIG.DataModel.Collections.LocalNameList;
import org.LexGrid.LexBIG.DataModel.Collections.ResolvedConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Core.AssociatedConcept;
import org.LexGrid.LexBIG.DataModel.Core.Association;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Exceptions.LBInvocationException;
import org.LexGrid.LexBIG.Extensions.Generic.SearchExtension;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeGraph;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.LexBIG.Utility.ConvenienceMethods;
import org.LexGrid.commonTypes.EntityDescription;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;

//@FixMethodOrder(MethodSorters.JVM)
public class TestAllInOneTestClient {
	
	String THES_SCHEME = "Thesaurus";
	static LexBIGService lbs;
	SearchExtension searchExtension;
	
	@BeforeClass
	public static void setUp(){
		lbs = (LexBIGService)LexEVSServiceHolder.instance().getLexEVSAppService();
	}
	
	@Test
	public void testListSchemes() throws LBInvocationException {
		CodingSchemeRenderingList csrl = lbs.getSupportedCodingSchemes();
		
		assertTrue(csrl.getCodingSchemeRenderingCount() > 0);
		
		System.out.println("No. of Coding Schemes: " + csrl.getCodingSchemeRenderingCount());
		for(int i = 0; i < csrl.getCodingSchemeRenderingCount(); i++)
		{
			System.out.println(csrl.getCodingSchemeRendering(i).getCodingSchemeSummary().getLocalName());
			System.out.println(csrl.getCodingSchemeRendering(i).getCodingSchemeSummary().getRepresentsVersion());
			System.out.println(csrl.getCodingSchemeRendering(i).getCodingSchemeSummary().getCodingSchemeURI());
			System.out.println("********************");
		}
	}
	
	@Test
	public void testListFirstTenEntities() throws LBException{
		CodedNodeSet set = lbs.getCodingSchemeConcepts(THES_SCHEME, null);
		ResolvedConceptReferenceList rcrl = set.resolveToList(null, null, null, 10);
		assertTrue(rcrl.getResolvedConceptReferenceCount() > 0);
		System.out.println();
		for(ResolvedConceptReference ref: rcrl.getResolvedConceptReference()){
			System.out.println("Term: " + ref.getEntityDescription().getContent());
			System.out.println("********************");
		}
		
		System.out.println();
		}
	
	@Test
	public void testGraphResolutionRandomTerm() throws LBException{

		CodedNodeGraph graph = lbs.getNodeGraph(THES_SCHEME, null, null);

		ResolvedConceptReferenceList leaves = graph.resolveAsList(Constructors.createConceptReference("C12434", THES_SCHEME), true, false, 0, -1, null, null, null, -1);
		assertTrue(leaves.getResolvedConceptReferenceCount() > 0);
		for(ResolvedConceptReference ref : leaves.getResolvedConceptReference()){
			System.out.println("Term: " + ref.getEntityDescription().getContent());
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

}
