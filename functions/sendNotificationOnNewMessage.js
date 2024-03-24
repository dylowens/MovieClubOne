const functions = require("firebase-functions");
const admin = require("firebase-admin");


exports.sendNotificationOnNewMessage =
 functions.firestore
     .document("messages/{messageId}")
     .onCreate(async (snapshot, context) => {
       //       const messageData = snapshot.data();

       // Prepare the payload for notification
       const payload = {
         notification: {
           title: "You have a new message",
           //                body: messageData.text,
         },
       };

       try {
         // Fetch all users with an fcmToken
         const usersSnapshot = await admin.firestore()
             .collection("users").where("fcmToken", "!=", null).get();

         if (usersSnapshot.empty) {
           console.log("No users with FCM tokens found.");
           return;
         }

         // Extract the FCM tokens
         const tokens = usersSnapshot.docs.map((doc) =>
           doc.data().fcmToken).filter((token) => token != null);

         if (tokens.length > 0) {
           // Send a notification to all tokens
           const response = await admin.messaging()
               .sendToDevice(tokens, payload);
           console.log("Notification sent to all tokens", response);
         } else {
           console.log("No valid tokens found.");
         }
       } catch (error) {
         console.log("Error sending notification to all users:", error);
       }
     });
