package me.paulbgd.mmgames.utils;

public interface Callback<T> {
   public void onLoad(T data);

   public void onFailure();
}