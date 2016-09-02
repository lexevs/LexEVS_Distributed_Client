package org.LexGrid.LexBIG.serviceHolder;

/**
 * SimpleSearchUtils (uses LexEVSAPI 6.1 SearchExtension)
 *
 * @author kimong
 *
 */

import java.util.*;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.log4j.*;

import org.LexGrid.LexBIG.DataModel.Core.AbsoluteCodingSchemeVersionReference;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Extensions.Generic.CodingSchemeReference;
import org.LexGrid.LexBIG.Extensions.Generic.SearchExtension;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;

import org.LexGrid.LexBIG.DataModel.Collections.*;
import org.LexGrid.LexBIG.DataModel.Core.*;
import org.LexGrid.LexBIG.LexBIGService.*;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet.*;
import org.LexGrid.LexBIG.Utility.*;
import org.LexGrid.concepts.*;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.*;
import org.LexGrid.LexBIG.Utility.Iterators.*;
import org.LexGrid.codingSchemes.*;
import org.apache.log4j.*;

import org.LexGrid.LexBIG.DataModel.Core.types.*;
import org.LexGrid.naming.*;
import org.LexGrid.LexBIG.Extensions.Generic.*;

import org.apache.commons.codec.language.*;
import org.LexGrid.LexBIG.Extensions.Generic.SearchExtension;
import org.LexGrid.LexBIG.caCore.interfaces.LexEVSDistributed;

import gov.nih.nci.evs.security.SecurityToken;
import gov.nih.nci.system.client.ApplicationServiceProvider;



public class SimpleSearchUtils {
	private static String serviceUrl = "http://localhost:8080/lexevsapi64";

	private static Logger _logger = Logger.getLogger(SimpleSearchUtils.class);

    final static String testID = "SimpleSearchUtils";

    public static final int BY_CODE = 1;
    public static final int BY_NAME = 2;

    public static final String EXACT_MATCH = "exactMatch";
    public static final String STARTS_WITH = "startsWith";
    public static final String CONTAINS = "contains";
    public static final String LUCENE = "lucene";

    public static final String NAMES = "names";
    public static final String CODES = "codes";
    public static final String PROPERTIES = "properties";
    public static final String RELATIONSHIPS = "relationships";

    public SimpleSearchUtils() {

	}

    public ResolvedConceptReferencesIterator search(
        Vector<String> schemes, Vector<String> versions, String matchText, String algorithm, String target) throws LBException {
        if (algorithm == null|| target == null) return null;

        if (algorithm.compareToIgnoreCase(EXACT_MATCH) == 0 && target.compareToIgnoreCase(CODES) == 0) {
			return search(schemes, versions, matchText, BY_CODE, "exactMatch");
        } else if (algorithm.compareToIgnoreCase(LUCENE) == 0 && target.compareToIgnoreCase(CODES) == 0) {
			return search(schemes, versions, matchText, BY_CODE, "exactMatch");
        } else if (algorithm.compareToIgnoreCase(LUCENE) == 0 && target.compareToIgnoreCase(NAMES) == 0) {
			return search(schemes, versions, matchText, BY_NAME, "lucene");
        } else if (algorithm.compareToIgnoreCase(CONTAINS) == 0 && target.compareToIgnoreCase(NAMES) == 0) {
			return search(schemes, versions, matchText, BY_NAME, "contains");
		}
		return null;
	}

    public CodedNodeSet getNodeSet(LexBIGService lbSvc, String scheme, CodingSchemeVersionOrTag versionOrTag)
        throws Exception {
		CodedNodeSet cns = null;
		try {
			cns = lbSvc.getCodingSchemeConcepts(scheme, versionOrTag);
			CodedNodeSet.AnonymousOption restrictToAnonymous = CodedNodeSet.AnonymousOption.NON_ANONYMOUS_ONLY;
			cns = cns.restrictToAnonymous(restrictToAnonymous);
	    } catch (Exception ex) {
			ex.printStackTrace();
		}

		return cns;
	}

/*
	public boolean isSimpleSearchSupported(String algorithm, String target) {
		if (algorithm == null|| target == null) return false;

        if (algorithm.compareToIgnoreCase(EXACT_MATCH) == 0 && target.compareToIgnoreCase(CODES) == 0) {
			return true;
        } else if (algorithm.compareToIgnoreCase(EXACT_MATCH) == 0 && target.compareToIgnoreCase(NAMES) == 0) {
			return true;
        } else if (algorithm.compareToIgnoreCase(LUCENE) == 0 && target.compareToIgnoreCase(CODES) == 0) {
			return true;
        } else if (algorithm.compareToIgnoreCase(LUCENE) == 0 && target.compareToIgnoreCase(NAMES) == 0) {
			return true;
        } else if (algorithm.compareToIgnoreCase(CONTAINS) == 0 && target.compareToIgnoreCase(NAMES) == 0) {
			return true;
		}
		return false;
	}
*/

    public boolean isSearchExtensionAvaliable() {
		if (getSearchExtension() == null) return false;
		return true;
	}


    public SearchExtension getSearchExtension() {
		SearchExtension searchExtension = null;

		try {
			LexBIGService lbSvc = getSecuredLexBIGService();
			if (lbSvc == null) {
				_logger.warn("createLexBIGService returns NULL???");
				return null;
			}
			searchExtension = (SearchExtension) lbSvc.getGenericExtension("SearchExtension");
			return searchExtension;
		} catch (Exception e){
			_logger.warn("SearchExtension is not available.");
			return null;
		}
	}


    public ResolvedConceptReferencesIterator search(
        String scheme, String version, String matchText, int searchOption, String algorithm) throws LBException {
		if (scheme == null) return null;
		Vector<String> schemes = new Vector();
		Vector<String> versions = new Vector();
		schemes.add(scheme);
		versions.add(version);
		return search(schemes, versions, matchText, searchOption, algorithm);
    }


    private void printNumberOfMatches(ResolvedConceptReferencesIterator iterator) {
		if (iterator == null) {
			return;
		}
		try {
			int numRemaining = iterator.numberRemaining();
			System.out.println("Number of matches: " + numRemaining);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}


    public ResolvedConceptReferencesIterator search(
        Vector<String> schemes, Vector<String> versions, String matchText, int searchOption, String algorithm) throws LBException {

	    if (schemes == null|| versions == null) return null;
	    if (schemes.size() != versions.size()) return null;
	    if (schemes.size() == 0) return null;
	    if (matchText == null) return null;
	    if (searchOption != BY_CODE && searchOption != BY_NAME) return null;
	    if (searchOption != BY_CODE && algorithm == null) return null;

		LexBIGService lbSvc = getSecuredLexBIGService();

		if (lbSvc == null) {
			return null;
		}

		SearchExtension searchExtension = null;
		try {
			searchExtension = (SearchExtension) lbSvc.getGenericExtension("SearchExtension");
		} catch (Exception e){
			_logger.warn("SearchExtension is not available.");
			return null;
		}

        Set<CodingSchemeReference> includes = new HashSet();

        for (int i=0; i<schemes.size(); i++) {
			String scheme = (String) schemes.elementAt(i);
			String version = (String) versions.elementAt(i);
			CodingSchemeReference ref = new CodingSchemeReference();
			ref.setCodingScheme(scheme);

			if (version != null) {
				CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
				System.out.println("scheme: " + scheme + " (version: " + version + ")");
				versionOrTag.setVersion(version);
				ref.setVersionOrTag(versionOrTag);
		    }
			includes.add(ref);
		}

		ResolvedConceptReferencesIterator iterator = null;
		try {
			iterator = searchExtension.search(matchText, includes, null, converToMatchAlgorithm(searchOption, algorithm), false, true);
			printNumberOfMatches(iterator);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return iterator;
	}


    protected void displayRef(ResolvedConceptReference ref) {
        System.out.println(ref.getConceptCode() + ":" + ref.getEntityDescription().getContent());
    }

    public void dumpIterator(ResolvedConceptReferencesIterator itr) {
        try {
            while (itr.hasNext()) {
                try {
                    ResolvedConceptReference[] refs =
                        itr.next(100).getResolvedConceptReference();
                    for (ResolvedConceptReference ref : refs) {
                        displayRef(ref);
                    }
                } catch (Exception ex) {
                    break;
                }
            }
        } catch (Exception ex) {
			ex.printStackTrace();
        }
    }


	public SearchExtension.MatchAlgorithm converToMatchAlgorithm(int searchOption, String algorithm) {
		if (algorithm == null) return null;
	    if (searchOption != BY_CODE && searchOption != BY_NAME) return null;
	    if (searchOption == BY_NAME) {
			if (algorithm.compareTo("exactMatch") == 0) {
				return SearchExtension.MatchAlgorithm.PRESENTATION_EXACT;
			} else if (algorithm.compareTo("contains") == 0) {
				return SearchExtension.MatchAlgorithm.PRESENTATION_CONTAINS;
			} else if (algorithm.compareTo("lucene") == 0) {
				return SearchExtension.MatchAlgorithm.LUCENE;
			} else { //if (algorithm.compareTo("lucene") == 0) {
				return SearchExtension.MatchAlgorithm.LUCENE;
			}

		} else if (algorithm.compareTo("exactMatch") == 0 && searchOption == BY_CODE) {
			return SearchExtension.MatchAlgorithm.CODE_EXACT;
		}
		return null;
	}


    public LexBIGService getSecuredLexBIGService() {
		SecurityToken token = new SecurityToken();
		token.setAccessToken("10382");

		LexEVSDistributed lbs = null;
		try {
			lbs = (LexEVSDistributed)ApplicationServiceProvider.getApplicationServiceFromUrl(serviceUrl, "EvsServiceInfo");
			lbs.registerSecurityToken("MedDRA (Medical Dictionary for Regulatory Activities Terminology)", token);
			lbs.registerSecurityToken("MedDRA", token);

		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return lbs;
	}


	public void run(String matchText) {
		System.out.println("\n=============================== " + matchText + " ===============================");
		ResolvedConceptReferencesIterator iterator = null;
        try {
			Vector<String> schemes = new Vector();
			Vector<String> versions = new Vector();
			schemes.add("NCI_Thesaurus");


			//String matchText = "Hyperreactio Luteinalis Variant -- with Corpora Lutea";
			System.out.println("EXACT MATCH: ");
			iterator = search(schemes, null, matchText, SimpleSearchUtils.BY_NAME, "contains");
			if (iterator != null) {
					try {
						int numRemaining = iterator.numberRemaining();
						System.out.println("Number of matches: " + numRemaining);
						dumpIterator(iterator);

					} catch (Exception ex) {
						ex.printStackTrace();
					}
			} else {
				System.out.println("iterator is NULL??? " + matchText);
			}

			System.out.println("CONTAINS: ");
			iterator = search(schemes, versions, matchText, SimpleSearchUtils.BY_NAME, "contains");
			if (iterator != null) {
					try {
						int numRemaining = iterator.numberRemaining();
						System.out.println("Number of matches: " + numRemaining);
						dumpIterator(iterator);

					} catch (Exception ex) {
						ex.printStackTrace();
					}
			} else {
				System.out.println("iterator is NULL??? " + matchText);
			}

			System.out.println("STARTS WITH: ");
			iterator = search(schemes, versions, matchText, SimpleSearchUtils.BY_NAME, "startsWith");
			if (iterator != null) {
					try {
						int numRemaining = iterator.numberRemaining();
						System.out.println("Number of matches: " + numRemaining);
						dumpIterator(iterator);

					} catch (Exception ex) {
						ex.printStackTrace();
					}

			} else {
				System.out.println("iterator is NULL??? " + matchText);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}


	public static void main(String [] args) {
		SimpleSearchUtils test = new SimpleSearchUtils();
		boolean searchExtensionAvaliable = test.isSearchExtensionAvaliable();
		if (!searchExtensionAvaliable) {
			System.out.println("SearchExtension is not available.");
			System.exit(1);
		}

		String matchText = "Hyperreactio Luteinalis Variant -- with Corpora Lutea";
		test.run(matchText);

		matchText = "Cell aging";
		test.run(matchText);

		matchText = "Gold Coast";
		test.run(matchText);

		matchText = "M A T101 HUMAN T-LYMPHOCYTE";
		test.run(matchText);

		matchText = "Epithelial Protein Up-Regulated in Carcinoma, Membrane Associated Protein 17 Gene";
		test.run(matchText);


	}


}

