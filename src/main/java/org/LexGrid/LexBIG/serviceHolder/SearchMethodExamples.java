package org.LexGrid.LexBIG.serviceHolder;
import gov.nih.nci.evs.security.SecurityToken;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Extensions.Generic.CodingSchemeReference;
import org.LexGrid.LexBIG.Extensions.Generic.SearchExtension;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;
import org.LexGrid.LexBIG.caCore.security.interfaces.TokenSecurableApplicationService;




/**
 * @author EVS Team
 * @version 1.0
 *
 *      Modification history Initial implementation kim.ong@ngc.com
 *
 */


/**
 * The Class TestSearchMethods.
 */


public class SearchMethodExamples {
    final static String testID = "TestSearchMethods";

    public final int BY_CODE = 1;
    public final int BY_NAME = 2;

    public final String EXACT_MATCH = "exactMatch";
    public final String STARTS_WITH = "startsWith";
    public final String CONTAINS = "contains";
    public final String LUCENE = "lucene";

    public final String NAMES = "names";
    public final String CODES = "codes";
    public final String PROPERTIES = "properties";
    public final String RELATIONSHIPS = "relationships";

	LexBIGService lbs;

	public void setUp(){
		lbs = getSecuredLexBIGService();
	}

    public LexBIGService getSecuredLexBIGService() {
		SecurityToken token = new SecurityToken();
		token.setAccessToken("10382");

		LexBIGService lbs = null;
		try {
			lbs = (LexBIGService)LexEVSServiceHolder.instance().getLexEVSAppService();
			((TokenSecurableApplicationService) lbs).registerSecurityToken("MedDRA (Medical Dictionary for Regulatory Activities Terminology)", token);
			((TokenSecurableApplicationService) lbs).registerSecurityToken("MedDRA", token);

		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return lbs;
	}


    protected String getTestID() {
        return testID;
    }

    public void testSearchMethods() throws Exception {

	    boolean bool_val = true;

	    String scheme = "NCI Metathesaurus";
	    String version = null;
	    String matchText = "Müllerian";
	    int searchOption = BY_NAME;
	    String algorithm = "contains";

	    System.out.println("Test case: " );
	    System.out.println("\tcoding scheme: " + scheme);
	    System.out.println("\tversion: " + version);
	    System.out.println("\tmatchText: " + matchText);
	    System.out.println("\tsearchOption: BY_NAME");
	    System.out.println("\talgorithm: " + algorithm);


        ResolvedConceptReferencesIteratorWrapper wrapper = null;
        try {
			wrapper = search(scheme, version, matchText, searchOption, algorithm);
			if (wrapper == null) {
				System.out.println("No results found.");
			} else {
				ResolvedConceptReferencesIterator iterator = wrapper.getIterator();
				try {
					int num = iterator.numberRemaining();
					System.out.println("Numbe of matches: " + num);
				} catch (Exception ex) {

				}
			}
		} catch (LBException ex) {

		}

	    scheme = "NCI Thesaurus";
	    version = "Diacrtitics";
	    matchText = "Waldenström";
	    searchOption = BY_NAME;
	    algorithm = "contains";

	    System.out.println("Test case: " );
	    System.out.println("\tcoding scheme: " + scheme);
	    System.out.println("\tversion: " + version);
	    System.out.println("\tmatchText: " + matchText);
	    System.out.println("\tsearchOption: BY_NAME");
	    System.out.println("\talgorithm: " + algorithm);


        try {
			wrapper = search(scheme, version, matchText, searchOption, algorithm);
			if (wrapper == null) {
				System.out.println("No results found.");
			} else {
				ResolvedConceptReferencesIterator iterator = wrapper.getIterator();
				try {
					int num = iterator.numberRemaining();
					System.out.println("Numbe of matches: " + num);
				} catch (Exception ex) {

				}
			}
		} catch (LBException ex) {

		}
    }


    public CodedNodeSet getNodeSet(LexBIGService lbs, String scheme, CodingSchemeVersionOrTag versionOrTag)
        throws Exception {
		CodedNodeSet cns = null;
		try {
			cns = lbs.getCodingSchemeConcepts(scheme, versionOrTag);
			CodedNodeSet.AnonymousOption restrictToAnonymous = CodedNodeSet.AnonymousOption.NON_ANONYMOUS_ONLY;
			cns = cns.restrictToAnonymous(restrictToAnonymous);
	    } catch (Exception ex) {
			ex.printStackTrace();
		}

		return cns;
	}


    public ResolvedConceptReferencesIteratorWrapper search(
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


    public boolean isSearchExtensionAvaliable() {
		if (getSearchExtension() == null) return false;
		return true;
	}


    public SearchExtension getSearchExtension() {
		SearchExtension searchExtension = null;
		try {
			searchExtension = (SearchExtension) lbs.getGenericExtension("SearchExtension");
			return searchExtension;
		} catch (Exception e){
			//_logger.warn("SearchExtension is not available.");
			return null;
		}
	}



    public boolean searchAllSources(String source) {
		if (source != null && source.compareTo("ALL") != 0) return false;
		return true;
	}


    public ResolvedConceptReferencesIteratorWrapper search(
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


    public ResolvedConceptReferencesIteratorWrapper search(
        Vector<String> schemes, Vector<String> versions, String matchText, int searchOption, String algorithm) throws LBException {

	    if (schemes == null|| versions == null) return null;
	    if (schemes.size() != versions.size()) return null;
	    if (schemes.size() == 0) return null;
	    if (matchText == null) return null;
	    if (searchOption != BY_CODE && searchOption != BY_NAME) return null;
	    if (searchOption != BY_CODE && algorithm == null) return null;

		SearchExtension searchExtension = null;
		try {
			searchExtension = (SearchExtension) lbs.getGenericExtension("SearchExtension");
		} catch (Exception e){
			//_logger.warn("SearchExtension is not available.");
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
				versionOrTag.setVersion(version);
				ref.setVersionOrTag(versionOrTag);
		    }
			includes.add(ref);
		}

		ResolvedConceptReferencesIterator iterator = null;
		try {
			iterator = searchExtension.search(matchText, includes, converToMatchAlgorithm(searchOption, algorithm));
			printNumberOfMatches(iterator);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (iterator != null) {
			return new ResolvedConceptReferencesIteratorWrapper(iterator);
		}
		return null;
	}


    protected static void displayRef(ResolvedConceptReference ref) {
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
			}
		} else if (algorithm.compareTo("exactMatch") == 0 && searchOption == BY_CODE) {
			return SearchExtension.MatchAlgorithm.CODE_EXACT;
		}
		return null;
	}

	public class ResolvedConceptReferencesIteratorWrapper {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 4126716487618136771L;

		/** The lbs. */
		private ResolvedConceptReferencesIterator _iterator;

		/** The quick iterator. */
		private String _message = null;
		private String _codingSchemeName = null;
		private String _codingSchemeVersion = null;

		public ResolvedConceptReferencesIteratorWrapper(
			ResolvedConceptReferencesIterator iterator) {
			_iterator = iterator;
			_message = null;
		}

		public ResolvedConceptReferencesIteratorWrapper(
			ResolvedConceptReferencesIterator iterator, String message) {
			_iterator = iterator;
			_message = message;
		}

		public void setIterator(ResolvedConceptReferencesIterator iterator) {
			_iterator = iterator;
		}

		public ResolvedConceptReferencesIterator getIterator() {
			return _iterator;
		}

		public void setMessage(String message) {
			_message = message;
		}

		public String getMessage() {
			return _message;
		}

		public void setCodingSchemeName(String scheme) {
			_codingSchemeName = scheme;
		}

		public String getCodingSchemeName() {
			return _codingSchemeName;
		}

		public void setCodingSchemeVersion(String version) {
			_codingSchemeVersion = version;
		}

		public String getCodingSchemeVersion() {
			return _codingSchemeVersion;
		}
	}


	public static void main(String [] args) {
		try {
			SearchMethodExamples test = new SearchMethodExamples();
			test.setUp();
			test.testSearchMethods();
		} catch (Exception ex) {

		}
	}
}


