package com.rex50.mausam.model_classes.utils;

import com.rex50.mausam.R;
import com.rex50.mausam.model_classes.unsplash.collection.Collections;
import com.rex50.mausam.model_classes.unsplash.collection.Tag;
import com.rex50.mausam.model_classes.unsplash.photos.UnsplashPhotos;
import com.rex50.mausam.model_classes.unsplash.photos.User;

import java.util.ArrayList;
import java.util.List;

import static com.rex50.mausam.utils.Constants.RecyclerItemTypes.*;

public class GenericModelFactory {

    private GeneralTypeModel generalTypeModel;

    private UserTypeModel userTypeModel;

    private TagTypeModel textTypeModel;

    private ColorTypeModel colorTypeModel;

    private CollectionTypeModel collectionTypeModel;

    private CategoryTypeModel categoryTypeModel;

    private FavouritePhotographerTypeModel favouritePhotographerTypeModel;

    private int viewType = ITEM_CATEGORY_TYPE;

    private int viewLayout = R.layout.item_category;

    private int itemType = GENERAL_TYPE;

    private int itemLayout = R.layout.general_type_cell;

    private String title = "", desc = "";

    private boolean hasMore = true;

    //private int endImage = R.drawable.banner_bg_1;

    private GenericModelFactory(){}

    public static GenericModelFactory getGeneralTypeObject(String title, String desc, boolean hasMore, List<UnsplashPhotos> photosList){
        GenericModelFactory generalTypeModel = new GenericModelFactory();
        generalTypeModel.setTitle(title);
        generalTypeModel.setDesc(desc);
        generalTypeModel.setHasMore(hasMore);
        generalTypeModel.setItemType(GENERAL_TYPE);
        generalTypeModel.setItemLayout(R.layout.general_type_cell);
        generalTypeModel.setGeneralTypeModel(new GeneralTypeModel(photosList));
        return generalTypeModel;
    }

    public static GenericModelFactory getUserTypeObject(String title, String desc, boolean hasMore, List<User> usersList){
        GenericModelFactory generalTypeModel = new GenericModelFactory();
        generalTypeModel.setTitle(title);
        generalTypeModel.setDesc(desc);
        generalTypeModel.setHasMore(hasMore);
        generalTypeModel.setItemType(USER_TYPE);
        generalTypeModel.setItemLayout(R.layout.user_type_cell);
        generalTypeModel.setUserTypeModel(new UserTypeModel(usersList));
        return generalTypeModel;
    }

    public static GenericModelFactory getCollectionTypeObject(String title, String desc, boolean hasMore, List<Collections> collectionsList){
        GenericModelFactory generalTypeModel = new GenericModelFactory();
        generalTypeModel.setTitle(title);
        generalTypeModel.setDesc(desc);
        generalTypeModel.setHasMore(hasMore);
        generalTypeModel.setItemType(COLLECTION_TYPE);
        generalTypeModel.setItemLayout(R.layout.collection_type_cell);
        generalTypeModel.setCollectionTypeModel(new CollectionTypeModel(collectionsList));
        return generalTypeModel;
    }

    public static GenericModelFactory getTagTypeObject(String title, String desc, boolean hasMore, List<Tag> tagsList, boolean shuffleList){
        GenericModelFactory textTypeModel = new GenericModelFactory();
        textTypeModel.setTitle(title);
        textTypeModel.setDesc(desc);
        textTypeModel.setHasMore(hasMore);
        textTypeModel.setItemType(TAG_TYPE);
        textTypeModel.setItemLayout(R.layout.tag_type_cell);
        textTypeModel.setTagTypeModel(new TagTypeModel(tagsList, shuffleList));
        return textTypeModel;
    }

    public static GenericModelFactory getColorTypeObject(String title, String desc, boolean hasMore, List<ColorModel> colorsList, boolean shuffleList){
        GenericModelFactory textTypeModel = new GenericModelFactory();
        textTypeModel.setTitle(title);
        textTypeModel.setDesc(desc);
        textTypeModel.setHasMore(hasMore);
        textTypeModel.setItemType(COLOR_TYPE);
        textTypeModel.setItemLayout(R.layout.color_type_cell);
        textTypeModel.setColorTypeModel(new ColorTypeModel(colorsList, shuffleList));
        return textTypeModel;
    }

    public static GenericModelFactory getCategoryTypeObject(String title, String desc, boolean hasMore, List<String> categories, boolean shuffleList){
        GenericModelFactory textTypeModel = new GenericModelFactory();
        textTypeModel.setTitle(title);
        textTypeModel.setDesc(desc);
        textTypeModel.setHasMore(hasMore);
        textTypeModel.setItemType(CATEGORY_TYPE);
        textTypeModel.setItemLayout(R.layout.category_type_cell);
        textTypeModel.setCategoryTypeModel(new CategoryTypeModel(categories, shuffleList));
        return textTypeModel;
    }

    public static GenericModelFactory getFavouritePhotographerTypeObject(String title, String desc, List<UnsplashPhotos> photosList){
        GenericModelFactory favouriteModel = new GenericModelFactory();
        favouriteModel.setTitle(title);
        favouriteModel.setDesc(desc);
        favouriteModel.setHasMore(false);
        favouriteModel.setItemType(FAV_PHOTOGRAPHER_PHOTOS_TYPE);
        favouriteModel.setViewType(FAVOURITE_PHOTOGRAPHER_PHOTOS_CATEGORY_TYPE);
        favouriteModel.setItemLayout(R.layout.fav_photograher_photo_type_cell);
        favouriteModel.setFavouritePhotographerTypeModel(new FavouritePhotographerTypeModel(photosList));
        return favouriteModel;
    }

    /*public static GenericModelFactory getEndImageTypeObject(int res){
        GenericModelFactory modelFactory = new GenericModelFactory();
        modelFactory.setEndImage(res);
        modelFactory.setViewType(END_IMAGE);
        modelFactory.setViewLayout(R.layout.general_type_cell);
        return modelFactory;
    }*/

    public static class GeneralTypeModel{

        private List<UnsplashPhotos> photosList = new ArrayList<>();

        private GeneralTypeModel(){}

        GeneralTypeModel(List<UnsplashPhotos> photosList){
            setPhotosList(photosList);

        }

        private void setPhotosList(List<UnsplashPhotos> photosList) {
            this.photosList = photosList;
        }

        public List<UnsplashPhotos> getPhotosList() {
            return photosList;
        }

    }

    public static class FavouritePhotographerTypeModel{

        private List<UnsplashPhotos> photosList = new ArrayList<>();

        private FavouritePhotographerTypeModel(){}

        FavouritePhotographerTypeModel(List<UnsplashPhotos> photosList){
            setPhotosList(photosList);
        }

        private void setPhotosList(List<UnsplashPhotos> photosList) {
            this.photosList = photosList;
        }

        public List<UnsplashPhotos> getPhotosList() {
            return photosList;
        }

    }

    public static class CollectionTypeModel{

        private List<Collections> collections = new ArrayList<>();

        private CollectionTypeModel(){}

        CollectionTypeModel(List<Collections> collectionsList){
            setCollections(collectionsList);
        }

        public List<Collections> getCollections() {
            return collections;
        }

        public void setCollections(List<Collections> collections) {
            this.collections = collections;
        }
    }

    public static class UserTypeModel{

        private List<User> usersList = new ArrayList<>();

        private UserTypeModel(){}

        private UserTypeModel(List<User> usersList){
            setUsersList(usersList);
        }

        private void setUsersList(List<User> usersList) {
            this.usersList = usersList;
        }

        public List<User> getUsersList() {
            return usersList;
        }

    }

    public static class TagTypeModel {

        private List<Tag> tagsList = new ArrayList<>();

        private TagTypeModel(){}

        private TagTypeModel(List<Tag> tagsList, boolean shuffle){
            if(shuffle){
                java.util.Collections.shuffle(tagsList);
            }
            setTagsList(tagsList);
        }

        private void setTagsList(List<Tag> tags) {
            this.tagsList = tags;
        }

        public List<Tag> getTagsList() {
            return tagsList;
        }

    }

    public static class CategoryTypeModel {

        private List<String> categories = new ArrayList<>();

        private CategoryTypeModel(){}

        private CategoryTypeModel(List<String> categories, boolean shuffle){
            if(shuffle){
                java.util.Collections.shuffle(categories);
            }
            setCategories(categories);
        }

        private void setCategories(List<String> categories) {
            this.categories = categories;
        }

        public List<String> getCategories() {
            return categories;
        }

    }

    public static class ColorTypeModel {

        private List<ColorModel> colorsList = new ArrayList<>();

        private ColorTypeModel(){}

        private ColorTypeModel(List<ColorModel> colorsList, boolean shuffle){
            if(shuffle){
                java.util.Collections.shuffle(colorsList);
            }
            setColorsList(colorsList);
        }

        public static List<ColorModel> createModelFromStringList(List<String> colors){
            List<ColorModel> list = new ArrayList<>();

            for(String color : colors){
                try {
                    String[] arr = color.split("_");
                    list.add(new ColorModel(arr[0], arr[1]));
                }catch (NullPointerException e){
                    throw new IllegalArgumentException("Invalid color string \"" + color + "\" : Correct format is \"[@colorName]_#[@colorCode]");
                }
            }

            return list;
        }

        private void setColorsList(List<ColorModel> colorsList) {
            this.colorsList = colorsList;
        }

        public List<ColorModel> getColorsList() {
            return colorsList;
        }

    }

    public static class ColorModel{
        String colorName;
        String colorCode;

        private ColorModel(){}

        public ColorModel(String colorName, String colorCode){
            setColorName(colorName);
            setColorCode(colorCode);
        }

        private void setColorCode(String colorCode) {
            this.colorCode = colorCode;
        }

        private void setColorName(String colorName) {
            this.colorName = colorName;
        }

        public String getColorCode() {
            return colorCode;
        }

        public String getColorName() {
            return colorName;
        }
    }


    private void setGeneralTypeModel(GeneralTypeModel generalTypeClass) {
        this.generalTypeModel = generalTypeClass;
    }

    public GeneralTypeModel getGeneralTypeModel() {
        return generalTypeModel;
    }

    private void setUserTypeModel(UserTypeModel userTypeModel) {
        this.userTypeModel = userTypeModel;
    }

    public UserTypeModel getUserTypeModel() {
        return userTypeModel;
    }

    private void setTagTypeModel(TagTypeModel textTypeModel) {
        this.textTypeModel = textTypeModel;
    }

    public TagTypeModel getTagTypeModel() {
        return textTypeModel;
    }

    private void setCollectionTypeModel(CollectionTypeModel collectionTypeModel) {
        this.collectionTypeModel = collectionTypeModel;
    }

    public CollectionTypeModel getCollectionTypeModel() {
        return collectionTypeModel;
    }

    private void setColorTypeModel(ColorTypeModel colorTypeModel) {
        this.colorTypeModel = colorTypeModel;
    }

    public ColorTypeModel getColorTypeModel() {
        return colorTypeModel;
    }

    private void setCategoryTypeModel(CategoryTypeModel categoryTypeModel) {
        this.categoryTypeModel = categoryTypeModel;
    }

    public CategoryTypeModel getCategoryTypeModel(){
        return categoryTypeModel;
    }

    private void setFavouritePhotographerTypeModel(FavouritePhotographerTypeModel favouritePhotographerTypeModel) {
        this.favouritePhotographerTypeModel = favouritePhotographerTypeModel;
    }

    public FavouritePhotographerTypeModel getFavouritePhotographerTypeModel() {
        return favouritePhotographerTypeModel;
    }

    private void setTitle(String title){
        this.title = title;
    }

    private void setDesc(String desc){
        this.desc = desc;
    }

    private void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    private void setViewType(int type) {
        this.viewType = type;
    }

    private void setViewLayout(int res) {
        this.viewLayout = res;
    }

    private void setItemType(int itemType) {
        this.itemType = itemType;
    }

    private void setItemLayout(int itemLayout){
        this.itemLayout = itemLayout;
    }

    public String getDesc() {
        return desc;
    }

    public String getTitle() {
        return title;
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public int getViewType() {
        return viewType;
    }

    public int getViewLayout(){
        return viewLayout;
    }

    public int getItemType() {
        return itemType;
    }

    public int getItemLayout(){
        return itemLayout;
    }

    /*private void setEndImage(int imageRes){
        this.endImage = imageRes;
    }

    public int getEndImage(){
        return endImage;
    }*/

}
