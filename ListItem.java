package com.guruinfo.scm;
import java.util.HashMap;
/**
 * @author Kannan G
 */
public class ListItem {
    public int level;
    public final HashMap<String,String> map;
    public int listPosition;
    public ListItem(int level, HashMap<String,String> map) {
        this.level = level;
        this.map = map;
    }
}
