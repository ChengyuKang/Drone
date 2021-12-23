package uk.ac.ed.inf.parsers;

/**
 * @author KangCY
 * @Description What three words is an object work as a receiver of data from server
 *              it maps each what3words string with a coordinate
 * @create 2021-12-01 13:08
 */
public class WhatThreeWords {
    private String country;
    private Square square;
    private String nearestPlace;
    private Coordinates coordinates;           // the coordinate of the location
    private String words;                      // the what three words string
    private  String language;
    private String map;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Square getSquare() {
        return square;
    }

    public void setSquare(Square square) {
        this.square = square;
    }

    public String getNearestPlace() {
        return nearestPlace;
    }

    public void setNearestPlace(String nearestPlace) {
        this.nearestPlace = nearestPlace;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public String getWords() {
        return words;
    }

    public void setWords(String words) {
        this.words = words;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getMap() {
        return map;
    }

    public void setMap(String map) {
        this.map = map;
    }

    public static class Square{
        Southwest southwest;
        Northeast northeast;

        public static class Southwest{
            Double lng;
            Double lat;

            @Override
            public String toString() {
                return "Southwest{" +
                        "lng=" + lng +
                        ", lat=" + lat +
                        '}';
            }
        }

        public static class Northeast{
            Double lng;
            Double lat;

            @Override
            public String toString() {
                return "Northeast{" +
                        "lng=" + lng +
                        ", lat=" + lat +
                        '}';
            }
        }

        @Override
        public String toString() {
            return "Square{" +
                    "southwest=" + southwest +
                    ", northeast=" + northeast +
                    '}';
        }
    }

    public static class Coordinates{
        Double lng;
        Double lat;

        @Override
        public String toString() {
            return "Coordinates{" +
                    "lng=" + lng +
                    ", lat=" + lat +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "WhatThreeWords{" +
                "country='" + country + '\'' +
                ", square=" + square +
                ", nearestPlace='" + nearestPlace + '\'' +
                ", coordinates=" + coordinates +
                ", words='" + words + '\'' +
                ", language='" + language + '\'' +
                ", map='" + map + '\'' +
                '}';
    }
}
