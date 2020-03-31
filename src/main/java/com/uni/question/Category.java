package com.uni.question;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

public class Category {
    public static Category[] categories;
    public static String[] names;
    /**
     * store indicies of major cats
     **/
    public static HashSet<Integer> majorCats = new HashSet<>();
    String name;
    public String[] subcategories;

    public Category(String name) {
        this.name = name;
    }

    public static void loadCategories(String path) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(Category.class.getResourceAsStream(path)));
            ArrayList<Category> categoriesList = new ArrayList<>();
            ArrayList<String> nameList = new ArrayList<>();
            categoriesList.add(null);
            String line;
            while ((line = br.readLine()) != null) {
                String[] split = line.split(" -> ", 2);
                Category cat = new Category(split[0]);
                majorCats.add(nameList.size());
                nameList.add(split[0]);
                String[] subcategories = split[1].split(", ");
                Collections.addAll(nameList, subcategories);
                cat.subcategories = new String[subcategories.length + 1];
                cat.subcategories[0] = null;
                System.arraycopy(subcategories, 0, cat.subcategories, 1, subcategories.length);
                categoriesList.add(cat);
            }
            categories = categoriesList.toArray(new Category[0]);
            names = nameList.toArray(new String[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
