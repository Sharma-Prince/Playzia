package playzia.com.playzia;


public class DataModel {

    String title;
    String timedate;
    String winPrize;
    String perKill;
    String entryFee;
    String amountPay;
    String matchType;
    String matchVersion;
    String matchMap;
    String spots;
    String size;
    int totalPeopleJoined;
    String matchID;
    String imgURL;
    String banner;

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public DataModel(){

    }

    public String getAmountPay() {
        return amountPay;
    }

    public void setAmountPay(String amountPay) {
        this.amountPay = amountPay;
    }

    public int getTotalPeopleJoined() {
        return totalPeopleJoined;
    }

    public void setTotalPeopleJoined(int totalPeopleJoined) {
        this.totalPeopleJoined = totalPeopleJoined;
    }

    public String getMatchID() {
        return matchID;
    }

    public void setMatchID(String matchID) {
        this.matchID = matchID;
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTimedate() {
        return timedate;
    }

    public void setTimedate(String timedate) {
        this.timedate = timedate;
    }

    public String getWinPrize() {
        return winPrize;
    }

    public void setWinPrize(String winPrize) {
        this.winPrize = winPrize;
    }

    public String getPerKill() {
        return perKill;
    }

    public void setPerKill(String perKill) {
        this.perKill = perKill;
    }

    public String getEntryFee() {
        return entryFee;
    }

    public void setEntryFee(String entryFee) {
        this.entryFee = entryFee;
    }

    public String getMatchType() {
        return matchType;
    }

    public void setMatchType(String matchType) {
        this.matchType = matchType;
    }

    public String getMatchVersion() {
        return matchVersion;
    }

    public void setMatchVersion(String matchVersion) {
        this.matchVersion = matchVersion;
    }

    public String getMatchMap() {
        return matchMap;
    }

    public void setMatchMap(String matchMap) {
        this.matchMap = matchMap;
    }

    public String getSpots() {
        return spots;
    }

    public void setSpots(String spots) {
        this.spots = spots;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}