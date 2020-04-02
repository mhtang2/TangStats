package com.uni.question;

import org.apache.poi.ss.formula.functions.T;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

public class Category {
    public static ArrayList<Category> categories;
    public static ArrayList<String> names;
    /**
     * store indicies of major cats
     **/
    private String name;
    public ArrayList<String> subcategories = new ArrayList<>();
    public String returnSubcat;

    public Category(String name) {
        this.name = name;
    }

    public static void loadCategories(String path) {
        try {
            categories = new ArrayList<>();
            names = new ArrayList<>();
            BufferedReader br = new BufferedReader(new InputStreamReader(Category.class.getResourceAsStream(path)));
            categories.add(null);
            String line;
            while ((line = br.readLine()) != null) {
                String[] split = line.split(" -> ", 2);
                Category cat = new Category(split[0]);
                names.add(split[0]);
                String[] subcategories = split[1].split(", ");
                cat.subcategories.add(null);
                Collections.addAll(cat.subcategories, subcategories);
                Collections.addAll(names, subcategories);
                categories.add(cat);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String pure(String name) {
        return name.replaceAll("[^A-Za-z0-9]", "").toLowerCase(Locale.US);
    }

    private static int indexOfCat(ArrayList<?> list, String s) {
        int idx = 0;//First value null
        for (Object o : list) {
            if (o == null) {
                idx++;
                continue;
            }
            if (pure(o.toString()).equals(s)) return idx;
            idx++;
        }
        return idx;
    }

    public static Category addCategory(String cat, String subcat) {
        String pureCat = pure(cat);
        int idx = indexOfCat(categories, pureCat);
        //Found
        if (idx < categories.size()) {
            ArrayList<String> subcategories = categories.get(idx).subcategories;
            if (subcat != null && subcat.length() > 0) {
                String puresubcat = pure(subcat);
                int idx2 = indexOfCat(subcategories, puresubcat);
                if (idx2 == subcategories.size()) {
                    subcategories.add(subcat);
                    names.add(1 + names.indexOf(cat), subcat);
                }
                categories.get(idx).returnSubcat = subcategories.get(idx2);
            }
        } else {
            Category newcat = new Category(cat);
            categories.add(newcat);
            names.add(cat);
            if (subcat != null) {
                newcat.subcategories.add(null);
                newcat.subcategories.add(subcat);
                names.add(subcat);
            }
            newcat.returnSubcat = subcat;
        }
        return categories.get(idx);
    }

    @Override
    public String toString() {
        return name;
    }
}
