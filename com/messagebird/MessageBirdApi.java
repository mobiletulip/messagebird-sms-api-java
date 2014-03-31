/*
  =======================================================================
   File:     MessageBirdApi.java
   Created:  2010-01-10
   Author:   MessageBird B.V.
   Version:  v1.1 - 2010-07-09

   For more information visit http://www.messagebird.com/content/api
   This class requires that you have JDK 5 or higher installed.
  ========================================================================
*/

package com.messagebird;

// Import all the needed libraries.
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.events.StartElement;

/**
 * MessageBirdApi Class.
 */
public class MessageBirdApi {

    /**
     * Message request parameters
     */
    private String username;
    private String password;
    private String responseType;
    private String sender;
    private ArrayList<String> destinations;
    private String timestamp;
    private String reference;
    private String responseMessage;
    private String responseCode;

    /**
     * Creates an instance of the MessageBird API
     */
    public MessageBirdApi() {
        this.destinations = new ArrayList<String>();
        this.responseType = "XML";
    }

    /**
     * Send message
     * This function needs to be called at the end
     *
     * @param message, the message that need to be send
     * @throws UnsupportedEncodingException
     */
    public void send(String message) throws UnsupportedEncodingException {
        StringBuffer postData = new StringBuffer();

        // Now concatenate the request parameters
        // The request paramters need to be url-encoded
        postData.append("username=" + URLEncoder.encode(this.username, "UTF-8"));
        postData.append("&password=" + URLEncoder.encode(this.password, "UTF-8"));
        postData.append("&sender=" + URLEncoder.encode(this.sender, "UTF-8"));
        postData.append("&body=" + URLEncoder.encode(message, "UTF-8"));
        postData.append("&responsetype=" + URLEncoder.encode(this.responseType, "UTF-8"));

        // Add/Loop through all the destinations and make sure that they are comma seperated
        postData.append("&destination=");
        for (int i = 0; i < this.destinations.size(); i++) {
            if (i > 0) {
                postData.append("%2C");
            }
            postData.append(URLEncoder.encode((String) this.destinations.get(i), "UTF-8"));
        }

        // If there is a timestamp set, add it to the parameters
        if (this.timestamp != null) {
            postData.append("&timestamp=" + URLEncoder.encode(this.timestamp, "UTF-8"));
        }

        // If there is a reference set, add it to the parameters
        if (this.reference != null) {
            postData.append("&reference=" + URLEncoder.encode(this.reference, "UTF-8"));
        }

        try {
            URL url = new URL("http://api.messagebird.com/api/sms");
            URLConnection conn = url.openConnection();

            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(postData.toString());
            wr.flush();

            // Get the response
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            try {
                XMLInputFactory inputFactory = XMLInputFactory.newInstance();
                XMLEventReader reader = inputFactory.createXMLEventReader(rd);
                while (reader.hasNext()) {
                    XMLEvent event = reader.nextEvent();
                    if (event.isStartElement()) {
                        StartElement element = (StartElement) event;
                        if (element.getName().toString().equals("responseCode")) {
                            event = reader.nextEvent();
                            this.responseCode = event.asCharacters().getData();
                            continue;
                        } else if (element.getName().toString().equals("responseMessage")) {
                            event = reader.nextEvent();
                            this.responseMessage = event.asCharacters().getData();
                            continue;
                        }
                    }
                }
            } catch (Exception e) {}

            wr.close();
            rd.close();
        } catch (Exception e) {}
    }

    /**
     * Set username and password for authentication on the SMScity Gateway
     *
     * @param username
     * @param password
     */
    public void authenticate(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Add receiver to destination list, up to 50k destinations
     *
     * @param destination, look Technical information document for specs
     */
    public void addDestination(String destination) {
        this.destinations.add(destination);
    }

    /**
     * Set the sender for the message
     *
     * @param sender
     */
    public void setSender(String sender) {
        this.sender = sender;
    }

    /**
     * Set the reference
     *
     * @param reference
     */
    public void setReference(String reference) {
        this.reference = reference;
    }

    /**
     * Set the timestamp, for scheduling the message
     *
     * @param Calendar c
     */
    public void setTimestamp(Calendar c) {
        SimpleDateFormat sdfTimezone = new SimpleDateFormat("yyyyMMddHHmm");
        sdfTimezone.setCalendar(c);
        Date date = new Date(c.getTimeInMillis());
        sdfTimezone.setTimeZone(TimeZone.getTimeZone("Europe/Amsterdam"));
        this.timestamp = sdfTimezone.format(date);
    }

    /**
     * Set the timestamp, for scheduling the message
     *
     * @param year
     * @param month
     * @param day
     * @param hour
     * @param minute
     * @param timezone
     * @throws ParseException
     */
    public void setTimestamp(int year, int month, int day, int hour, int minute, TimeZone timezone) throws ParseException {

        Calendar c = Calendar.getInstance();
        c.set(year, month - 1, day, hour, minute);
        if (timezone != null) {
            c.setTimeZone(timezone);
        }
        this.setTimestamp(c);
    }

    /**
     * Set the timestamp, for scheduling the message
     *
     * @param year
     * @param month
     * @param day
     * @param hour
     * @param minute
     * @throws ParseException
     */
    public void setTimestamp(int year, int month, int day, int hour, int minute) throws ParseException {
        this.setTimestamp(year, month, day, hour, minute, null);
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

    /**
     * Will return the response code which is returned after sending the the message.
     *
     * @return responseCode
     */
    public String getResponseCode() {
        return this.responseCode;
    }

    /**
     * Will return the response message.
     *
     * @return responseMessage
     */
    public String getResponseMessage() {
        return this.responseMessage;
    }
}
