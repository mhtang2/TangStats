package com.uni.question;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Category {
    public static Category[] categories;
    String name;
    public String[] subcategories;

    public Category(String name) {
        this.name = name;
    }

    public static void loadCategories(String path) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(Category.class.getResourceAsStream(path)));
            ArrayList<Category> categoriesList = new ArrayList<>();
            categoriesList.add(null);
            String line;
            while ((line = br.readLine()) != null) {
                String[] split = line.split(" -> ", 2);
                Category cat = new Category(split[0]);
                String[] subcategories = split[1].split(", ");
                cat.subcategories = new String[subcategories.length + 1];
                cat.subcategories[0] = null;
                System.arraycopy(subcategories, 0, cat.subcategories, 1, subcategories.length);
                categoriesList.add(cat);
            }
            categories = categoriesList.toArray(new Category[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
