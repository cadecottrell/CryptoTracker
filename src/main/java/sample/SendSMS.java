package sample;

//********************************************************************//
//                          SendSMS class                             //
//                                                                    //
//     This file's job is to:                                         //
//         - To send text alerts to user's phone                      //
//                                                                    //
//                                                                    //
//********************************************************************//



import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class SendSMS {

    public static final String ACCOUNT_SID = "AC7242b9100d73d0f0b9b13a57764bbd95";
    public static final String AUTH_TOKEN = "d618bf639e37dc33064197d6f36cd0cc";

    protected void sendTextAlert(AlertItem alertItem, String cross)
    {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        Message message = Message.creator(
                new PhoneNumber("+15122010373"),
                new PhoneNumber("+17205135462"),
                alertItem.getAlertName().getText() + " has just crossed " + cross + " your price target!"
        ).create();

    }


}
