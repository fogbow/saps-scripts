package model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;

import utils.PropertiesConstants;

public class ImageTask implements Serializable {

    private String name;
    private String downloadLink;
    private String dataSet;
    private String region;
    private String date;

    public ImageTask(String dataSet, String region, String date){
        this.dataSet = dataSet;
        this.region = region;
        this.date = date;
        this.name = buildImageName();
    }

    public String buildImageName() {
        String imageName = formatDataSet();
        imageName = imageName + region;
        imageName += formatDate();
        imageName += PropertiesConstants.DEFAULT_STATION;
        return imageName;
    }

    private String formatDataSet() {
        switch (dataSet) {
            case (PropertiesConstants.DATASET_LT5_TYPE):
                return PropertiesConstants.LANDSAT_5_PREFIX;
            case (PropertiesConstants.DATASET_LE7_TYPE):
                return PropertiesConstants.LANDSAT_7_PREFIX;
            case (PropertiesConstants.DATASET_LC8_TYPE):
                return PropertiesConstants.LANDSAT_8_PREFIX;
            default:
                return "";
        }
    }

    private String formatDate() {
        String[] dateArray = date.split("-");
        if(dateArray.length != 3){
            System.exit(7);
        }
        int year = Integer.parseInt(dateArray[0]);
        int month = Integer.parseInt(dateArray[1]);
        int day = Integer.parseInt(dateArray[2]);
        Calendar cal = new GregorianCalendar();
        cal.setLenient(false);
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1); // Calendar month is 0-based.
        cal.set(Calendar.DAY_OF_MONTH, day);
        String formattedDate = String.valueOf(year) + String.format("%03d",cal.get(Calendar.DAY_OF_YEAR));
        return formattedDate;
    }

    public String getName() {
        return name;
    }

    public String getDownloadLink() {
        return downloadLink;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDownloadLink(String downloadLink) {
        this.downloadLink = downloadLink;
    }

    public String toString() {
        return "[" + dataSet + region + date + name + ", " + downloadLink + "]";
    }

    public String getDataSet() {
        return dataSet;
    }

    public void setDataSet(String dataSet) {
        this.dataSet = dataSet;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public String formatedToString() {

        return  "[ DataSet = " + dataSet + " ]" +
                "[ Region = " + region + " ]" +
                "[ Date = " + date + " ]" +
                "[ ImageName = " + name + " ]\n"
                + "[ DownloadLink = " + downloadLink + " ]\n" ;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ImageTask) {
            ImageTask other = (ImageTask) o;
            return  getDataSet().equals(other.getDataSet()) &&
                    getRegion().equals(other.getRegion()) &&
                    getDate().equals(other.getDate()) &&
                    getName().equals(other.getName()) &&
                    getDownloadLink().equals(other.getDownloadLink());
        }
        return false;
    }}
