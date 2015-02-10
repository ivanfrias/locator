package locator.withus.pt.domain;

/**
 * Created by ivanfrias on 31/01/15.
 */
public enum CommunicationStatus {
    OK(1),
    NOK(0);

    private int statusCode;

    CommunicationStatus(int statusCode){
        this.statusCode = statusCode;
    }

    public int getStatusCode(){
        return this.statusCode;
    }
}
