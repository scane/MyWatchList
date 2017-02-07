package com.scanba.mywatchlist.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.SQLException;

@DatabaseTable(tableName = "movies")
public class Movie implements Parcelable {

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField
    private String theMovieDbId;
    @DatabaseField
    private String title;
    @DatabaseField
    private String releaseDate;
    @DatabaseField
    private String posterPath;
    @DatabaseField
    private int rating;
    private String description;
    private String genres;

    public Movie() {

    }

    protected Movie(Parcel in) {
        theMovieDbId = in.readString();
        title = in.readString();
        releaseDate = in.readString();
        posterPath = in.readString();
        rating = in.readInt();
        description = in.readString();
        genres = in.readString();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public String getTheMovieDbId() {
        return theMovieDbId;
    }

    public void setTheMovieDbId(String theMovieDbId) {
        this.theMovieDbId = theMovieDbId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(theMovieDbId);
        dest.writeString(title);
        dest.writeString(releaseDate);
        dest.writeString(posterPath);
        dest.writeInt(rating);
        dest.writeString(description);
        dest.writeString(genres);
    }

    //Checks if record exists in database
    public static boolean exists(String theMovieDbId, Dao<Movie, Integer> movieDao) {
        QueryBuilder<Movie, Integer> queryBuilder = movieDao.queryBuilder();
        try {
            long count = queryBuilder.where().eq("theMovieDBId", theMovieDbId).countOf();
            if (count > 0)
                return true;
            else
                return false;

        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }
}
