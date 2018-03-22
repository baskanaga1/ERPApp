package com.guruinfo.scm.tms;
/**
 * Created by Kannan G on 9/21/2016.
 */
import java.io.Serializable;
/**
 * Simple container object for contact data
 *
 *
 */
public class lists implements Serializable{
    private String value;
    private String id;
    public lists(String n, String e) {
        value = n;
        id = e;
    }
    public String getValue() { return value; }
    public String getId() { return id; }
    @Override
    public String toString() { return value; }
}
