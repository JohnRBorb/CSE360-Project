package application;

public class ScoreParam {
    private String domain;
    private Double weight;
    
    public ScoreParam(String domain, Double weight) {
        this.domain = domain; 
        this.weight = weight;
    }
    
    public String getDomain() { return domain; }
    public Double getWeight() { return weight; }
    public void setWeight(Double w) { this.weight = w; }
    
}
