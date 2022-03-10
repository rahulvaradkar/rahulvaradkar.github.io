package com.boardwalk.table;

import java.util.*;
import java.io.*;

public class Columntype  implements Serializable {
   String  m_type;


  public Columntype ( String a_type ) {
    m_type  = a_type;
  }


  public Columntype () {
  }


  public String getType () {
  return m_type;
  }


};


