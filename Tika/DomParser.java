//javax.xml-1.3.4.jar
//w3c-dom.jar


import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;


public class DomParser {
	


	public static void main(String[] args)
	{

		// Schema schema = null;
		try {


			//Get Document Builder
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();

			//Build Document
			Document document = builder.parse(new File("employee.xml"));

			//Normalize the XML Structure; It's just too important !!
			document.getDocumentElement().normalize();

			//Here comes the root node
			Element root = document.getDocumentElement();
			System.out.println(root.getNodeName());


			//Get all employees
			NodeList nList = document.getElementsByTagName("employee");
			System.out.println("============================");


			for (int temp = 0; temp < nList.getLength(); temp++)
			{
				Node node = nList.item(temp);
				System.out.println("");    //Just a separator
				if (node.getNodeType() == Node.ELEMENT_NODE)
				{
					//Print each employee's detail
					Element eElement = (Element) node;
					System.out.println("Employee id : "    + eElement.getAttribute("id"));
					System.out.println("First Name : "  + eElement.getElementsByTagName("firstName").item(0).getTextContent());
					System.out.println("Last Name : "   + eElement.getElementsByTagName("lastName").item(0).getTextContent());
					System.out.println("Location : "    + eElement.getElementsByTagName("location").item(0).getTextContent());
				}
			}

			// String language = XMLConstants.W3C_XML_SCHEMA_NS_URI;
			// SchemaFactory factory = SchemaFactory.newInstance(language);
			// schema = factory.newSchema(new File(name));


			// Element root = document.getDocumentElement();	

			// element.getAttribute("attributeName") ;    //returns specific attribute
			// element.getAttributes();                //returns a Map (table) of names/values

			// node.getElementsByTagName("subElementName") //returns a list of sub-elements of specified name
			// node.getChildNodes()                         //returns a list of all child nodes

		} 
		catch (Exception e) 
		{
		    e.printStackTrace();
		}
		
		// Validator validator = schema.newValidator();
		// validator.validate(new DOMSource(document));


		// Element root = document.getDocumentElement();

	}


}
