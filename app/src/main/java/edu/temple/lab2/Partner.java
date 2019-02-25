package edu.temple.lab2;

import com.google.android.gms.maps.model.LatLng;

public class Partner implements Comparable<Partner> {
   public String id;
   public double longitude;
   public double latitude;
   public LatLng location;
   public LatLng currentLocation;

    public Partner(String id, double longitude, double latitude, LatLng currentLocation){
        this.id = id;
        this.longitude = longitude;
        this.latitude = latitude;
        location = new LatLng(latitude,longitude);
        this.currentLocation = currentLocation;
    }

    public double getDistance(double lat, double lon){
        double distance = calculateDistance(currentLocation.latitude, currentLocation.longitude, latitude, longitude);
        return distance;
    }

    @Override
    public int compareTo(Partner p) {
        if(p.getDistance(currentLocation.latitude, currentLocation.longitude) > this.getDistance(currentLocation.latitude, currentLocation.longitude)){
            return -1;
        }
        else if(p.getDistance(currentLocation.latitude, currentLocation.longitude) < this.getDistance(currentLocation.latitude, currentLocation.longitude)){
            return 1;
        }
        return 0;
    }

    public String toJsonString(){
        String str = "{\"user\":\"" + id + "\",\"latitude\":\"" + latitude + "\",\"longitude\":\"" + longitude + "\"}";
        return str;
    }

    private double degreesToRadians(double degrees) {
        return degrees * Math.PI / 180;
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double earthRadiusKm = 6371.0;

        double dLat = degreesToRadians(lat2-lat1);
        double dLon = degreesToRadians(lon2-lon1);

        lat1 = degreesToRadians(lat1);
        lat2 = degreesToRadians(lat2);

        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.sin(dLon/2) * Math.sin(dLon/2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return earthRadiusKm * c;
    }
}
