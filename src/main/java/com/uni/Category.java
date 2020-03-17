package com.uni;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

public class Category {
    static Category[] categories;
    String name;
    String[] subcategories;

    public Category(String name) {
        this.name = name;
    }

    static void loadCategories(String path) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(Category.class.getResourceAsStream(path)));
            ArrayList<Category> categoriesList = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                String[] split = line.split(" -> ", 2);
                Category cat = new Category(split[0]);
                cat.subcategories = split[1].split(", ");
                categoriesList.add(cat);
            }
            categories = categoriesList.toArray(new Category[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
       /* for (Category cat: categories){
            System.out.println(cat.name);
            System.out.println(Arrays.toString(cat.subcategories));
        }*/
    }
}
