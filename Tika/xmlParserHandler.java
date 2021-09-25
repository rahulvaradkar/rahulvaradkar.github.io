//javax.xml-1.3.4.jar
//w3c-dom.jar

//Files in this exercise
// xmlParserHandler.java
// xmlParser.java
// TestXmlSaxParser.java
// sample.xml

import java.util.ArrayList;
import java.util.Stack;
import java.util.LinkedHashSet;
import java.util.*;
 
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.Scanner;

public class xmlParserHandler extends DefaultHandler
{
    //This is the list which shall be populated while parsing the XML.
    // private ArrayList userList = new ArrayList();

    //As we read any XML element we will push that in this stack
    private Stack elementStack = new Stack();

    //As we complete one user block in XML, we will push the User instance in userList
    // private Stack objectStack = new Stack();

    //As we read any XML element we will push that in this stack
    private Stack pathStack = new Stack();
    private Stack allPaths = new Stack();


	Scanner scan = new Scanner(System.in);

	void pressAnyKey()
	{
	     System.out.print("Press any key to continue . . . ");
	     scan.nextLine();
	}


	public void printAllPaths()
	{
        System.out.println("Printing all paths =============start");

		allPaths.forEach(k->{
		    System.out.println("....."+k);
		});
        System.out.println("Printing all paths =============end");

	}

    public void startDocument() throws SAXException
    {
        System.out.println("start of the document   : ");
    }
 
    public void endDocument() throws SAXException
    {
        System.out.println("end of the document document     : ");

		// LinkedHashSet<String> al=new LinkedHashSet<String>();  

		allPaths.forEach(k->{
		    System.out.println("All Paths....."+k);
			// al.add((String) k);
		});

		// Iterator<String> itr = al.iterator();  
		// while(itr.hasNext())
		// {  
		// 	System.out.println("Unique Paths : " + itr.next());  
		// }  
		showUniquePaths();

    }

    private void showUniquePaths() throws SAXException
    {

        LinkedHashSet<String> al=new LinkedHashSet<String>();  

        allPaths.forEach(k->{
            // System.out.println("All Paths....."+k);
            al.add((String) k);
        });

        Iterator<String> itr = al.iterator();  
        while(itr.hasNext())
        {  
            System.out.println("Unique Paths : " + itr.next());  
        }  

    }

    public Iterator<String> getUniquePaths() throws SAXException
    {

		LinkedHashSet<String> al=new LinkedHashSet<String>();  

		allPaths.forEach(k->{
		    // System.out.println("All Paths....."+k);
			al.add((String) k);
		});

		Iterator<String> itr = al.iterator();  
		// while(itr.hasNext())
		// {  
		// 	System.out.println("Unique Paths : " + itr.next());  
		// }  
        return itr;
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
    {
        System.out.println("........Inside startElement....");
        System.out.println("........uri: " + uri);
        System.out.println("........localName: " + localName);
        System.out.println("........qName: " + qName);
        System.out.println("........attributes: " + attributes);
		int attributeLength = attributes.getLength();

		if (!this.pathStack.empty())
		{
	        String asOfPath = (String) this.pathStack.peek();
	        this.pathStack.pop();
	        this.pathStack.push(asOfPath + "/" + qName);
		}
		else
		{
	        this.pathStack.push("/" + qName);
		}


		if (attributeLength == 0)
			System.out.print("......No Attributes");
		else
		{
			for (int i = 0; i < attributeLength; i++) {
				// Get attribute names and values
				String attrName = attributes.getQName(i);
				String attrVal = attributes.getValue(i);
				System.out.print(".......attribute [" + i + "] : " + attrName + " = " + attrVal + "; ");
			}
		}
		System.out.println("");
        pressAnyKey();

        //Push it in element stack
        this.elementStack.push(qName);
 
        //If this is start of 'user' element then prepare a new User instance and push it in object stack
        // if ("user".equals(qName))
        // {
        //     //New User instance
        //     User user = new User();
 
        //     //Set all required attributes in any XML element here itself
        //     if(attributes != null && attributes.getLength() == 1)
        //     {
        //         user.setId(Integer.parseInt(attributes.getValue(0)));
        //     }
        //     this.objectStack.push(user);
        // }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException
    {
        System.out.println("........Inside endElement....");
        System.out.println("........uri: " + uri);
        System.out.println("........localName: " + localName);
        System.out.println("........qName: " + qName);

		StringBuffer lastPath = new StringBuffer();
		elementStack.forEach(k->{
		    System.out.println("....."+k);
		    lastPath.append("/" + k);
		});
        this.allPaths.push(lastPath.toString());

        //Remove last added  element
        this.elementStack.pop();


        printAllPaths();

        pressAnyKey();

 
        //User instance has been constructed so pop it from object stack and push in userList
        // if ("user".equals(qName))
        // {
        //     User object = (User) this.objectStack.pop();
        //     this.userList.add(object);
        // }
    }


	/**
	* This will be called everytime parser encounter a value node
	* */
    public void characters(char[] ch, int start, int length) throws SAXException
    {
        System.out.println("........Inside characters....");
        System.out.println("........ch[]: " + ch);
        System.out.println("........start: " + start);
        System.out.println("........length: " + length);

        String value = new String(ch, start, length).trim();

        System.out.println("........value: " + value);
        pressAnyKey();
         
        if (value.length() == 0)
        {
            return; // ignore white space
        }
 
        //handle the value based on to which element it belongs
        // if ("firstname".equals(currentElement()))
        // {
        //     User user = (User) this.objectStack.peek();
        //     user.setFirstName(value);
        // }
        // else if ("lastname".equals(currentElement()))
        // {
        //     User user = (User) this.objectStack.peek();
        //     user.setLastName(value);
        // }
    }

	/**
	* Utility method for getting the current element in processing
	* */
    private String currentElement()
    {
        return (String) this.elementStack.peek();
    }

    //Accessor for userList object
    // public ArrayList getUsers()
    // {
    //     return userList;
    // }

}

