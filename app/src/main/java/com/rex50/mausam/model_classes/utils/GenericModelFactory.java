package com.rex50.mausam.model_classes.utils;

import com.rex50.mausam.R;
import com.rex50.mausam.model_classes.unsplash.photos.UnsplashPhotos;
import com.rex50.mausam.model_classes.unsplash.photos.User;

import java.util.ArrayList;
import java.util.List;

import static com.rex50.mausam.utils.Constants.RecyclerItemTypes.*;

public class GenericModelFactory {

    private GeneralTypeModel generalTypeModel;

    private UserTypeModel userTypeModel;

    private TextTypeModel textTypeModel;

    private ColorTypeModel colorTypeModel;

    private CollectionTypeModel collectionTypeModel;

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

    public static GenericModelFactory getCollectionTypeObject(String title, String desc, boolean hasMore, List<UnsplashPhotos> photosList){
        GenericModelFactory generalTypeModel = new GenericModelFactory();
        generalTypeModel.setTitle(title);
        generalTypeModel.setDesc(desc);
        generalTypeModel.setHasMore(hasMore);
        generalTypeModel.setItemType(COLLECTION_TYPE);
        generalTypeModel.setItemLayout(R.layout.collection_type_cell);
        generalTypeModel.setCollectionTypeModel(new CollectionTypeModel(photosList));
        return generalTypeModel;
    }

    public static GenericModelFactory getTextTypeObject(String title, String desc, boolean hasMore, List<String> tagsList){
        GenericModelFactory textTypeModel = new GenericModelFactory();
        textTypeModel.setTitle(title);
        textTypeModel.setDesc(desc);
        textTypeModel.setHasMore(hasMore);
        textTypeModel.setItemType(TEXT_TYPE);
        textTypeModel.setItemLayout(R.layout.text_type_cell);
        textTypeModel.setTextTypeModel(new TextTypeModel(tagsList));
        return textTypeModel;
    }

    public static GenericModelFactory getColorTypeObject(String title, String desc, boolean hasMore, List<ColorModel> colorsList){
        GenericModelFactory textTypeModel = new GenericModelFactory();
        textTypeModel.setTitle(title);
        textTypeModel.setDesc(desc);
        textTypeModel.setHasMore(hasMore);
        textTypeModel.setItemType(COLOR_TYPE);
        textTypeModel.setItemLayout(R.layout.color_type_cell);
        textTypeModel.setColorTypeModel(new ColorTypeModel(colorsList));
        return textTypeModel;
    }

    public static GenericModelFactory getFavouritePhotographerTypeObject(String title, String desc, List<UnsplashPhotos> photosList){
        GenericModelFactory favouriteModel = new GenericModelFactory();
        favouriteModel.setTitle(title);
        favouriteModel.setDesc(desc);
        favouriteModel.setItemType(GENERAL_TYPE);
        favouriteModel.setViewType(FAVOURITE_PHOTOGRAPHER_PHOTOS_TYPE);
        favouriteModel.setItemLayout(R.layout.general_type_cell);
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

        //TODO : use Collection instead of UnsplashPhotos later

        private List<UnsplashPhotos> photosList = new ArrayList<>();

        private CollectionTypeModel(){}

        CollectionTypeModel(List<UnsplashPhotos> photosList){
            setPhotosList(photosList);
        }

        private void setPhotosList(List<UnsplashPhotos> photosList) {
            this.photosList = photosList;
        }

        public List<UnsplashPhotos> getPhotosList() {
            return photosList;
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

    public static class TextTypeModel {

        private List<String> tagsList = new ArrayList<>();

        private TextTypeModel(){}

        private TextTypeModel(List<String> usersList){
            setTagsList(usersList);
        }

        private void setTagsList(List<String> tags) {
            this.tagsList = tags;
        }

        public List<String> getTagsList() {
            return tagsList;
        }

    }

    public static class ColorTypeModel {

        private List<ColorModel> colorsList = new ArrayList<>();

        private ColorTypeModel(){}

        private ColorTypeModel(List<ColorModel> usersList){
            setColorsList(usersList);
        }

        private void setColorsList(List<ColorModel> colorsList) {
            this.colorsList = colorsList;
        }

        public List<ColorModel> getColorsList() {
            return colorsList;
        }

    }

    public class ColorModel{
        String colorName;
        int colorCode;

        private ColorModel(){}

        public ColorModel(String colorName, int colorCode){
            setColorName(colorName);
            setColorCode(colorCode);
        }

        private void setColorCode(int colorCode) {
            this.colorCode = colorCode;
        }

        private void setColorName(String colorName) {
            this.colorName = colorName;
        }

        public int getColorCode() {
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

    private void setTextTypeModel(TextTypeModel textTypeModel) {
        this.textTypeModel = textTypeModel;
    }

    public TextTypeModel getTextTypeModel() {
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
