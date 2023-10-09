package com.oustme.oustsdk.layoutFour.data;

public class TabSection
{
    private String image;

    private String indexName;

    private boolean showTodo;

    private String type;

    public void setImage(String image){
        this.image = image;
    }
    public String getImage(){
        return this.image;
    }
    public void setIndexName(String indexName){
        this.indexName = indexName;
    }
    public String getIndexName(){
        return this.indexName;
    }
    public void setShowTodo(boolean showTodo){
        this.showTodo = showTodo;
    }
    public boolean getShowTodo(){
        return this.showTodo;
    }
    public void setType(String type){
        this.type = type;
    }
    public String getType(){
        return this.type;
    }
}