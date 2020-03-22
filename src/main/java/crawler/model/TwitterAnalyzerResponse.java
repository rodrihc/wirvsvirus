package crawler.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;

public class TwitterAnalyzerResponse implements Serializable {

    @JsonProperty("Key")
    @XmlElement(name = "Key")
    private String key;

    @JsonProperty("Value")
    @XmlElement(name = "Value")
    private String value;

    @JsonProperty("Twitter")
    @XmlElement(name = "Twitter")
    private String twitter;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }
}
