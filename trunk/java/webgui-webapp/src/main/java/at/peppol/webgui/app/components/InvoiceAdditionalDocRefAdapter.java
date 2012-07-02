package at.peppol.webgui.app.components;

import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.AttachmentType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.DocumentReferenceType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.ExternalReferenceType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.DocumentTypeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.EmbeddedDocumentBinaryObjectType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.IDType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.URIType;

public class InvoiceAdditionalDocRefAdapter extends DocumentReferenceType {
  
  public InvoiceAdditionalDocRefAdapter() {
    setID (new IDType ());
    setDocumentType (new DocumentTypeType ());
    setAttachment (new AttachmentType ());
    getAttachment().setEmbeddedDocumentBinaryObject (new EmbeddedDocumentBinaryObjectType ());
    getAttachment().setExternalReference (new ExternalReferenceType ());
    getAttachment().getExternalReference ().setURI (new URIType ());
  }
  
  public void setAdditionalDocRefID(String v) {
    getID ().setValue (v);
  }
  
  public String getAdditionalDocRefID() {
    return getID ().getValue ();
  }
  
  public void setAdditionalDocRefDocumentType(String v) {
    getDocumentType ().setValue (v);
  }
  
  public String getAdditionalDocRefDocumentType() {
    return getDocumentType ().getValue ();
  }
  
  public void setAdditionalDocRefEmbeddedDocumentBinaryObject(byte[] v) {
    getAttachment().getEmbeddedDocumentBinaryObject().setValue (v);
  }
  
  public byte[] getAdditionalDocRefEmbeddedDocumentBinaryObject() {
    return getAttachment().getEmbeddedDocumentBinaryObject ().getValue ();
  }    
  
  public void setAdditionalDocRefExternalReference(String v) {
    getAttachment().getExternalReference ().getURI ().setValue (v);
  }
  
  public String getAdditionalDocRefExternalReference() {
    return getAttachment().getExternalReference ().getURI ().getValue ();
  }  
}