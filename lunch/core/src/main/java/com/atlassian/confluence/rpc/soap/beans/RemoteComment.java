/**
 * RemoteComment.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.atlassian.confluence.rpc.soap.beans;

import javax.xml.namespace.QName;

@SuppressWarnings({"unused", "serial", "rawtypes"})
public class RemoteComment implements java.io.Serializable {

	private java.lang.String content;

	private java.util.Calendar created;

	private java.lang.String creator;

	private long id;

	private java.util.Calendar modified;

	private java.lang.String modifier;

	private long pageId;

	private long parentId;

	private java.lang.String title;

	private java.lang.String url;

	public RemoteComment() {
	}

	public RemoteComment(
		final java.lang.String content,
		final java.util.Calendar created,
		final java.lang.String creator,
		final long id,
		final java.util.Calendar modified,
		final java.lang.String modifier,
		final long pageId,
		final long parentId,
		final java.lang.String title,
		final java.lang.String url
	) {
		this.content = content;
		this.created = created;
		this.creator = creator;
		this.id = id;
		this.modified = modified;
		this.modifier = modifier;
		this.pageId = pageId;
		this.parentId = parentId;
		this.title = title;
		this.url = url;
	}

	/**
	 * Gets the content value for this RemoteComment.
	 *
	 * @return content
	 */
	public java.lang.String getContent() {
		return content;
	}

	/**
	 * Sets the content value for this RemoteComment.
	 *
	 * @param content
	 */
	public void setContent(final java.lang.String content) {
		this.content = content;
	}

	/**
	 * Gets the created value for this RemoteComment.
	 *
	 * @return created
	 */
	public java.util.Calendar getCreated() {
		return created;
	}

	/**
	 * Sets the created value for this RemoteComment.
	 *
	 * @param created
	 */
	public void setCreated(final java.util.Calendar created) {
		this.created = created;
	}

	/**
	 * Gets the creator value for this RemoteComment.
	 *
	 * @return creator
	 */
	public java.lang.String getCreator() {
		return creator;
	}

	/**
	 * Sets the creator value for this RemoteComment.
	 *
	 * @param creator
	 */
	public void setCreator(final java.lang.String creator) {
		this.creator = creator;
	}

	/**
	 * Gets the id value for this RemoteComment.
	 *
	 * @return id
	 */
	public long getId() {
		return id;
	}

	/**
	 * Sets the id value for this RemoteComment.
	 *
	 * @param id
	 */
	public void setId(final long id) {
		this.id = id;
	}

	/**
	 * Gets the modified value for this RemoteComment.
	 *
	 * @return modified
	 */
	public java.util.Calendar getModified() {
		return modified;
	}

	/**
	 * Sets the modified value for this RemoteComment.
	 *
	 * @param modified
	 */
	public void setModified(final java.util.Calendar modified) {
		this.modified = modified;
	}

	/**
	 * Gets the modifier value for this RemoteComment.
	 *
	 * @return modifier
	 */
	public java.lang.String getModifier() {
		return modifier;
	}

	/**
	 * Sets the modifier value for this RemoteComment.
	 *
	 * @param modifier
	 */
	public void setModifier(final java.lang.String modifier) {
		this.modifier = modifier;
	}

	/**
	 * Gets the pageId value for this RemoteComment.
	 *
	 * @return pageId
	 */
	public long getPageId() {
		return pageId;
	}

	/**
	 * Sets the pageId value for this RemoteComment.
	 *
	 * @param pageId
	 */
	public void setPageId(final long pageId) {
		this.pageId = pageId;
	}

	/**
	 * Gets the parentId value for this RemoteComment.
	 *
	 * @return parentId
	 */
	public long getParentId() {
		return parentId;
	}

	/**
	 * Sets the parentId value for this RemoteComment.
	 *
	 * @param parentId
	 */
	public void setParentId(final long parentId) {
		this.parentId = parentId;
	}

	/**
	 * Gets the title value for this RemoteComment.
	 *
	 * @return title
	 */
	public java.lang.String getTitle() {
		return title;
	}

	/**
	 * Sets the title value for this RemoteComment.
	 *
	 * @param title
	 */
	public void setTitle(final java.lang.String title) {
		this.title = title;
	}

	/**
	 * Gets the url value for this RemoteComment.
	 *
	 * @return url
	 */
	public java.lang.String getUrl() {
		return url;
	}

	/**
	 * Sets the url value for this RemoteComment.
	 *
	 * @param url
	 */
	public void setUrl(final java.lang.String url) {
		this.url = url;
	}

	private java.lang.Object __equalsCalc = null;

	@Override
	public synchronized boolean equals(final java.lang.Object obj) {
		if (!(obj instanceof RemoteComment))
			return false;
		final RemoteComment other = (RemoteComment) obj;
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (__equalsCalc != null) {
			return (__equalsCalc == obj);
		}
		__equalsCalc = obj;
		final boolean _equals;
		_equals = true && ((this.content == null && other.getContent() == null) || (this.content != null && this.content.equals(other.getContent())))
			&& ((this.created == null && other.getCreated() == null) || (this.created != null && this.created.equals(other.getCreated())))
			&& ((this.creator == null && other.getCreator() == null) || (this.creator != null && this.creator.equals(other.getCreator()))) && this.id == other.getId()
			&& ((this.modified == null && other.getModified() == null) || (this.modified != null && this.modified.equals(other.getModified())))
			&& ((this.modifier == null && other.getModifier() == null) || (this.modifier != null && this.modifier.equals(other.getModifier()))) && this.pageId == other.getPageId()
			&& this.parentId == other.getParentId() && ((this.title == null && other.getTitle() == null) || (this.title != null && this.title.equals(other.getTitle())))
			&& ((this.url == null && other.getUrl() == null) || (this.url != null && this.url.equals(other.getUrl())));
		__equalsCalc = null;
		return _equals;
	}

	private boolean __hashCodeCalc = false;

	@Override
	public synchronized int hashCode() {
		if (__hashCodeCalc) {
			return 0;
		}
		__hashCodeCalc = true;
		int _hashCode = 1;
		if (getContent() != null) {
			_hashCode += getContent().hashCode();
		}
		if (getCreated() != null) {
			_hashCode += getCreated().hashCode();
		}
		if (getCreator() != null) {
			_hashCode += getCreator().hashCode();
		}
		_hashCode += new Long(getId()).hashCode();
		if (getModified() != null) {
			_hashCode += getModified().hashCode();
		}
		if (getModifier() != null) {
			_hashCode += getModifier().hashCode();
		}
		_hashCode += new Long(getPageId()).hashCode();
		_hashCode += new Long(getParentId()).hashCode();
		if (getTitle() != null) {
			_hashCode += getTitle().hashCode();
		}
		if (getUrl() != null) {
			_hashCode += getUrl().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	// Type metadata
	private static final org.apache.axis.description.TypeDesc typeDesc = new org.apache.axis.description.TypeDesc(RemoteComment.class, true);

	static {
		typeDesc.setXmlType(new javax.xml.namespace.QName("http://beans.soap.rpc.confluence.atlassian.com", "RemoteComment"));
		org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("content");
		elemField.setXmlName(new javax.xml.namespace.QName("", "content"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("created");
		elemField.setXmlName(new javax.xml.namespace.QName("", "created"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("creator");
		elemField.setXmlName(new javax.xml.namespace.QName("", "creator"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("id");
		elemField.setXmlName(new javax.xml.namespace.QName("", "id"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("modified");
		elemField.setXmlName(new javax.xml.namespace.QName("", "modified"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("modifier");
		elemField.setXmlName(new javax.xml.namespace.QName("", "modifier"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("pageId");
		elemField.setXmlName(new javax.xml.namespace.QName("", "pageId"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("parentId");
		elemField.setXmlName(new javax.xml.namespace.QName("", "parentId"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
		elemField.setNillable(false);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("title");
		elemField.setXmlName(new javax.xml.namespace.QName("", "title"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
		elemField = new org.apache.axis.description.ElementDesc();
		elemField.setFieldName("url");
		elemField.setXmlName(new javax.xml.namespace.QName("", "url"));
		elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
		elemField.setNillable(true);
		typeDesc.addFieldDesc(elemField);
	}

	/**
	 * Return type metadata object
	 */
	public static org.apache.axis.description.TypeDesc getTypeDesc() {
		return typeDesc;
	}

	/**
	 * Get Custom Serializer
	 */
	public static org.apache.axis.encoding.Serializer getSerializer(final Class _javaType, final QName _xmlType) {
		return new org.apache.axis.encoding.ser.BeanSerializer(_javaType, _xmlType, typeDesc);
	}

	/**
	 * Get Custom Deserializer
	 */
	public static org.apache.axis.encoding.Deserializer getDeserializer(final Class _javaType, final QName _xmlType) {
		return new org.apache.axis.encoding.ser.BeanDeserializer(_javaType, _xmlType, typeDesc);
	}

}
