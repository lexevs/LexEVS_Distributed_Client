package org.LexGrid.LexBIG.serviceHolder;
import static org.junit.Assert.*;

import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeRenderingList;
import org.LexGrid.LexBIG.DataModel.Collections.ResolvedConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Exceptions.LBInvocationException;
import org.LexGrid.LexBIG.Extensions.Generic.SearchExtension;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.JVM)
public class TestAllInOneTestClient {
	
	String THES_SCHEME = "Thesaurus";
	LexBIGService lbs;
	SearchExtension searchExtension;
	
	@Before
	public void setUp(){
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
		
		for(ResolvedConceptReference ref: rcrl.getResolvedConceptReference()){
			System.out.println("Term: " + ref.getEntityDescription().getContent());
			System.out.println("********************");
		}
		
		System.out.println();
		}

}
