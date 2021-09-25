//javax.xml-1.3.4.jar
//w3c-dom.jar

//Files in this exercise
// UserParserHandler.java
// user.java
// UsersXmlParser.java
// TestSaxParser.java
// sample.xml

public class User
{
    //XML attribute id
    private int id;
 
    //XML element fisrtName
     private String firstName;
 
    //XML element lastName
     private String lastName;
 
    public int getId() {
        return id;
    }
 
    public void setId(int id) {
        this.id = id;
    }
 
    public String getFirstName() {
        return firstName;
    }
 
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
 
    public String getLastName() {
        return lastName;
    }
 
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
 
    @Override
    public String toString() {
        return this.id + ":" + this.firstName +  ":" +this.lastName ;
    }
}