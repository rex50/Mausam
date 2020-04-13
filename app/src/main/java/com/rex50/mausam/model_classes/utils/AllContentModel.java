package com.rex50.mausam.model_classes.utils;

import com.rex50.mausam.model_classes.unsplash.photos.UnsplashPhotos;
import com.rex50.mausam.model_classes.unsplash.photos.Urls;

import java.util.ArrayList;
import java.util.List;

public class AllContentModel {

    private List<GenericModelFactory> models;

    private List<String> types;

    public AllContentModel(){
        models = new ArrayList<>();
        types = new ArrayList<>();
    }

    public void addModel(String type, GenericModelFactory model){
        this.models.add(model);
        this.types.add(type);
    }

    public void addModel(int pos, String type, GenericModelFactory model){
        this.models.add(pos, model);
        this.types.add(pos, type);
    }

    public void addAllModel(List<GenericModelFactory> list){
        models.addAll(list);
    }

    public GenericModelFactory getModel(int pos){
        return /*models.size() > pos ?*/ models.get(pos) /*: getDummyModel(pos)*/;
    }

    public String getType(int pos){
        return types.get(pos);
    }

    public int size(){
        return models == null ? 0 : models.size();
    }



    /*private static GenericModelFactory getDummyModel(int type){
        if(type%6 == 0){
            return GenericModelFactory.getFavouritePhotographerTypeObject("Favourite Title " + (type + 1), "Description " + (type + 1), getDummyPhotos(10));
        }else if(type%5 == 0){
            return GenericModelFactory.getColorTypeObject("Color Title " + (type + 1), "Description " + (type + 1), false, getDummyTags(10));
        }else if(type%4 == 0){
            return GenericModelFactory.getTextTypeObject("Tag Title " + (type + 1), "Description " + (type + 1), false, getDummyTags(10));
        }else if(type%3 == 0){
            return GenericModelFactory.getGeneralTypeObject("General Title " + (type + 1), "Description " + (type + 1), true, getDummyPhotos(10));
        }else if(type%2 == 0){
            return GenericModelFactory.getUserTypeObject("User Title" + (type + 1), "Description " + (type + 1), true, getDummyPhotos(10));
        }else {
            return GenericModelFactory.getCollectionTypeObject("Collection Title" + (type + 1), "Description " + (type + 1), true, getDummyPhotos(10));
        }
    }*/

    public static List<GenericModelFactory> getDummyModelsList(int size){
        if(size > 0){
            List<GenericModelFactory> list = new ArrayList<>();
            /*for (int i = 0; i < size; i++) {
                list.add(getDummyModel(i));
            }*/
            return list;
        }else
            return new ArrayList<>();
    }

    private static List<UnsplashPhotos> getDummyPhotos(int size){
        if(size > 0){
            List<UnsplashPhotos> list = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                UnsplashPhotos photos = new UnsplashPhotos();
                Urls urls = new Urls();
                urls.setSmall("https://images.unsplash.com/photo-1586126928376-eaf2b1278093?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=634&q=80");
                photos.setUrls(urls);
                list.add(photos);
            }
            return list;
        }else
            return new ArrayList<>();
    }

    private static List<String> getDummyTags(int size){
        if(size > 0){
            List<String> list = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                list.add("Tag "+ i);
            }
            return list;
        }else
            return new ArrayList<>();
    }

}
