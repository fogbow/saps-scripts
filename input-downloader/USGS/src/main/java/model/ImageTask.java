package model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

public class ImageTask implements Serializable {

    private String name;
    private String downloadLink;
    private String collectionTierName;

    public ImageTask(String collectionTierName, String name){
        this.name = name;
        this.collectionTierName = collectionTierName;
    }

    public String getName() {
        return name;
    }

    public String getDownloadLink() {
        return downloadLink;
    }

    public String getCollectionTierName() {
        return collectionTierName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDownloadLink(String downloadLink) {
        this.downloadLink = downloadLink;
    }

    public void setCollectionTierName(String collectionTierName) {
        this.collectionTierName = collectionTierName;
    }

    public String toString() {
        return "[" + name + ", " + downloadLink +  ", " + collectionTierName + "]";
    }

    public String formatedToString() {

        return "[ ImageName = " + name + " ]\n"
                + "[ DownloadLink = " + downloadLink + " ]\n"
                + "[ CollectionTierImageName = " + collectionTierName + " ]";
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ImageTask) {
            ImageTask other = (ImageTask) o;
            return getName().equals(other.getName())
                    && getDownloadLink().equals(other.getDownloadLink())
                    && getCollectionTierName().equals(other.getCollectionTierName());
        }
        return false;
    }}
