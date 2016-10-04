package org.LexGrid.LexBIG.serviceHolder;

import java.util.Vector;

import org.LexGrid.LexBIG.DataModel.Collections.LocalNameList;
import org.LexGrid.LexBIG.DataModel.Collections.NameAndValueList;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.DataModel.Core.NameAndValue;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;

public class SourceCodeFromMetaTest {
	String MTHES_SCHEME = "NCI MetaThesaurus";
	LexBIGService lbs;


	public void run(){
		try {
			lbs = (LexBIGService)LexEVSServiceHolder.instance().getLexEVSAppService();
			findConceptWithSourceCodeMatching(MTHES_SCHEME, null, "SRC", "V-NCI");
		} catch (LBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void findConceptWithSourceCodeMatching(
			String scheme, String version, String sourceAbbr, String code) throws LBException {
			CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
			versionOrTag.setVersion(version);
			
//			NameAndValueList qualifierList = new NameAndValueList(); 
//			NameAndValue nv = new NameAndValue(); 
//			nv.setName("source-code");
//			nv.setContent(code); 
//			qualifierList.addNameAndValue(nv);
			
//			NameAndValue sv = new NameAndValue(); 
//			sv.setContent(sourceAbbr); 
//			qualifierList.addNameAndValue(sv);
			
			LocalNameList sources = new LocalNameList();
			sources.addEntry(sourceAbbr);

			CodedNodeSet cns = lbs.getNodeSet(scheme, null, null);

			CodedNodeSet.PropertyType[] types =
			new CodedNodeSet.PropertyType[]
			{ CodedNodeSet.PropertyType.PRESENTATION};

			cns =
			cns.restrictToProperties(null, types, null, sources, null);

			ResolvedConceptReferencesIterator itr = cns.resolve(null, null, null);
			System.out.println(itr.hasNext());
			while(itr.hasNext()){
				ResolvedConceptReference ref = itr.next();
				System.out.println(ref.getEntity().getEntityCode());
				System.out.println(ref.getEntityDescription().getContent());
				System.out.println(ref.getEntity());
			}

	}
	

	public static void main(String[] args) {
		new SourceCodeFromMetaTest().run();
	}

}
