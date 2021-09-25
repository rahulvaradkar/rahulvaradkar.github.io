//javax.xml-1.3.4.jar
//w3c-dom.jar

//Files in this exercise
// UserParserHandler.java
// user.java
// UsersXmlParser.java
// TestSaxParser.java
// sample.xml

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;


public class TestSaxParser 
{

    public static void main(String[] args) throws FileNotFoundException
    {
        //Locate the file
//        File xmlFile = new File("sample.xml");
         File xmlFile = new File(args[0]);
        //Create the parser instance
        UsersXmlParser parser = new UsersXmlParser();
 
        //Parse the file
        ArrayList users = parser.parseXml(new FileInputStream(xmlFile));
 
        //Verify the result
        System.out.println(users);
    }
	
}