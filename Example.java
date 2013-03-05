/*
  =======================================================================
   File:     Example.java
   Created:  2010-01-10
   Author:   MessageBird B.V.
   Version:  v1.1 - 2010-07-09

   For more information visit http://www.messagebird.com/content/api
   This class requires that you have JDK 5 or higher installed.
  ========================================================================
*/

import com.messagebird.MessageBirdApi;

/**
 * This is en example script to show how to use our API with Java.
 */
public class Example {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        MessageBirdApi smsApi = new MessageBirdApi();

        smsApi.authenticate("username", "password");        //authenticate with MessageBird SMS API
        smsApi.setSender("YourSender");                     //set the name or number from where the message come from
        smsApi.addDestination("31600000001");               //add number to destination list, this function can be called multiple times for more receivers
        smsApi.setReference("123456789");                   //your unique reference
        //smsApi.setTimestamp(2012, 2, 27, 11, 30);         //only use if you want to schedule message
        smsApi.send("This is a test message");              //send the message to the receiver(s)

        System.out.println(smsApi.getResponseCode());       //print out the response code
        System.out.println(smsApi.getResponseMessage());    //print out the response message
        System.out.println(smsApi.getCreditBalance());      //print out the credit balance
    }

}
