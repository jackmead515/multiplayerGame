package com.jack.load;

import java.util.ArrayList;

import com.jack.entities.Boundary;
import com.jack.entities.Item;

public class Map {

	public ArrayList<Boundary> boundaries;
	public ArrayList<Item> items;
	public String name;
	
	public Map(ArrayList<Boundary> boundaries, ArrayList<Item> items, String name){
		this.boundaries = boundaries;
		this.items = items;
		this.name = name;
	}

}
