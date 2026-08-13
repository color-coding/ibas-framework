package org.colorcoding.ibas.bobas.serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.xml.XMLConstants;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.colorcoding.ibas.bobas.MyConfiguration;
import org.colorcoding.ibas.bobas.data.DateTime;
import org.colorcoding.ibas.bobas.serialization.structure.Analyzer;
import org.colorcoding.ibas.bobas.serialization.structure.Element;
import org.colorcoding.ibas.bobas.serialization.structure.ElementRoot;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * xml序列化者
 * 
 * 继承实现时，注意序列化和反序列化监听
 */
public class SerializerXml extends Serializer {

	private final Map<String, JAXBContext> contextCache = new ConcurrentHashMap<>();

	private JAXBContext getOrCreateContext(Class<?>... types) throws JAXBException {
		String key = Arrays.stream(types).map(Class::getName).sorted().collect(Collectors.joining(","));
		JAXBContext ctx = this.contextCache.get(key);
		if (ctx == null) {
			synchronized (this.contextCache) {
				ctx = this.contextCache.get(key);
				if (ctx == null) {
					ctx = JAXBContext.newInstance(types);
					this.contextCache.put(key, ctx);
				}
			}
		}
		return ctx;
	}

	/**
	 * 为Unmarshaller设置XXE安全属性，JDK 8内置JAXB不支持时静默忽略
	 */
	private void configureSecureUnmarshaller(Unmarshaller unmarshaller) {
		try {
			unmarshaller.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
			unmarshaller.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
		} catch (Exception e) {
			// JDK 8 内置 Unmarshaller 不支持此属性，忽略
		}
	}

	@Override
	public void serialize(Object object, OutputStream outputStream, boolean formated, Class<?>... types) {
		try {
			Objects.requireNonNull(object);
			Class<?>[] knownTypes = new Class<?>[types.length + 1];
			knownTypes[0] = object.getClass();
			System.arraycopy(types, 0, knownTypes, 1, types.length);
			JAXBContext context = this.getOrCreateContext(knownTypes);
			if (object instanceof Collection<?>) {
				// 集合根：JAXB不支持Collection子类作为根直接marshal，用DOM包裹逐项marshal
				this.serializeCollection(object, context, outputStream, formated, types);
			} else {
				Marshaller marshaller = context.createMarshaller();
				marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");// 编码格式
				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, formated);// 是否格式化生成的xml串
				marshaller.setProperty(Marshaller.JAXB_FRAGMENT, false);// 是否省略xm头声明信息
				marshaller.marshal(object, outputStream);
			}
		} catch (Exception e) {
			throw new SerializationException(e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T deserialize(InputSource inputSource, Class<?>... types)  {
		try {
			if (types != null && types.length > 0 && this.isCollectionType(types[0])) {
				return (T) this.deserializeCollection(inputSource, types);
			}
			// 反序列化不使用缓存，避免types不含根类型时context缺少descriptor
			JAXBContext context = JAXBContext.newInstance(types);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			this.configureSecureUnmarshaller(unmarshaller);
			return (T) unmarshaller.unmarshal(inputSource);
		} catch (Exception e) {
			throw new SerializationException(e.getMessage(), e);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T deserialize(InputStream inputStream, Class<?>... types)  {
		try {
			if (types != null && types.length > 0 && this.isCollectionType(types[0])) {
				return (T) this.deserializeCollection(new InputSource(inputStream), types);
			}
			// 反序列化不使用缓存，避免types不含根类型时context缺少descriptor
			JAXBContext context = JAXBContext.newInstance(types);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			this.configureSecureUnmarshaller(unmarshaller);
			return (T) unmarshaller.unmarshal(inputStream);
		} catch (Exception e) {
			throw new SerializationException(e.getMessage(), e);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T deserialize(Reader reader, Class<?>... types)  {
		try {
			if (types != null && types.length > 0 && this.isCollectionType(types[0])) {
				return (T) this.deserializeCollection(new InputSource(reader), types);
			}
			// 反序列化不使用缓存，避免types不含根类型时context缺少descriptor
			JAXBContext context = JAXBContext.newInstance(types);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			this.configureSecureUnmarshaller(unmarshaller);
			return (T) unmarshaller.unmarshal(reader);
		} catch (Exception e) {
			throw new SerializationException(e.getMessage(), e);
		}
	}

	/**
	 * 序列化集合根：用DOM包裹逐项marshal，再通过Transformer输出。
	 *
	 * JAXB RI将Collection子类视为CollectionBeanInfo，不支持作为根直接marshal；
	 * 此方法创建一个包裹元素，将每个元素marshal为其子节点。
	 */
	@SuppressWarnings("unchecked")
	private void serializeCollection(Object object, JAXBContext context, OutputStream outputStream, boolean formated,
			Class<?>... types)
				throws Exception {
		Collection<?> collection = (Collection<?>) object;
		Class<?> itemType = this.resolveCollectionItemType(types);
		String[] rootInfo = this.resolveXmlInfo(object.getClass());
		// 构建DOM文档
		DocumentBuilder db = this.createSecureDocumentBuilder();
		Document doc = db.newDocument();
		org.w3c.dom.Element root = doc.createElementNS(rootInfo[0].isEmpty() ? null : rootInfo[0], rootInfo[1]);
		if (!rootInfo[0].isEmpty()) {
			root.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns", rootInfo[0]);
		}
		doc.appendChild(root);
		if (!collection.isEmpty()) {
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
			for (Object item : collection) {
				if (item == null) {
					continue;
				}
				try {
					marshaller.marshal(item, root);
				} catch (JAXBException e) {
					String[] itemInfo = this.resolveXmlInfo(itemType);
					JAXBElement<Object> element = new JAXBElement<>(
							new javax.xml.namespace.QName(itemInfo[0], itemInfo[1]),
							(Class<Object>) itemType, item);
					marshaller.marshal(element, root);
				}
			}
		}
		// 通过Transformer输出，支持格式化
		TransformerFactory tf = TransformerFactory.newInstance();
		tf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		try {
			tf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
			tf.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
		} catch (IllegalArgumentException e) {
			// Oracle JDK 8 内置 TransformerFactory 不支持此属性，忽略
		}
		Transformer transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty(OutputKeys.ENCODING, XML_FILE_ENCODING);
		if (formated) {
			transformer.setOutputProperty(OutputKeys.INDENT, XML_FILE_INDENT);
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		} else {
			transformer.setOutputProperty(OutputKeys.INDENT, "no");
		}
		transformer.transform(new DOMSource(doc), new StreamResult(outputStream));
	}

	/**
	 * 反序列化集合根：解析XML为DOM，逐项unmarshal子元素，填充到具体集合实例。
	 */
	@SuppressWarnings("unchecked")
	private <T> T deserializeCollection(InputSource inputSource, Class<?>... types) throws Exception {
		DocumentBuilder db = this.createSecureDocumentBuilder();
		Document doc = db.parse(inputSource);
		org.w3c.dom.Element root = doc.getDocumentElement();
		Class<?> collectionType = types[0];
		Class<?> itemType = this.resolveCollectionItemType(types);
		Collection<Object> collection = this.newCollection(collectionType);
		// 反序列化不使用缓存，避免types不含根类型时context缺少descriptor
		JAXBContext context = JAXBContext.newInstance(types);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		this.configureSecureUnmarshaller(unmarshaller);
		NodeList children = root.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				Object item = unmarshaller.unmarshal(child, itemType).getValue();
				if (item != null) {
					collection.add(item);
				}
			}
		}
		return (T) collection;
	}

	/**
	 * 创建安全的DocumentBuilder，防御XXE攻击。
	 */
	private DocumentBuilder createSecureDocumentBuilder() throws ParserConfigurationException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
		dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		dbf.setXIncludeAware(false);
		dbf.setExpandEntityReferences(false);
		return dbf.newDocumentBuilder();
	}

	public void validate(Schema schema, InputStream data) throws ValidateException {
		try {
			Validator validator = schema.newValidator();
			try {
				validator.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
				validator.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
			} catch (SAXException e) {
				// JDK 8 内置 Validator 不支持此属性，忽略
			}
			Source xmlSource = new StreamSource(data);
			validator.validate(xmlSource);
		} catch (SAXException | IOException e) {
			throw new ValidateException(e.getMessage(), e);
		}
	}

	@Override
	public void validate(Class<?> type, InputStream data) throws ValidateException {
		this.validate(this.schema(type), data);
	}

	public Schema schema(Class<?> type)  {
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream(512)) {
			this.schema(type, outputStream);
			try (InputStream stream = new ByteArrayInputStream(outputStream.toByteArray())) {
				SchemaFactory factory = SchemaFactory.newInstance(XML_FILE_NAMESPACE);
				factory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
				factory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
				Source xsdSource = new StreamSource(stream);
				return factory.newSchema(xsdSource);
			}
		} catch (SAXException | IOException e) {
			throw new SerializationException(e.getMessage(), e);
		}
	}

	public static final String XML_FILE_EXTENSION = ".xml";
	public static final String XML_FILE_ENCODING = "utf-8";
	public static final String XML_FILE_INDENT = "yes";
	public static final String XML_FILE_NAMESPACE = "http://www.w3.org/2001/XMLSchema";

	@Override
	public void schema(Class<?> type, OutputStream outputStream)  {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
			dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			dbf.setXIncludeAware(false);
			dbf.setExpandEntityReferences(false);
			DocumentBuilder db = dbf.newDocumentBuilder();
			DOMImplementation domImpl = db.getDOMImplementation();
			Document document = domImpl.createDocument(XML_FILE_NAMESPACE, "xs:schema", null);
			// 创建文档
			SchemaWriter schemaWriter = new SchemaWriter();
			schemaWriter.document = document;
			schemaWriter.element = new Analyzer().analyse(type);
			schemaWriter.write();
			// 将xml写到文件中
			TransformerFactory tf = TransformerFactory.newInstance();
			tf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			try {
				tf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
				tf.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
			} catch (IllegalArgumentException e) {
				// Oracle JDK 8 内置 TransformerFactory 不支持此属性，忽略
			}
			javax.xml.transform.Transformer transformer = tf.newTransformer();
			// 添加xml 头信息
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.ENCODING, XML_FILE_ENCODING);
			transformer.setOutputProperty(OutputKeys.INDENT, XML_FILE_INDENT);

			if (MyConfiguration.getConfigValue(MyConfiguration.CONFIG_ITEM_FORMATTED_OUTPUT, false)) {
				transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			}
			transformer.transform(new DOMSource(document), new StreamResult(outputStream));
		} catch (ParserConfigurationException | TransformerException e) {
			throw new SerializationException(e.getMessage(), e);
		}
	}

	class SchemaWriter {

		public SchemaWriter() {
			this.knownTypes = new HashMap<>();
			this.knownTypes.put("int", "xs:int");
			this.knownTypes.put("integer", "xs:int");
			this.knownTypes.put("long", "xs:long");
			this.knownTypes.put("short", "xs:short");
			this.knownTypes.put("float", "xs:float");
			this.knownTypes.put("double", "xs:double");
			this.knownTypes.put("boolean", "xs:boolean");
			this.knownTypes.put("java.lang.Integer", "xs:int");
			this.knownTypes.put("java.lang.Long", "xs:long");
			this.knownTypes.put("java.lang.Short", "xs:short");
			this.knownTypes.put("java.math.BigInteger", "xs:integer");
			this.knownTypes.put("java.lang.Float", "xs:float");
			this.knownTypes.put("java.lang.Double", "xs:double");
			this.knownTypes.put("java.math.BigDecimal", "xs:decimal");
			this.knownTypes.put("java.lang.String", "xs:string");
			this.knownTypes.put("java.lang.Character", "xs:string");
			this.knownTypes.put("java.lang.Boolean", "xs:boolean");
			this.knownTypes.put("java.util.Date", "xs:dateTime");
		}

		public Document document;
		public ElementRoot element;
		private Map<String, String> knownTypes;

		public void write() {
			if (this.element.getNamespace() != null) {
				document.getDocumentElement().setAttribute("targetNamespace", this.element.getNamespace());
			}
			org.w3c.dom.Element dom = this.document.createElement("xs:element");
			dom.setAttribute("name", this.element.getName());
			org.w3c.dom.Element domType = this.document.createElement("xs:complexType");
			org.w3c.dom.Element domSequence = this.document.createElement("xs:sequence");
			for (Element item : this.element.getChilds()) {
				this.write(domSequence, item);
			}
			domType.appendChild(domSequence);
			dom.appendChild(domType);
			this.document.getDocumentElement().appendChild(dom);
		}

		private void write(org.w3c.dom.Element domParent, Element element) {
			org.w3c.dom.Element dom = this.document.createElement("xs:element");
			// 获取元素类型
			String typeName = this.knownTypes.get(element.getType().getName());
			if (typeName != null) {
				// 已知类型
				dom.setAttribute("name", element.getName());
				dom.setAttribute("minOccurs", "0");
				dom.setAttribute("nillable", "true");
				dom.setAttribute("type", typeName);
			} else if (element.getType().isEnum()) {
				// 枚举类型
				dom.setAttribute("name", element.getName());
				dom.setAttribute("minOccurs", "0");
				dom.setAttribute("nillable", "true");
				org.w3c.dom.Element domType = this.document.createElement("xs:simpleType");
				org.w3c.dom.Element domRestriction = this.document.createElement("xs:restriction");
				domRestriction.setAttribute("base", "xs:string");
				for (Object enumItem : element.getType().getEnumConstants()) {
					if (enumItem instanceof Enum<?>) {
						// 枚举值（比对枚举索引）
						Enum<?> itemValue = (Enum<?>) enumItem;
						org.w3c.dom.Element domEnumeration = this.document.createElement("xs:enumeration");
						domEnumeration.setAttribute("value", itemValue.name());
						domRestriction.appendChild(domEnumeration);
					}
				}
				domType.appendChild(domRestriction);
				dom.appendChild(domType);
			} else if (element.getType() == DateTime.class) {
				// 日期类型
				dom.setAttribute("name", element.getName());
				dom.setAttribute("minOccurs", "0");
				dom.setAttribute("nillable", "true");
				org.w3c.dom.Element domType = this.document.createElement("xs:simpleType");
				org.w3c.dom.Element domRestriction = this.document.createElement("xs:restriction");
				domRestriction.setAttribute("base", "xs:string");
				org.w3c.dom.Element domEnumeration = this.document.createElement("xs:pattern");
				// 格式：2000-01-01 or 2000-01-01T00:00:00
				domEnumeration.setAttribute("value",
						"|[0-9]{4}-[0-1][0-9]-[0-3][0-9]|[0-9]{4}-[0-1][0-9]-[0-3][0-9]T[0-2][0-9]:[0-6][0-9]:[0-6][0-9]");
				domRestriction.appendChild(domEnumeration);
				domType.appendChild(domRestriction);
				dom.appendChild(domType);
			} else if (element.isCollection()) {
				dom.setAttribute("name", element.getWrapper());
				dom.setAttribute("minOccurs", "0");
				dom.setAttribute("maxOccurs", "unbounded");
				org.w3c.dom.Element domType = this.document.createElement("xs:complexType");
				org.w3c.dom.Element domSequence = this.document.createElement("xs:sequence");
				org.w3c.dom.Element domItem = this.document.createElement("xs:element");
				domItem.setAttribute("name", element.getName());
				domItem.setAttribute("minOccurs", "0");
				domItem.setAttribute("maxOccurs", "unbounded");
				if (!element.getChilds().isEmpty()) {
					org.w3c.dom.Element domItemType = this.document.createElement("xs:complexType");
					org.w3c.dom.Element domItemSequence = this.document.createElement("xs:sequence");
					for (Element item : element.getChilds()) {
						this.write(domItemSequence, item);
					}
					domItemType.appendChild(domItemSequence);
					domItem.appendChild(domItemType);
				}
				domSequence.appendChild(domItem);
				domType.appendChild(domSequence);
				dom.appendChild(domType);
			} else {
				dom.setAttribute("name", element.getName());
				dom.setAttribute("minOccurs", "0");
				dom.setAttribute("maxOccurs", "unbounded");
				if (!element.getChilds().isEmpty()) {
					org.w3c.dom.Element domType = this.document.createElement("xs:complexType");
					org.w3c.dom.Element domSequence = this.document.createElement("xs:sequence");
					for (Element item : element.getChilds()) {
						this.write(domSequence, item);
					}
					domType.appendChild(domSequence);
					dom.appendChild(domType);
				}
			}
			domParent.appendChild(dom);
		}
	}

}
