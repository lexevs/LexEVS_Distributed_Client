package org.LexGrid.LexBIG.serviceHolder;

import gov.nih.nci.system.client.ApplicationServiceProvider;
import org.LexGrid.LexBIG.DataModel.Collections.ConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Collections.ResolvedConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.DataModel.Core.ConceptReference;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.caCore.interfaces.LexEVSApplicationService;
import org.LexGrid.concepts.Entity;
import org.LexGrid.valueSets.DefinitionEntry;
import org.LexGrid.valueSets.EntityReference;
import org.LexGrid.valueSets.ValueSetDefinition;
import org.LexGrid.valueSets.ValueSetDefinitionReference;
import org.LexGrid.valueSets.types.DefinitionOperator;
import org.lexgrid.valuesets.LexEVSValueSetDefinitionServices;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Vector;

public class ExportExample {

    private static String url = "https://lexevsapi6.nci.nih.gov/lexevsapi64";

    public static ConceptReferenceList createConceptReferenceList(
            String[] codes, String codingSchemeName) {
        if (codes == null) {
            return null;
        }
        ConceptReferenceList list = new ConceptReferenceList();
        for (int i = 0; i < codes.length; i++) {
            ConceptReference cr = new ConceptReference();
            cr.setCodingSchemeName(codingSchemeName);
            cr.setConceptCode(codes[i]);
            list.addConceptReference(cr);
        }
        return list;
    }

    public static Entity getConceptByCode(String codingSchemeName, String vers, String ltag, String code) {
        try {
            LexBIGService lbSvc =
                    (LexEVSApplicationService)ApplicationServiceProvider.getApplicationServiceFromUrl(url, "EvsServiceInfo");

            if (lbSvc == null) {
                return null;
            }
            CodingSchemeVersionOrTag versionOrTag =
                    new CodingSchemeVersionOrTag();
            versionOrTag.setVersion(vers);

            ConceptReferenceList crefs =
                    createConceptReferenceList(new String[] { code },
                            codingSchemeName);

            CodedNodeSet cns = null;
            try {
                cns =
                        lbSvc.getCodingSchemeConcepts(codingSchemeName,
                                versionOrTag);
                cns = cns.restrictToCodes(crefs);
                ResolvedConceptReferenceList matches =
                        cns.resolveToList(null, null, null, 1);
                if (matches == null) {
                    return null;
                }
                int count = matches.getResolvedConceptReferenceCount();
                if (count == 0)
                    return null;
                if (count > 0) {
                    try {
                        ResolvedConceptReference ref =
                                (ResolvedConceptReference) matches
                                        .enumerateResolvedConceptReference()
                                        .nextElement();
                        Entity entry = ref.getReferencedEntry();
                        return entry;
                    } catch (Exception ex1) {
                        return null;
                    }
                }
            } catch (Exception e1) {
                e1.printStackTrace();
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }


    public static String exportCartXML(String codingSchemeName, Vector v) throws Exception {
        LexEVSApplicationService svc =
                (LexEVSApplicationService)ApplicationServiceProvider.getApplicationServiceFromUrl(url, "EvsServiceInfo");;

        LexEVSValueSetDefinitionServices vsd_service = svc.getLexEVSValueSetDefinitionServices();
        ValueSetDefinition vsd = new ValueSetDefinition();
        vsd.setValueSetDefinitionURI("EXPORT:VSDREF_CART");
        vsd.setValueSetDefinitionName("VSDREF_CART");
        vsd.setDefaultCodingScheme(codingSchemeName);
        vsd.setConceptDomain("Concepts");

        DefinitionEntry de = new DefinitionEntry();

        de.setRuleOrder(1L);
        de.setOperator(DefinitionOperator.OR);

        ValueSetDefinitionReference vsdRef = new ValueSetDefinitionReference();
        vsdRef.setValueSetDefinitionURI("EXPORT:CART_NODES");

        de.setValueSetDefinitionReference(vsdRef);

        vsd.addDefinitionEntry(de);

        String vers = null;
        String ltag = null;

        for (int i=0; i<v.size(); i++) {
            String code = (String) v.elementAt(i);
            Entity entity = getConceptByCode(codingSchemeName, vers, ltag, code);
            if (entity != null) {

                String EC = entity.getEntityCode();
                String ECN = entity.getEntityCodeNamespace();

                EntityReference entityRef = new EntityReference();

                entityRef.setEntityCode(EC);
                entityRef.setEntityCodeNamespace(ECN);
                entityRef.setLeafOnly(false);
                entityRef.setTransitiveClosure(false);

                de = new DefinitionEntry();
                de.setRuleOrder(2L);
                de.setOperator(DefinitionOperator.OR);

                de.setEntityReference(entityRef);
                vsd.addDefinitionEntry(de);
            }
        }
        StringBuffer buf = new StringBuffer();
        long ms = System.currentTimeMillis();
        InputStream reader = vsd_service.exportValueSetResolution(vsd, null, null, null, false);
        System.out.println("Total run time vsd_service.exportValueSetResolution (ms): " + (System.currentTimeMillis() - ms));

        System.out.println("NOW: " + new Date());
        if (reader != null) {
            try {
                for (int c = reader.read(); c != -1; c = reader.read()) {
                    buf.append((char) c);
                }

            } catch (IOException e) {
                throw e;
            } finally {
                try {
                    System.out.println("THEN: " + new Date());
                    reader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        String str = buf.toString();
        return str;
    }

    public static void main(String [ ] args)
    {
        try {
            String codingSchemeName = "Thesaurus";
            Vector v = new Vector();
            v.add("C12354");

            String xml = exportCartXML(codingSchemeName, v);
            System.out.println(xml);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}