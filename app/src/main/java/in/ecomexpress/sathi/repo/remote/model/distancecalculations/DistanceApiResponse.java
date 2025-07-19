package in.ecomexpress.sathi.repo.remote.model.distancecalculations;

import com.fasterxml.jackson.annotation.JsonProperty;


import java.util.ArrayList;



 public class DistanceApiResponse{
    @JsonProperty("code")
    public String getCode() {
        return this.code; }
    public void setCode(String code) {
        this.code = code; }
    String code;
    @JsonProperty("sources")
    public ArrayList<Source> getSources() {
        return this.sources; }
    public void setSources(ArrayList<Source> sources) {
        this.sources = sources; }
    ArrayList<Source> sources;
    @JsonProperty("destinations")
    public ArrayList<Destination> getDestinations() {
        return this.destinations; }
    public void setDestinations(ArrayList<Destination> destinations) {
        this.destinations = destinations; }
    ArrayList<Destination> destinations;
    @JsonProperty("durations")
    public ArrayList<ArrayList<Double>> getDurations() {
        return this.durations; }
    public void setDurations(ArrayList<ArrayList<Double>> durations) {
        this.durations = durations; }
    ArrayList<ArrayList<Double>> durations;
    @JsonProperty("distances")
    public ArrayList<ArrayList<Double>> getDistances() {
        return this.distances; }
    public void setDistances(ArrayList<ArrayList<Double>> distances) {
        this.distances = distances; }
    ArrayList<ArrayList<Double>> distances;
}
class Destination {
    @JsonProperty("hint")
    public String getHint() {
        return this.hint; }
    public void setHint(String hint) {
        this.hint = hint; }
    String hint;
    @JsonProperty("distance")
    public double getDistance() {
        return this.distance; }
    public void setDistance(double distance) {
        this.distance = distance; }
    double distance;
    @JsonProperty("name")
    public String getName() {
        return this.name; }
    public void setName(String name) {
        this.name = name; }
    String name;
    @JsonProperty("location")
    public ArrayList<Double> getLocation() {
        return this.location; }
    public void setLocation(ArrayList<Double> location) {
        this.location = location; }
    ArrayList<Double> location;
}

 class Source{
    @JsonProperty("hint")
    public String getHint() {
        return this.hint; }
    public void setHint(String hint) {
        this.hint = hint; }
    String hint;
    @JsonProperty("distance")
    public double getDistance() {
        return this.distance; }
    public void setDistance(double distance) {
        this.distance = distance; }
    double distance;
    @JsonProperty("name")
    public String getName() {
        return this.name; }
    public void setName(String name) {
        this.name = name; }
    String name;
    @JsonProperty("location")
    public ArrayList<Double> getLocation() {
        return this.location; }
    public void setLocation(ArrayList<Double> location) {
        this.location = location; }
    ArrayList<Double> location;
}
